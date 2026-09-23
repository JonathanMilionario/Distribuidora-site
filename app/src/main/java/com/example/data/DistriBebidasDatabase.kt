package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CashRegisterDao
import com.example.data.dao.ClientDao
import com.example.data.dao.ProductDao
import com.example.data.dao.SaleOrderDao
import com.example.data.dao.StockMovementDao
import com.example.data.dao.UserDao
import com.example.data.entity.CashRegisterEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleOrderEntity
import com.example.data.entity.SaleOrderItemEntity
import com.example.data.entity.StockMovementEntity
import com.example.data.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        ProductEntity::class,
        ClientEntity::class,
        SaleOrderEntity::class,
        SaleOrderItemEntity::class,
        StockMovementEntity::class,
        CashRegisterEntity::class,
        UserEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DistriBebidasDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun clientDao(): ClientDao
    abstract fun saleOrderDao(): SaleOrderDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun cashRegisterDao(): CashRegisterDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: DistriBebidasDatabase? = null

        fun getDatabase(context: Context): DistriBebidasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DistriBebidasDatabase::class.java,
                    "distribebidas_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseSeedCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseSeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedInitialData(database)
                }
            }
        }
    }
}

suspend fun seedInitialData(database: DistriBebidasDatabase) {
    val productDao = database.productDao()
    val clientDao = database.clientDao()
    val orderDao = database.saleOrderDao()
    val movementDao = database.stockMovementDao()

    val initialProducts = listOf(
        ProductEntity(
            name = "Heineken Long Neck 330ml (Cx 24un)",
            category = "Cervejas",
            barcode = "7896045506049",
            unitType = "Caixa c/ 24",
            costPrice = 110.00,
            salePrice = 148.00,
            stockQuantity = 65,
            minStock = 20,
            isReturnable = false,
            supplier = "Heineken Brasil"
        ),
        ProductEntity(
            name = "Amstel Puro Malte Lata 350ml (Fd 12un)",
            category = "Cervejas",
            barcode = "7896045505431",
            unitType = "Fardo c/ 12",
            costPrice = 34.50,
            salePrice = 46.90,
            stockQuantity = 90,
            minStock = 30,
            isReturnable = false,
            supplier = "Heineken Brasil"
        ),
        ProductEntity(
            name = "Brahma Chopp Garrafa 600ml (Cx 24un)",
            category = "Cervejas",
            barcode = "7891991000826",
            unitType = "Caixa c/ 24",
            costPrice = 132.00,
            salePrice = 175.00,
            stockQuantity = 38,
            minStock = 15,
            isReturnable = true,
            supplier = "Ambev"
        ),
        ProductEntity(
            name = "Corona Extra 330ml (Cx 24un)",
            category = "Cervejas",
            barcode = "7501064191319",
            unitType = "Caixa c/ 24",
            costPrice = 135.00,
            salePrice = 189.00,
            stockQuantity = 52,
            minStock = 15,
            isReturnable = false,
            supplier = "Ambev"
        ),
        ProductEntity(
            name = "Chopp Pilsen Barril 50L",
            category = "Chopp & Barril",
            barcode = "7898912340012",
            unitType = "Barril 50L",
            costPrice = 380.00,
            salePrice = 550.00,
            stockQuantity = 11,
            minStock = 5,
            isReturnable = true,
            supplier = "Cervejaria Dourada"
        ),
        ProductEntity(
            name = "Coca-Cola Original 2L (Fd 6un)",
            category = "Refrigerantes",
            barcode = "7894900010015",
            unitType = "Fardo c/ 6",
            costPrice = 45.00,
            salePrice = 59.90,
            stockQuantity = 120,
            minStock = 40,
            isReturnable = false,
            supplier = "Coca-Cola Femsa"
        ),
        ProductEntity(
            name = "Guaraná Antarctica 2L (Fd 6un)",
            category = "Refrigerantes",
            barcode = "7891991001342",
            unitType = "Fardo c/ 6",
            costPrice = 38.00,
            salePrice = 49.90,
            stockQuantity = 85,
            minStock = 30,
            isReturnable = false,
            supplier = "Ambev"
        ),
        ProductEntity(
            name = "Coca-Cola Lata 350ml (Fd 12un)",
            category = "Refrigerantes",
            barcode = "7894900011517",
            unitType = "Fardo c/ 12",
            costPrice = 32.00,
            salePrice = 44.00,
            stockQuantity = 110,
            minStock = 35,
            isReturnable = false,
            supplier = "Coca-Cola Femsa"
        ),
        ProductEntity(
            name = "Whisky Red Label 1 Litro",
            category = "Destilados",
            barcode = "5000267014005",
            unitType = "Garrafa 1L",
            costPrice = 72.00,
            salePrice = 99.90,
            stockQuantity = 26,
            minStock = 10,
            isReturnable = false,
            supplier = "Diageo"
        ),
        ProductEntity(
            name = "Gin Tanqueray London Dry 750ml",
            category = "Destilados",
            barcode = "5000281005409",
            unitType = "Garrafa 750ml",
            costPrice = 84.00,
            salePrice = 119.00,
            stockQuantity = 22,
            minStock = 8,
            isReturnable = false,
            supplier = "Diageo"
        ),
        ProductEntity(
            name = "Vodka Absolut 1 Litro",
            category = "Destilados",
            barcode = "7312040017034",
            unitType = "Garrafa 1L",
            costPrice = 68.00,
            salePrice = 94.90,
            stockQuantity = 29,
            minStock = 10,
            isReturnable = false,
            supplier = "Pernod Ricard"
        ),
        ProductEntity(
            name = "Vinho Casillero del Diablo Cabernet 750ml",
            category = "Vinhos",
            barcode = "7804300100412",
            unitType = "Garrafa 750ml",
            costPrice = 39.00,
            salePrice = 58.00,
            stockQuantity = 45,
            minStock = 12,
            isReturnable = false,
            supplier = "Concha y Toro"
        ),
        ProductEntity(
            name = "Espumante Chandon Réserve Brut 750ml",
            category = "Vinhos",
            barcode = "7896000701014",
            unitType = "Garrafa 750ml",
            costPrice = 75.00,
            salePrice = 105.00,
            stockQuantity = 16,
            minStock = 6,
            isReturnable = false,
            supplier = "Moët Hennessy"
        ),
        ProductEntity(
            name = "Red Bull Energy Drink 250ml (Fd 24un)",
            category = "Energéticos",
            barcode = "9002490100070",
            unitType = "Fardo c/ 24",
            costPrice = 148.00,
            salePrice = 199.00,
            stockQuantity = 34,
            minStock = 15,
            isReturnable = false,
            supplier = "Red Bull Brasil"
        ),
        ProductEntity(
            name = "Monster Energy 473ml (Fd 6un)",
            category = "Energéticos",
            barcode = "7898930810108",
            unitType = "Fardo c/ 6",
            costPrice = 42.00,
            salePrice = 58.00,
            stockQuantity = 48,
            minStock = 15,
            isReturnable = false,
            supplier = "Monster Energy"
        ),
        ProductEntity(
            name = "Água Mineral Crystal sem Gás 500ml (Fd 12un)",
            category = "Águas & Sucos",
            barcode = "7894900530018",
            unitType = "Fardo c/ 12",
            costPrice = 14.00,
            salePrice = 24.00,
            stockQuantity = 135,
            minStock = 40,
            isReturnable = false,
            supplier = "Coca-Cola Femsa"
        ),
        ProductEntity(
            name = "Água São Lourenço c/ Gás 300ml (Cx 24un)",
            category = "Águas & Sucos",
            barcode = "7891048030014",
            unitType = "Caixa c/ 24",
            costPrice = 36.00,
            salePrice = 54.00,
            stockQuantity = 7,
            minStock = 15, // Low stock indicator
            isReturnable = false,
            supplier = "Minalba"
        )
    )
    productDao.insertProducts(initialProducts)

    val initialClients = listOf(
        ClientEntity(
            name = "Bar & Espetinho do Zé",
            tradeName = "José Pereira ME",
            document = "28.192.405/0001-33",
            phone = "(11) 97123-4567",
            address = "Rua Augusta, 820 - Consolação",
            creditBalance = 420.00,
            returnableBottlesPending = 6
        ),
        ClientEntity(
            name = "Mercadinho Central da Vila",
            tradeName = "Silva Alimentos LTDA",
            document = "14.920.103/0001-88",
            phone = "(11) 98234-5678",
            address = "Av. do Cursino, 1420 - Saúde",
            creditBalance = 0.0,
            returnableBottlesPending = 2
        ),
        ClientEntity(
            name = "Empório Gourmet & Adega",
            tradeName = "Empório Vinhos & Cia",
            document = "35.882.119/0001-44",
            phone = "(11) 99345-6789",
            address = "Al. dos Nhambiquaras, 510 - Moema",
            creditBalance = 850.00,
            returnableBottlesPending = 0
        ),
        ClientEntity(
            name = "Lanchonete & Pizzaria Bella",
            tradeName = "Bella Napoli Pizza ME",
            document = "42.110.590/0001-12",
            phone = "(11) 96456-7890",
            address = "Rua Vergueiro, 3300 - Vila Mariana",
            creditBalance = 0.0,
            returnableBottlesPending = 10
        )
    )
    clientDao.insertClients(initialClients)

    // Seed an initial sale order
    val order1 = SaleOrderEntity(
        orderNumber = "#DIST-1001",
        clientId = 1L,
        clientName = "Bar & Espetinho do Zé",
        timestamp = System.currentTimeMillis() - 86400000L,
        totalAmount = 521.80,
        totalCost = 398.00,
        discount = 10.00,
        paymentMethod = "PIX",
        paymentStatus = "PAGO",
        orderStatus = "CONCLUIDO",
        isDelivery = true,
        deliveryAddress = "Rua Augusta, 820 - Consolação",
        notes = "Entregar na porta lateral"
    )
    val orderId1 = orderDao.insertOrder(order1)
    val items1 = listOf(
        SaleOrderItemEntity(
            saleOrderId = orderId1,
            productId = 1L,
            productName = "Heineken Long Neck 330ml (Cx 24un)",
            unitType = "Caixa c/ 24",
            unitPrice = 148.00,
            costPrice = 110.00,
            quantity = 2,
            subtotal = 296.00
        ),
        SaleOrderItemEntity(
            saleOrderId = orderId1,
            productId = 3L,
            productName = "Brahma Chopp Garrafa 600ml (Cx 24un)",
            unitType = "Caixa c/ 24",
            unitPrice = 175.00,
            costPrice = 132.00,
            quantity = 1,
            subtotal = 175.00
        ),
        SaleOrderItemEntity(
            saleOrderId = orderId1,
            productId = 6L,
            productName = "Coca-Cola Original 2L (Fd 6un)",
            unitType = "Fardo c/ 6",
            unitPrice = 59.90,
            costPrice = 45.00,
            quantity = 1,
            subtotal = 59.90
        )
    )
    orderDao.insertOrderItems(items1)

    // Seed today's orders for Caixa do Dia demonstration
    val todayOrder1 = SaleOrderEntity(
        orderNumber = "#DIST-1002",
        clientId = 2L,
        clientName = "Mercadinho Central da Vila",
        timestamp = System.currentTimeMillis() - 7200000L, // 2 horas atrás hoje
        totalAmount = 485.80,
        totalCost = 352.00,
        discount = 0.0,
        paymentMethod = "DINHEIRO",
        paymentStatus = "PAGO",
        orderStatus = "CONCLUIDO",
        isDelivery = false,
        deliveryAddress = "",
        notes = "Pago em dinheiro no balcão"
    )
    val todayOrderId1 = orderDao.insertOrder(todayOrder1)
    val todayItems1 = listOf(
        SaleOrderItemEntity(
            saleOrderId = todayOrderId1,
            productId = 1L,
            productName = "Heineken Long Neck 330ml (Cx 24un)",
            unitType = "Caixa c/ 24",
            unitPrice = 148.00,
            costPrice = 110.00,
            quantity = 2,
            subtotal = 296.00
        ),
        SaleOrderItemEntity(
            saleOrderId = todayOrderId1,
            productId = 6L,
            productName = "Coca-Cola Original 2L (Fd 6un)",
            unitType = "Fardo c/ 6",
            unitPrice = 59.90,
            costPrice = 45.00,
            quantity = 1,
            subtotal = 59.90
        ),
        SaleOrderItemEntity(
            saleOrderId = todayOrderId1,
            productId = 10L,
            productName = "Red Bull Energy Drink 250ml (Fd 24un)",
            unitType = "Fardo c/ 24",
            unitPrice = 129.90,
            costPrice = 87.00,
            quantity = 1,
            subtotal = 129.90
        )
    )
    orderDao.insertOrderItems(todayItems1)

    val todayOrder2 = SaleOrderEntity(
        orderNumber = "#DIST-1003",
        clientId = 4L,
        clientName = "Lanchonete & Pizzaria Bella",
        timestamp = System.currentTimeMillis() - 3600000L, // 1 hora atrás hoje
        totalAmount = 265.80,
        totalCost = 188.00,
        discount = 5.00,
        paymentMethod = "PIX",
        paymentStatus = "PAGO",
        orderStatus = "CONCLUIDO",
        isDelivery = true,
        deliveryAddress = "Rua Vergueiro, 3300 - Vila Mariana",
        notes = "Entrega rápida"
    )
    val todayOrderId2 = orderDao.insertOrder(todayOrder2)
    val todayItems2 = listOf(
        SaleOrderItemEntity(
            saleOrderId = todayOrderId2,
            productId = 2L,
            productName = "Amstel Puro Malte Lata 350ml (Fd 12un)",
            unitType = "Fardo c/ 12",
            unitPrice = 46.90,
            costPrice = 34.50,
            quantity = 3,
            subtotal = 140.70
        ),
        SaleOrderItemEntity(
            saleOrderId = todayOrderId2,
            productId = 7L,
            productName = "Guaraná Antarctica 2L (Fd 6un)",
            unitType = "Fardo c/ 6",
            unitPrice = 43.40,
            costPrice = 31.00,
            quantity = 2,
            subtotal = 86.80
        ),
        SaleOrderItemEntity(
            saleOrderId = todayOrderId2,
            productId = 13L,
            productName = "Água Mineral Crystal sem Gás 500ml (Fd 12un)",
            unitType = "Fardo c/ 12",
            unitPrice = 24.00,
            costPrice = 14.00,
            quantity = 1,
            subtotal = 24.00
        )
    )
    orderDao.insertOrderItems(todayItems2)

    // Seed cash register for today
    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val cashRegisterDao = database.cashRegisterDao()
    cashRegisterDao.insertOrUpdate(
        CashRegisterEntity(
            dateKey = todayKey,
            openingFloat = 200.00,
            suppliesAmount = 50.00, // Suprimento de troco
            bleedingsAmount = 0.0,
            countedCash = 735.80, // Fundo (200) + Suprimento (50) + Dinheiro (485.80) = 735.80 (Sem quebra!)
            status = "ABERTO",
            notes = "Caixa aberto às 08:00 com R$ 200,00 de troco."
        )
    )

    // Seed initial stock movements
    val sampleMovements = listOf(
        StockMovementEntity(
            productId = 1L,
            productName = "Heineken Long Neck 330ml (Cx 24un)",
            type = "ENTRADA",
            quantity = 50,
            previousStock = 17,
            newStock = 67,
            reason = "Carga recebida - NF 84920 - Fornecedor Heineken",
            timestamp = System.currentTimeMillis() - 172800000L
        ),
        StockMovementEntity(
            productId = 1L,
            productName = "Heineken Long Neck 330ml (Cx 24un)",
            type = "VENDA",
            quantity = 2,
            previousStock = 67,
            newStock = 65,
            reason = "Venda #DIST-1001",
            timestamp = System.currentTimeMillis() - 86400000L
        ),
        StockMovementEntity(
            productId = 5L,
            productName = "Chopp Pilsen Barril 50L",
            type = "ENTRADA",
            quantity = 12,
            previousStock = 0,
            newStock = 12,
            reason = "Carga inicial Chopp Artesanal",
            timestamp = System.currentTimeMillis() - 259200000L
        )
    )
    movementDao.insertMovements(sampleMovements)

    // Seed default users (Admin & Funcionário)
    val userDao = database.userDao()
    userDao.insertUser(
        UserEntity(
            name = "Administrador Geral",
            username = "admin",
            password = "admin123",
            role = "ADMIN"
        )
    )
    userDao.insertUser(
        UserEntity(
            name = "João Caixa (Operador)",
            username = "caixa",
            password = "123456",
            role = "FUNCIONARIO"
        )
    )
}
