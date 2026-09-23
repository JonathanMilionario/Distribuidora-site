package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // ENTRADA, SAIDA, AJUSTE, VENDA
    val quantity: Int,
    val previousStock: Int,
    val newStock: Int,
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
