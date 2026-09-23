package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CartItem
import com.example.data.DistriBebidasDatabase
import com.example.data.DistriBebidasRepository
import com.example.data.entity.CashRegisterEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleOrderEntity
import com.example.data.entity.SaleOrderItemEntity
import com.example.data.entity.StockMovementEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val BEVERAGE_CATEGORIES = listOf(
    "Todos",
    "Cervejas",
    "Refrigerantes",
    "Destilados",
    "Vinhos",
    "Energéticos",
    "Águas & Sucos",
    "Chopp & Barril"
)

val BEVERAGE_UNITS = listOf(
    "Caixa c/ 24",
    "Fardo c/ 12",
    "Fardo c/ 6",
    "Garrafa 1L",
    "Garrafa 750ml",
    "Garrafa 600ml",
    "Lata 350ml",
    "Lata 473ml",
    "Long Neck 330ml",
    "Barril 50L",
    "Barril 30L",
    "PET 2L",
    "Unidade"
)

data class PosUiState(
    val cartItems: List<CartItem> = emptyList(),
    val selectedClient: ClientEntity? = null,
    val discount: Double = 0.0,
    val paymentMethod: String = "PIX",
    val isDelivery: Boolean = false,
    val deliveryAddress: String = "",
    val notes: String = ""
) {
    val subtotal: Double get() = cartItems.sumOf { it.subtotal }
    val totalCost: Double get() = cartItems.sumOf { it.totalCost }
    val total: Double get() = (subtotal - discount).coerceAtLeast(0.0)
    val totalItemCount: Int get() = cartItems.sumOf { it.quantity }
}

class DistriBebidasViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DistriBebidasRepository

    init {
        val db = DistriBebidasDatabase.getDatabase(application)
        repository = DistriBebidasRepository(db)
    }

    // Products & Inventory Filter
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCategory = MutableStateFlow("Todos")
    val productSearchQuery = MutableStateFlow("")

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        products,
        selectedCategory,
        productSearchQuery
    ) { all, category, query ->
        all.filter { prod ->
            val matchesCat = (category == "Todos" || prod.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    prod.name.contains(query, ignoreCase = true) ||
                    prod.barcode.contains(query, ignoreCase = true) ||
                    prod.supplier.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Clients
    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientSearchQuery = MutableStateFlow("")

    val filteredClients: StateFlow<List<ClientEntity>> = combine(
        clients,
        clientSearchQuery
    ) { all, query ->
        if (query.isBlank()) all
        else all.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.tradeName.contains(query, ignoreCase = true) ||
            it.document.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders
    val orders: StateFlow<List<SaleOrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderStatusFilter = MutableStateFlow("TODOS") // TODOS, CONCLUIDO, EM_SEPARACAO, EM_ROTA, PENDENTE

    val filteredOrders: StateFlow<List<SaleOrderEntity>> = combine(
        orders,
        orderStatusFilter
    ) { all, filter ->
        if (filter == "TODOS") all
        else if (filter == "PENDENTE") all.filter { it.paymentStatus == "PENDENTE" }
        else all.filter { it.orderStatus == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Movements
    val stockMovements: StateFlow<List<StockMovementEntity>> = repository.allMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // POS State
    val posState = MutableStateFlow(PosUiState())

    // Feedback message
    val userMessage = MutableStateFlow<String?>(null)

    // Last completed order for receipt modal
    val completedOrderReceipt = MutableStateFlow<SaleOrderEntity?>(null)

    // Current detail order
    val selectedDetailOrder = MutableStateFlow<SaleOrderEntity?>(null)
    val selectedDetailOrderItems = MutableStateFlow<List<SaleOrderItemEntity>>(emptyList())

    // --- Daily Cash Register (Caixa do Dia) ---
    val todayDateKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayFormattedDate: String = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date())

    val currentCashRegister: StateFlow<CashRegisterEntity?> = repository.getCashRegisterByDate(todayDateKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyCashReport: StateFlow<DailyCashReport> = combine(
        orders,
        repository.allOrderItems,
        currentCashRegister
    ) { allOrdersList, allItemsList, cashReg ->
        val calNow = Calendar.getInstance()
        val todayOrders = allOrdersList.filter { order ->
            val calOrder = Calendar.getInstance().apply { timeInMillis = order.timestamp }
            calNow.get(Calendar.YEAR) == calOrder.get(Calendar.YEAR) &&
            calNow.get(Calendar.DAY_OF_YEAR) == calOrder.get(Calendar.DAY_OF_YEAR)
        }

        val todayOrderIds = todayOrders.map { it.id }.toSet()
        val todayItems = allItemsList.filter { it.saleOrderId in todayOrderIds }

        // Group by product to aggregate sold quantities, unit price, revenue and profits
        val soldProductsMap = todayItems.groupBy { it.productId }
        val soldProductsList = soldProductsMap.map { (prodId, items) ->
            val first = items.first()
            val totalQty = items.sumOf { it.quantity }
            val totalRev = items.sumOf { it.subtotal }
            val costPrice = first.costPrice
            val unitPrice = if (totalQty > 0) (totalRev / totalQty) else first.unitPrice
            val totalProdProfit = (totalRev - (costPrice * totalQty)).coerceAtLeast(0.0)
            val markup = if (costPrice > 0) ((unitPrice - costPrice) / costPrice) * 100 else 0.0

            DailySoldProduct(
                productId = prodId,
                productName = first.productName,
                unitType = first.unitType,
                unitPrice = unitPrice,
                costPrice = costPrice,
                quantitySold = totalQty,
                totalRevenue = totalRev,
                totalProfit = totalProdProfit,
                markupPercent = markup
            )
        }.sortedByDescending { it.totalRevenue }

        val totalSales = todayOrders.sumOf { it.totalAmount }
        val totalCost = todayOrders.sumOf { it.totalCost }
        val totalProfit = (totalSales - totalCost).coerceAtLeast(0.0)

        val cashSales = todayOrders.filter { it.paymentMethod == "DINHEIRO" }.sumOf { it.totalAmount }
        val pixSales = todayOrders.filter { it.paymentMethod == "PIX" }.sumOf { it.totalAmount }
        val creditSales = todayOrders.filter { it.paymentMethod == "CARTAO_CREDITO" }.sumOf { it.totalAmount }
        val debitSales = todayOrders.filter { it.paymentMethod == "CARTAO_DEBITO" }.sumOf { it.totalAmount }
        val fiadoSales = todayOrders.filter { it.paymentMethod == "FATURADO_BOLETO" }.sumOf { it.totalAmount }

        val openingFloat = cashReg?.openingFloat ?: 200.0
        val supplies = cashReg?.suppliesAmount ?: 0.0
        val bleedings = cashReg?.bleedingsAmount ?: 0.0
        val expectedCash = (openingFloat + cashSales + supplies - bleedings).coerceAtLeast(0.0)

        val countedCash = cashReg?.countedCash
        val diff = if (countedCash != null) countedCash - expectedCash else 0.0
        val hasShortage = countedCash != null && diff < -0.05
        val hasSurplus = countedCash != null && diff > 0.05
        val isBalanced = countedCash != null && kotlin.math.abs(diff) <= 0.05

        DailyCashReport(
            dateKey = todayFormattedDate,
            totalSales = totalSales,
            totalCost = totalCost,
            totalProfit = totalProfit,
            totalOrdersCount = todayOrders.size,
            cashSales = cashSales,
            pixSales = pixSales,
            creditSales = creditSales,
            debitSales = debitSales,
            fiadoSales = fiadoSales,
            soldProducts = soldProductsList,
            openingFloat = openingFloat,
            suppliesAmount = supplies,
            bleedingsAmount = bleedings,
            expectedCash = expectedCash,
            countedCash = countedCash,
            cashDifference = diff,
            hasCashShortage = hasShortage,
            hasCashSurplus = hasSurplus,
            isCashBalanced = isBalanced,
            isCounted = countedCash != null,
            status = cashReg?.status ?: "ABERTO",
            notes = cashReg?.notes ?: "",
            registerId = cashReg?.id
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyCashReport(dateKey = todayFormattedDate))

    // --- POS Cart Actions ---
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val currentItems = posState.value.cartItems.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = currentItems[index]
            val newQty = existing.quantity + quantity
            if (newQty <= product.stockQuantity) {
                currentItems[index] = existing.copy(quantity = newQty)
                posState.value = posState.value.copy(cartItems = currentItems)
            } else {
                userMessage.value = "Estoque insuficiente para ${product.name} (Disponível: ${product.stockQuantity})"
            }
        } else {
            if (product.stockQuantity >= quantity) {
                currentItems.add(CartItem(product, quantity, product.salePrice))
                posState.value = posState.value.copy(cartItems = currentItems)
            } else {
                userMessage.value = "Estoque insuficiente! Disponível: ${product.stockQuantity}"
            }
        }
    }

    fun updateCartItemQuantity(productId: Long, delta: Int) {
        val currentItems = posState.value.cartItems.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val item = currentItems[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentItems.removeAt(index)
            } else if (newQty <= item.product.stockQuantity) {
                currentItems[index] = item.copy(quantity = newQty)
            } else {
                userMessage.value = "Estoque máximo atingido (${item.product.stockQuantity})"
                return
            }
            posState.value = posState.value.copy(cartItems = currentItems)
        }
    }

    fun removeCartItem(productId: Long) {
        val currentItems = posState.value.cartItems.filterNot { it.product.id == productId }
        posState.value = posState.value.copy(cartItems = currentItems)
    }

    fun clearCart() {
        posState.value = PosUiState()
    }

    fun setPosClient(client: ClientEntity?) {
        posState.value = posState.value.copy(
            selectedClient = client,
            deliveryAddress = if (client != null && posState.value.isDelivery) client.address else posState.value.deliveryAddress
        )
    }

    fun setPosDiscount(discount: Double) {
        posState.value = posState.value.copy(discount = discount.coerceAtLeast(0.0))
    }

    fun setPosPaymentMethod(method: String) {
        posState.value = posState.value.copy(paymentMethod = method)
    }

    fun setPosDelivery(isDelivery: Boolean) {
        val client = posState.value.selectedClient
        posState.value = posState.value.copy(
            isDelivery = isDelivery,
            deliveryAddress = if (isDelivery && client != null && posState.value.deliveryAddress.isBlank()) client.address else posState.value.deliveryAddress
        )
    }

    fun setPosDeliveryAddress(address: String) {
        posState.value = posState.value.copy(deliveryAddress = address)
    }

    fun setPosNotes(notes: String) {
        posState.value = posState.value.copy(notes = notes)
    }

    fun completeSale() {
        val state = posState.value
        if (state.cartItems.isEmpty()) {
            userMessage.value = "O carrinho de compras está vazio!"
            return
        }

        viewModelScope.launch {
            try {
                val isFaturado = state.paymentMethod == "FATURADO_BOLETO"
                val orderId = repository.createSaleOrder(
                    client = state.selectedClient,
                    cartItems = state.cartItems,
                    discount = state.discount,
                    paymentMethod = state.paymentMethod,
                    isFaturado = isFaturado,
                    isDelivery = state.isDelivery,
                    deliveryAddress = state.deliveryAddress,
                    notes = state.notes
                )

                // Retrieve order for receipt display
                val db = DistriBebidasDatabase.getDatabase(getApplication())
                val created = db.saleOrderDao().getOrderById(orderId)
                completedOrderReceipt.value = created
                userMessage.value = "Venda concluída com sucesso! Pedido ${created?.orderNumber}"
                clearCart()
            } catch (e: Exception) {
                userMessage.value = "Erro ao finalizar venda: ${e.localizedMessage}"
            }
        }
    }

    // --- Product & Stock Actions ---
    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.insertProduct(product)
                // Log initial stock movement if > 0
                if (product.stockQuantity > 0) {
                    repository.adjustStock(
                        product = product,
                        deltaQuantity = product.stockQuantity,
                        movementType = "ENTRADA",
                        reason = "Cadastro inicial de estoque"
                    )
                }
                userMessage.value = "Produto cadastrado: ${product.name}"
            } else {
                repository.updateProduct(product)
                userMessage.value = "Produto atualizado: ${product.name}"
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            userMessage.value = "Produto removido: ${product.name}"
        }
    }

    fun adjustStockQuantity(product: ProductEntity, delta: Int, type: String, reason: String) {
        viewModelScope.launch {
            repository.adjustStock(product, delta, type, reason)
            userMessage.value = "Estoque de ${product.name} atualizado ($type: ${if (delta > 0) "+$delta" else "$delta"})"
        }
    }

    // --- Client Actions ---
    fun saveClient(client: ClientEntity) {
        viewModelScope.launch {
            if (client.id == 0L) {
                repository.insertClient(client)
                userMessage.value = "Cliente cadastrado: ${client.name}"
            } else {
                repository.updateClient(client)
                userMessage.value = "Cliente atualizado: ${client.name}"
            }
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
            userMessage.value = "Cliente removido: ${client.name}"
        }
    }

    fun payClientDebt(clientId: Long, amount: Double) {
        viewModelScope.launch {
            repository.payClientDebt(clientId, amount)
            userMessage.value = "Pagamento de R$ ${String.format("%.2f", amount)} registrado com sucesso!"
        }
    }

    fun returnClientBottles(clientId: Long, count: Int) {
        viewModelScope.launch {
            repository.returnBottles(clientId, count)
            userMessage.value = "Devolução de $count cascos/vasilhames registrada!"
        }
    }

    // --- Order Actions ---
    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            userMessage.value = "Status do pedido atualizado para $newStatus"
        }
    }

    fun markOrderPaid(orderId: Long) {
        viewModelScope.launch {
            repository.markOrderAsPaid(orderId)
            userMessage.value = "Pedido marcado como Pago!"
        }
    }

    fun loadOrderDetails(order: SaleOrderEntity) {
        selectedDetailOrder.value = order
        viewModelScope.launch {
            val db = DistriBebidasDatabase.getDatabase(getApplication())
            selectedDetailOrderItems.value = db.saleOrderDao().getItemsForOrderSync(order.id)
        }
    }

    fun clearOrderDetails() {
        selectedDetailOrder.value = null
        selectedDetailOrderItems.value = emptyList()
    }

    fun clearUserMessage() {
        userMessage.value = null
    }

    fun clearCompletedReceipt() {
        completedOrderReceipt.value = null
    }

    // --- Cash Register Actions ---
    fun countCash(countedAmount: Double, notes: String) {
        viewModelScope.launch {
            val existing = repository.getCashRegisterByDateSync(todayDateKey)
            if (existing != null) {
                repository.updateCashCount(existing.id, countedAmount, notes)
            } else {
                repository.saveCashRegister(
                    CashRegisterEntity(
                        dateKey = todayDateKey,
                        countedCash = countedAmount,
                        notes = notes
                    )
                )
            }
            userMessage.value = "Contagem da gaveta salva: R$ ${String.format(Locale("pt", "BR"), "%.2f", countedAmount)}"
        }
    }

    fun addCashSupply(amount: Double, reason: String) {
        viewModelScope.launch {
            val existing = repository.getCashRegisterByDateSync(todayDateKey)
            val regId = existing?.id ?: repository.saveCashRegister(
                CashRegisterEntity(dateKey = todayDateKey)
            )
            repository.addCashSupply(regId, amount)
            userMessage.value = "Suprimento de R$ ${String.format(Locale("pt", "BR"), "%.2f", amount)} adicionado ao caixa!"
        }
    }

    fun addCashBleeding(amount: Double, reason: String) {
        viewModelScope.launch {
            val existing = repository.getCashRegisterByDateSync(todayDateKey)
            val regId = existing?.id ?: repository.saveCashRegister(
                CashRegisterEntity(dateKey = todayDateKey)
            )
            repository.addCashBleeding(regId, amount)
            userMessage.value = "Sangria de R$ ${String.format(Locale("pt", "BR"), "%.2f", amount)} retirada do caixa!"
        }
    }

    fun closeCashRegister(countedAmount: Double, notes: String) {
        viewModelScope.launch {
            val existing = repository.getCashRegisterByDateSync(todayDateKey)
            val regId = existing?.id ?: repository.saveCashRegister(
                CashRegisterEntity(dateKey = todayDateKey)
            )
            repository.closeCashRegister(regId, System.currentTimeMillis(), countedAmount, notes)
            userMessage.value = "Caixa do dia fechado com sucesso!"
        }
    }

    fun reopenCashRegister() {
        viewModelScope.launch {
            val existing = repository.getCashRegisterByDateSync(todayDateKey)
            if (existing != null) {
                repository.saveCashRegister(existing.copy(status = "ABERTO", closedAt = null))
                userMessage.value = "Caixa reaberto para novas movimentações."
            }
        }
    }

    fun updateOpeningFloat(newFloat: Double) {
        viewModelScope.launch {
            val existing = repository.getCashRegisterByDateSync(todayDateKey)
            if (existing != null) {
                repository.saveCashRegister(existing.copy(openingFloat = newFloat))
            } else {
                repository.saveCashRegister(CashRegisterEntity(dateKey = todayDateKey, openingFloat = newFloat))
            }
            userMessage.value = "Fundo de troco atualizado para R$ ${String.format(Locale("pt", "BR"), "%.2f", newFloat)}"
        }
    }
}
