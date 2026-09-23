package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_orders")
data class SaleOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val clientId: Long? = null,
    val clientName: String = "Consumidor Balcão",
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val totalCost: Double = 0.0,
    val discount: Double = 0.0,
    val paymentMethod: String = "PIX",
    val paymentStatus: String = "PAGO", // PAGO, PENDENTE
    val orderStatus: String = "CONCLUIDO", // CONCLUIDO, EM_SEPARACAO, EM_ROTA, CANCELADO
    val isDelivery: Boolean = false,
    val deliveryAddress: String = "",
    val notes: String = ""
)
