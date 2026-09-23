package com.example.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val ptBrLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", ptBrLocale)
    private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", ptBrLocale)

    fun formatCurrency(value: Double): String {
        return try {
            currencyFormat.format(value)
        } catch (e: Exception) {
            "R$ ${String.format(ptBrLocale, "%.2f", value)}"
        }
    }

    fun formatDateTime(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return shortDateFormat.format(Date(timestamp))
    }
}
