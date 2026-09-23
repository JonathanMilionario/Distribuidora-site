package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val tradeName: String = "",
    val document: String = "",
    val phone: String = "",
    val address: String = "",
    val creditBalance: Double = 0.0,
    val returnableBottlesPending: Int = 0
)
