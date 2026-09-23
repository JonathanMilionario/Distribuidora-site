package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val barcode: String = "",
    val unitType: String = "Unidade",
    val costPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val stockQuantity: Int = 0,
    val minStock: Int = 10,
    val isReturnable: Boolean = false,
    val supplier: String = ""
)
