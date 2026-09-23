package com.example.ui

data class DailySoldProduct(
    val productId: Long,
    val productName: String,
    val unitType: String,
    val unitPrice: Double, // Preço de venda unitário praticado
    val costPrice: Double, // Preço de custo unitário
    val quantitySold: Int, // Quantidade de unidades/caixas vendidas no dia
    val totalRevenue: Double, // Faturamento total gerado por este produto
    val totalProfit: Double, // Lucro total gerado por este produto no dia
    val markupPercent: Double // Percentual de lucro / markup
)

data class DailyCashReport(
    val dateKey: String = "",
    val totalSales: Double = 0.0, // Total de vendas do dia
    val totalCost: Double = 0.0, // Custo das mercadorias vendidas
    val totalProfit: Double = 0.0, // Lucro do dia
    val totalOrdersCount: Int = 0, // Número de vendas do dia
    val cashSales: Double = 0.0, // Vendas em Dinheiro
    val pixSales: Double = 0.0, // Vendas em PIX
    val creditSales: Double = 0.0, // Vendas Cartão Crédito
    val debitSales: Double = 0.0, // Vendas Cartão Débito
    val fiadoSales: Double = 0.0, // Vendas Faturadas / Boleto
    val soldProducts: List<DailySoldProduct> = emptyList(), // Mercadorias vendidas
    val openingFloat: Double = 200.0, // Fundo de troco inicial
    val suppliesAmount: Double = 0.0, // Suprimentos / Reforços
    val bleedingsAmount: Double = 0.0, // Sangrias / Retiradas
    val expectedCash: Double = 0.0, // Saldo Esperado em Dinheiro na Gaveta
    val countedCash: Double? = null, // Dinheiro Físico Contado na Gaveta
    val cashDifference: Double = 0.0, // Diferença apurada (Contado - Esperado)
    val hasCashShortage: Boolean = false, // SE TEVE QUEBRA DE CAIXA (Falta de dinheiro)
    val hasCashSurplus: Boolean = false, // Sobra de caixa
    val isCashBalanced: Boolean = true, // Caixa correto sem quebra
    val isCounted: Boolean = false, // Se o operador já realizou a contagem
    val status: String = "ABERTO", // ABERTO ou FECHADO
    val notes: String = "",
    val registerId: Long? = null
)
