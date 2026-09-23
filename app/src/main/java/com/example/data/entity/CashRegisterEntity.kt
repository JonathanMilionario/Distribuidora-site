package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_registers")
data class CashRegisterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateKey: String, // formato "yyyy-MM-dd"
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null,
    val openingFloat: Double = 200.0, // Fundo de troco inicial
    val suppliesAmount: Double = 0.0, // Suprimentos / reforços de dinheiro
    val bleedingsAmount: Double = 0.0, // Sangrias / retiradas de dinheiro
    val countedCash: Double? = null, // Dinheiro físico contado pelo operador
    val status: String = "ABERTO", // ABERTO, FECHADO
    val notes: String = ""
)
