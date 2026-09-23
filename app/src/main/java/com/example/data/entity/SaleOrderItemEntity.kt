package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_order_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["saleOrderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("saleOrderId"), Index("productId")]
)
data class SaleOrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleOrderId: Long,
    val productId: Long,
    val productName: String,
    val unitType: String,
    val unitPrice: Double,
    val costPrice: Double,
    val quantity: Int,
    val subtotal: Double
)
