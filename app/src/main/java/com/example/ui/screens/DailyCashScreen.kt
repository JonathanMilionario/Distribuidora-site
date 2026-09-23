package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.DailyCashReport
import com.example.ui.DailySoldProduct
import com.example.ui.DistriBebidasViewModel
import com.example.ui.components.CashCountDialog
import com.example.ui.components.CashMovementDialog
import com.example.ui.components.OpeningFloatDialog
import com.example.ui.theme.*
import com.example.ui.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCashScreen(
    viewModel: DistriBebidasViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPos: () -> Unit
) {
    val report by viewModel.dailyCashReport.collectAsState()

    var showCountDialog by remember { mutableStateOf(false) }
    var showSupplyDialog by remember { mutableStateOf(false) }
    var showBleedingDialog by remember { mutableStateOf(false) }
    var showFloatDialog by remember { mutableStateOf(false) }
    var showCloseConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Caixa do Dia",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Referência: ${report.dateKey} • Status: ${report.status}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_back_daily_cash")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    AssistChip(
                        onClick = {
                            if (report.status == "ABERTO") {
                                showCloseConfirmDialog = true
                            } else {
                                viewModel.reopenCashRegister()
                            }
                        },
                        label = {
                            Text(
                                text = if (report.status == "ABERTO") "Fechar Caixa" else "Reabrir Caixa",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (report.status == "ABERTO") Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (report.status == "ABERTO") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_toggle_cash_status")
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. INDICADOR PRINCIPAL: TEVE QUEBRA DE CAIXA?
            item {
                CashDiscrepancyIndicatorCard(
                    report = report,
                    onOpenCountDialog = { showCountDialog = true }
                )
            }

            // 2. INDICADORES DE VENDAS & LUCRO DO DIA
            item {
                Text(
                    text = "Desempenho Geral do Dia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Total de Vendas",
                        value = Formatters.formatCurrency(report.totalSales),
                        subtitle = "${report.totalOrdersCount} pedidos hoje",
                        icon = Icons.Default.TrendingUp,
                        color = GreenSuccess,
                        modifier = Modifier.weight(1f).testTag("card_daily_total_sales")
                    )
                    KpiCard(
                        title = "Lucro do Dia",
                        value = Formatters.formatCurrency(report.totalProfit),
                        subtitle = if (report.totalSales > 0)
                            "Margem: ${String.format("%.1f", (report.totalProfit / report.totalSales) * 100)}%"
                        else "Sem margem",
                        icon = Icons.Default.MonetizationOn,
                        color = AmberPrimary,
                        modifier = Modifier.weight(1f).testTag("card_daily_total_profit")
                    )
                }
            }

            // 3. CONFERÊNCIA FÍSICA DA GAVETA DE DINHEIRO
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_cash_drawer_reconciliation"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PointOfSale,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Conferência da Gaveta (Dinheiro)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = { showFloatDialog = true },
                                modifier = Modifier.size(32.dp).testTag("btn_edit_opening_float")
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = "Configurar Fundo", modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Linhas de Composição
                        ReconciliationRow(label = "Fundo de Troco Inicial", value = Formatters.formatCurrency(report.openingFloat), isNeutral = true)
                        ReconciliationRow(label = "(+) Vendas em Dinheiro Hoje", value = "+ " + Formatters.formatCurrency(report.cashSales), isPositive = true)
                        if (report.suppliesAmount > 0) {
                            ReconciliationRow(label = "(+) Suprimentos de Caixa", value = "+ " + Formatters.formatCurrency(report.suppliesAmount), isPositive = true)
                        }
                        if (report.bleedingsAmount > 0) {
                            ReconciliationRow(label = "(-) Sangrias / Retiradas", value = "- " + Formatters.formatCurrency(report.bleedingsAmount), isNegative = true)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        ReconciliationRow(
                            label = "Saldo Esperado em Dinheiro",
                            value = Formatters.formatCurrency(report.expectedCash),
                            isBold = true
                        )

                        ReconciliationRow(
                            label = "Dinheiro Físico Contado",
                            value = if (report.countedCash != null) Formatters.formatCurrency(report.countedCash!!) else "Aguardando contagem",
                            isBold = true,
                            customColor = if (report.countedCash != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        ReconciliationRow(
                            label = "Diferença Apurada",
                            value = if (report.countedCash != null) {
                                when {
                                    report.hasCashShortage -> "FALTA DE " + Formatters.formatCurrency(kotlin.math.abs(report.cashDifference))
                                    report.hasCashSurplus -> "SOBRA DE " + Formatters.formatCurrency(report.cashDifference)
                                    else -> "R$ 0,00 (Caixa Exato)"
                                }
                            } else "Não apurado",
                            isBold = true,
                            customColor = when {
                                !report.isCounted -> MaterialTheme.colorScheme.onSurfaceVariant
                                report.hasCashShortage -> RedDanger
                                report.hasCashSurplus -> BlueInfo
                                else -> GreenSuccessDark
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botões de Ação da Gaveta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showCountDialog = true },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("btn_open_count_dialog"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.PriceCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Conferir Gaveta")
                            }

                            OutlinedButton(
                                onClick = { showBleedingDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_open_bleeding_dialog")
                            ) {
                                Text("- Sangria", color = RedDanger)
                            }

                            OutlinedButton(
                                onClick = { showSupplyDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_open_supply_dialog")
                            ) {
                                Text("+ Reforço", color = GreenSuccessDark)
                            }
                        }
                    }
                }
            }

            // 4. FORMAS DE PAGAMENTO DO DIA
            item {
                Text(
                    text = "Vendas por Forma de Pagamento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethodRow(name = "Dinheiro", amount = report.cashSales, total = report.totalSales, icon = Icons.Default.Payments)
                        PaymentMethodRow(name = "PIX", amount = report.pixSales, total = report.totalSales, icon = Icons.Default.QrCode)
                        PaymentMethodRow(name = "Cartão de Crédito", amount = report.creditSales, total = report.totalSales, icon = Icons.Default.CreditCard)
                        PaymentMethodRow(name = "Cartão de Débito", amount = report.debitSales, total = report.totalSales, icon = Icons.Default.CreditCard)
                        PaymentMethodRow(name = "Faturado / Fiado", amount = report.fiadoSales, total = report.totalSales, icon = Icons.Default.ReceiptLong)
                    }
                }
            }

            // 5. QUAIS MERCADORIAS FORAM VENDIDAS NO DIA (DETALHAMENTO COM PREÇOS E LUCRO)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mercadorias Vendidas Hoje",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${report.soldProducts.size} itens distintos faturados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (report.soldProducts.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = onNavigateToPos,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_sell_more")
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Vender", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            if (report.soldProducts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ProductionQuantityLimits,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nenhuma mercadoria vendida hoje ainda",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Abra o PDV para registrar novas vendas de bebidas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(onClick = onNavigateToPos, modifier = Modifier.testTag("btn_go_to_pos_from_cash")) {
                                Text("Abrir PDV / Balcão")
                            }
                        }
                    }
                }
            } else {
                items(report.soldProducts, key = { it.productId }) { prod ->
                    SoldProductCard(prod = prod)
                }
            }
        }
    }

    // Dialogs
    if (showCountDialog) {
        CashCountDialog(
            expectedAmount = report.expectedCash,
            initialCounted = report.countedCash,
            initialNotes = report.notes,
            onDismiss = { showCountDialog = false },
            onConfirm = { counted, notes ->
                viewModel.countCash(counted, notes)
                showCountDialog = false
            }
        )
    }

    if (showSupplyDialog) {
        CashMovementDialog(
            isBleeding = false,
            onDismiss = { showSupplyDialog = false },
            onConfirm = { amount, reason ->
                viewModel.addCashSupply(amount, reason)
                showSupplyDialog = false
            }
        )
    }

    if (showBleedingDialog) {
        CashMovementDialog(
            isBleeding = true,
            onDismiss = { showBleedingDialog = false },
            onConfirm = { amount, reason ->
                viewModel.addCashBleeding(amount, reason)
                showBleedingDialog = false
            }
        )
    }

    if (showFloatDialog) {
        OpeningFloatDialog(
            currentFloat = report.openingFloat,
            onDismiss = { showFloatDialog = false },
            onConfirm = { newFloat ->
                viewModel.updateOpeningFloat(newFloat)
                showFloatDialog = false
            }
        )
    }

    if (showCloseConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCloseConfirmDialog = false },
            title = { Text("Fechar Caixa do Dia?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("O fechamento irá consolidar as vendas e travar novas saídas neste turno.")
                    if (report.hasCashShortage) {
                        Text(
                            text = "⚠️ ATENÇÃO: Há uma quebra de caixa registrada no valor de ${Formatters.formatCurrency(kotlin.math.abs(report.cashDifference))}.",
                            color = RedDanger,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = report.countedCash ?: report.expectedCash
                        viewModel.closeCashRegister(count, report.notes)
                        showCloseConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmar Fechamento")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ---------------- Helper Components ----------------

@Composable
private fun CashDiscrepancyIndicatorCard(
    report: DailyCashReport,
    onOpenCountDialog: () -> Unit
) {
    val (bgColor, borderColor, icon, title, subtitle) = when {
        !report.isCounted -> DiscrepancyCardState(
            bgColor = AmberContainer.copy(alpha = 0.4f),
            borderColor = AmberPrimary,
            icon = Icons.Default.HelpOutline,
            title = "Aguardando Conferência da Gaveta",
            subtitle = "Clique para contar as notas e verificar se houve quebra de caixa."
        )
        report.hasCashShortage -> DiscrepancyCardState(
            bgColor = RedDanger.copy(alpha = 0.15f),
            borderColor = RedDanger,
            icon = Icons.Default.Warning,
            title = "TEVE QUEBRA DE CAIXA: Falta de ${Formatters.formatCurrency(kotlin.math.abs(report.cashDifference))}",
            subtitle = "O dinheiro físico contado (R$ ${String.format("%.2f", report.countedCash)}) é menor que o esperado (R$ ${String.format("%.2f", report.expectedCash)})."
        )
        report.hasCashSurplus -> DiscrepancyCardState(
            bgColor = BlueInfo.copy(alpha = 0.15f),
            borderColor = BlueInfo,
            icon = Icons.Default.Info,
            title = "SOBRA DE CAIXA: Excedente de ${Formatters.formatCurrency(report.cashDifference)}",
            subtitle = "O dinheiro físico na gaveta está acima do saldo teórico esperado."
        )
        else -> DiscrepancyCardState(
            bgColor = GreenSuccessContainer.copy(alpha = 0.4f),
            borderColor = GreenSuccessDark,
            icon = Icons.Default.CheckCircle,
            title = "SEM QUEBRA DE CAIXA: Caixa 100% Correto!",
            subtitle = "O dinheiro físico na gaveta bate exatamente com as vendas apuradas."
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenCountDialog() }
            .testTag("card_cash_discrepancy_indicator"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(borderColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class DiscrepancyCardState(
    val bgColor: Color,
    val borderColor: Color,
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Composable
private fun ReconciliationRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isPositive: Boolean = false,
    isNegative: Boolean = false,
    isNeutral: Boolean = false,
    customColor: Color? = null
) {
    val textColor = customColor ?: when {
        isPositive -> GreenSuccessDark
        isNegative -> RedDanger
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun PaymentMethodRow(name: String, amount: Double, total: Double, icon: ImageVector) {
    val percent = if (total > 0) (amount / total) * 100 else 0.0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = Formatters.formatCurrency(amount), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${String.format("%.1f", percent)}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SoldProductCard(prod: DailySoldProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sold_product_${prod.productId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Linha 1: Nome do produto e Embalagem
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prod.productName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Embalagem: ${prod.unitType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Badge(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = "${prod.quantitySold} vendidas hoje",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Linha 2: Preço de Venda, Preço de Custo, Faturamento e Lucro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Preço Unit. Venda", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(Formatters.formatCurrency(prod.unitPrice), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }

                Column {
                    Text("Preço de Custo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(Formatters.formatCurrency(prod.costPrice), style = MaterialTheme.typography.bodyMedium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Subtotal Faturado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(Formatters.formatCurrency(prod.totalRevenue), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Lucro do Item", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = Formatters.formatCurrency(prod.totalProfit),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreenSuccessDark
                    )
                }
            }
        }
    }
}
