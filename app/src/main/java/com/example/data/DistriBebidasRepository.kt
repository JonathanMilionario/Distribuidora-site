package com.example.data

import androidx.room.withTransaction
import com.example.data.entity.CashRegisterEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleOrderEntity
import com.example.data.entity.SaleOrderItemEntity
import com.example.data.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

data class CartItem(
    val product: ProductEntity,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double get() = quantity * unitPrice
    val totalCost: Double get() = quantity * product.costPrice
}

class DistriBebidasRepository(private val database: DistriBebidasDatabase) {
    private val productDao = database.productDao()
    private val clientDao = database.clientDao()
    private val saleOrderDao = database.saleOrderDao()
    private val stockMovementDao = database.stockMovementDao()
    private val cashRegisterDao = database.cashRegisterDao()

    // Products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()

    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategory(category)

    suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

    suspend fun adjustStock(
        product: ProductEntity,
        deltaQuantity: Int,
        movementType: String, // "ENTRADA", "SAIDA", "AJUSTE"
        reason: String
    ) {
        database.withTransaction {
            val prevStock = product.stockQuantity
            val newStock = (prevStock + deltaQuantity).coerceAtLeast(0)
            productDao.updateStock(product.id, newStock)

            val movement = StockMovementEntity(
                productId = product.id,
                productName = product.name,
                type = movementType,
                quantity = kotlin.math.abs(deltaQuantity),
                previousStock = prevStock,
                newStock = newStock,
                reason = reason,
                timestamp = System.currentTimeMillis()
            )
            stockMovementDao.insertMovement(movement)
        }
    }

    // Clients
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()

    fun searchClients(query: String): Flow<List<ClientEntity>> = clientDao.searchClients(query)
    suspend fun insertClient(client: ClientEntity): Long = clientDao.insertClient(client)
    suspend fun updateClient(client: ClientEntity) = clientDao.updateClient(client)
    suspend fun deleteClient(client: ClientEntity) = clientDao.deleteClient(client)

    suspend fun payClientDebt(clientId: Long, amountPaid: Double) {
        database.withTransaction {
            clientDao.adjustCreditBalance(clientId, -amountPaid)
        }
    }

    suspend fun returnBottles(clientId: Long, returnedCount: Int) {
        database.withTransaction {
            clientDao.adjustReturnables(clientId, -returnedCount)
        }
    }

    // Orders & Sales
    val allOrders: Flow<List<SaleOrderEntity>> = saleOrderDao.getAllOrders()
    val allOrderItems: Flow<List<SaleOrderItemEntity>> = saleOrderDao.getAllOrderItems()

    fun getItemsForOrder(orderId: Long): Flow<List<SaleOrderItemEntity>> =
        saleOrderDao.getItemsForOrder(orderId)

    suspend fun createSaleOrder(
        client: ClientEntity?,
        cartItems: List<CartItem>,
        discount: Double,
        paymentMethod: String,
        isFaturado: Boolean,
        isDelivery: Boolean,
        deliveryAddress: String,
        notes: String
    ): Long {
        return database.withTransaction {
            val totalRaw = cartItems.sumOf { it.subtotal }
            val finalAmount = (totalRaw - discount).coerceAtLeast(0.0)
            val totalCost = cartItems.sumOf { it.totalCost }
            val orderNum = "#DIST-${(1000 + (System.currentTimeMillis() % 9000))}"

            val order = SaleOrderEntity(
                orderNumber = orderNum,
                clientId = client?.id,
                clientName = client?.name ?: "Consumidor Balcão",
                timestamp = System.currentTimeMillis(),
                totalAmount = finalAmount,
                totalCost = totalCost,
                discount = discount,
                paymentMethod = paymentMethod,
                paymentStatus = if (isFaturado) "PENDENTE" else "PAGO",
                orderStatus = if (isDelivery) "EM_SEPARACAO" else "CONCLUIDO",
                isDelivery = isDelivery,
                deliveryAddress = if (isDelivery) deliveryAddress else "",
                notes = notes
            )
            val orderId = saleOrderDao.insertOrder(order)

            val orderItems = cartItems.map { cartItem ->
                SaleOrderItemEntity(
                    saleOrderId = orderId,
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    unitType = cartItem.product.unitType,
                    unitPrice = cartItem.unitPrice,
                    costPrice = cartItem.product.costPrice,
                    quantity = cartItem.quantity,
                    subtotal = cartItem.subtotal
                )
            }
            saleOrderDao.insertOrderItems(orderItems)

            // Decrement stock for each item & record movement
            var returnableCount = 0
            for (cartItem in cartItems) {
                val currentProduct = productDao.getProductById(cartItem.product.id) ?: cartItem.product
                val prevStock = currentProduct.stockQuantity
                val newStock = (prevStock - cartItem.quantity).coerceAtLeast(0)
                productDao.updateStock(cartItem.product.id, newStock)

                stockMovementDao.insertMovement(
                    StockMovementEntity(
                        productId = cartItem.product.id,
                        productName = cartItem.product.name,
                        type = "VENDA",
                        quantity = cartItem.quantity,
                        previousStock = prevStock,
                        newStock = newStock,
                        reason = "Venda $orderNum para ${order.clientName}",
                        timestamp = System.currentTimeMillis()
                    )
                )

                if (cartItem.product.isReturnable) {
                    returnableCount += cartItem.quantity
                }
            }

            // If debt/faturado, update client credit balance
            if (client != null) {
                if (isFaturado) {
                    clientDao.adjustCreditBalance(client.id, finalAmount)
                }
                if (returnableCount > 0) {
                    clientDao.adjustReturnables(client.id, returnableCount)
                }
            }

            orderId
        }
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) {
        saleOrderDao.updateOrderStatus(orderId, newStatus)
    }

    suspend fun markOrderAsPaid(orderId: Long) {
        saleOrderDao.updatePaymentStatus(orderId, "PAGO")
    }

    // Stock Movements
    val allMovements: Flow<List<StockMovementEntity>> = stockMovementDao.getAllMovements()

    // Cash Register (Caixa do Dia)
    fun getCashRegisterByDate(dateKey: String): Flow<CashRegisterEntity?> =
        cashRegisterDao.getCashRegisterByDate(dateKey)

    suspend fun getCashRegisterByDateSync(dateKey: String): CashRegisterEntity? =
        cashRegisterDao.getCashRegisterByDateSync(dateKey)

    suspend fun saveCashRegister(cashRegister: CashRegisterEntity): Long =
        cashRegisterDao.insertOrUpdate(cashRegister)

    suspend fun updateCashCount(id: Long, countedCash: Double, notes: String) {
        cashRegisterDao.updateCashCount(id, countedCash, notes)
    }

    suspend fun addCashSupply(id: Long, amount: Double) {
        cashRegisterDao.addSupply(id, amount)
    }

    suspend fun addCashBleeding(id: Long, amount: Double) {
        cashRegisterDao.addBleeding(id, amount)
    }

    suspend fun closeCashRegister(id: Long, closedAt: Long, countedCash: Double, notes: String) {
        cashRegisterDao.closeRegister(id, closedAt, countedCash, notes)
    }
}
