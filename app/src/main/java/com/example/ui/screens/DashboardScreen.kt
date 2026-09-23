package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleOrderEntity
import com.example.ui.DistriBebidasViewModel
import com.example.ui.theme.*
import com.example.ui.util.Formatters

@Composable
fun DashboardScreen(
    viewModel: DistriBebidasViewModel,
    onNavigateToPos: () -> Unit,
    onNavigateToDailyCash: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToClients: () -> Unit,
    onOpenAddProduct: () -> Unit,
    onOpenAddClient: () -> Unit,
    onQuickRestock: (ProductEntity) -> Unit
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val dailyReport by viewModel.dailyCashReport.collectAsStateWithLifecycle()

    val totalSalesAmount = orders.sumOf { it.totalAmount }
    val totalSalesCost = orders.sumOf { it.totalCost }
    val totalProfit = (totalSalesAmount - totalSalesCost).coerceAtLeast(0.0)
    val totalReceivables = clients.sumOf { it.creditBalance }
    val totalReturnablesPending = clients.sumOf { it.returnableBottlesPending }
    val totalStockUnits = products.sumOf { it.stockQuantity }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "DistriBebidas Gestão",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Painel Geral & Controle Operacional",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsBar,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeaderBadge(
                                label = "Estoque Total",
                                value = "$totalStockUnits un",
                                modifier = Modifier.weight(1f)
                            )
                            HeaderBadge(
                                label = "Cascos no Mercado",
                                value = "$totalReturnablesPending un",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Card de Acesso ao Sistema Web
        item {
            val context = LocalContext.current
            val clipboardManager = LocalClipboardManager.current
            val webUrl = "https://ais-dev-wdyzjysgxsioufqonqnvo5-843185410408.us-east1.run.app"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_web_access"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Acesso ao Sistema Web",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Disponível no navegador (PC / Celular)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                                context.startActivity(intent)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_open_web_browser")
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Abrir Site")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = webUrl,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(webUrl))
                                Toast.makeText(context, "Link copiado!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp).testTag("btn_copy_web_url")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar link", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Fast Action Buttons Row
        item {
            Text(
                text = "Ações Rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.PointOfSale,
                    label = "Nova Venda",
                    color = AmberPrimary,
                    modifier = Modifier.weight(1f).testTag("quick_action_pos"),
                    onClick = onNavigateToPos
                )
                QuickActionButton(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Caixa do Dia",
                    color = GreenSuccessDark,
                    modifier = Modifier.weight(1f).testTag("quick_action_daily_cash"),
                    onClick = onNavigateToDailyCash
                )
                QuickActionButton(
                    icon = Icons.Default.AddBox,
                    label = "Novo Produto",
                    color = BlueInfo,
                    modifier = Modifier.weight(1f).testTag("quick_action_add_product"),
                    onClick = onOpenAddProduct
                )
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "Novo Cliente",
                    color = AmberDark,
                    modifier = Modifier.weight(1f).testTag("quick_action_add_client"),
                    onClick = onOpenAddClient
                )
            }
        }

        // Indicador de Caixa do Dia (Daily Cash Register Indicator)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDailyCash() }
                    .testTag("dashboard_daily_cash_card"),
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AmberPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PriceCheck,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Indicador de Caixa do Dia",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Data: ${dailyReport.dateKey} • Status: ${dailyReport.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        AssistChip(
                            onClick = onNavigateToDailyCash,
                            label = { Text("Conferir", fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // INDICADOR CRÍTICO: TEVE QUEBRA DE CAIXA?
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    !dailyReport.isCounted -> AmberContainer.copy(alpha = 0.4f)
                                    dailyReport.hasCashShortage -> RedDanger.copy(alpha = 0.15f)
                                    dailyReport.hasCashSurplus -> BlueInfo.copy(alpha = 0.15f)
                                    else -> GreenSuccessContainer.copy(alpha = 0.5f)
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                !dailyReport.isCounted -> Icons.Default.HelpOutline
                                dailyReport.hasCashShortage -> Icons.Default.Warning
                                dailyReport.hasCashSurplus -> Icons.Default.Info
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when {
                                !dailyReport.isCounted -> AmberPrimary
                                dailyReport.hasCashShortage -> RedDanger
                                dailyReport.hasCashSurplus -> BlueInfo
                                else -> GreenSuccessDark
                            },
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when {
                                    !dailyReport.isCounted -> "Conferência de gaveta pendente"
                                    dailyReport.hasCashShortage -> "TEVE QUEBRA DE CAIXA: Falta de ${Formatters.formatCurrency(kotlin.math.abs(dailyReport.cashDifference))}"
                                    dailyReport.hasCashSurplus -> "Sobra de Caixa: +${Formatters.formatCurrency(dailyReport.cashDifference)}"
                                    else -> "SEM QUEBRA DE CAIXA (Saldo 100% Correto)"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    !dailyReport.isCounted -> MaterialTheme.colorScheme.onSurface
                                    dailyReport.hasCashShortage -> RedDanger
                                    dailyReport.hasCashSurplus -> BlueInfo
                                    else -> GreenSuccessDark
                                }
                            )
                            Text(
                                text = when {
                                    !dailyReport.isCounted -> "Clique para informar a contagem de notas na gaveta."
                                    dailyReport.hasCashShortage -> "O dinheiro físico contado foi menor que o saldo esperado pelo sistema."
                                    dailyReport.hasCashSurplus -> "O valor físico contado superou o esperado."
                                    else -> "O valor físico contado na gaveta confere com o sistema."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Linha com Total de Vendas Hoje e Lucro do Dia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Total de Vendas Hoje",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatCurrency(dailyReport.totalSales),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccessDark
                            )
                            Text(
                                text = "${dailyReport.totalOrdersCount} vendas realizadas",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Lucro do Dia",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatCurrency(dailyReport.totalProfit),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary
                            )
                            Text(
                                text = if (dailyReport.totalSales > 0)
                                    "Margem: ${String.format("%.1f", (dailyReport.totalProfit / dailyReport.totalSales) * 100)}%"
                                else "Sem margem",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Resumo das Mercadorias Vendidas Hoje
                    if (dailyReport.soldProducts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Mercadorias Vendidas no Dia (${dailyReport.soldProducts.size} itens):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        dailyReport.soldProducts.take(3).forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${prod.quantitySold}x ${prod.productName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Preço Unit: ${Formatters.formatCurrency(prod.unitPrice)} • Custo: ${Formatters.formatCurrency(prod.costPrice)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = Formatters.formatCurrency(prod.totalRevenue),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Lucro: ${Formatters.formatCurrency(prod.totalProfit)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GreenSuccessDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        if (dailyReport.soldProducts.size > 3) {
                            Text(
                                text = "+ mais ${dailyReport.soldProducts.size - 3} mercadorias vendidas...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onNavigateToDailyCash,
                        modifier = Modifier.align(Alignment.End).testTag("btn_see_all_cash_details")
                    ) {
                        Text("Ver Relatório Completo do Caixa →")
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Text(
                text = "Indicadores Financeiros & Estoque",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Faturamento Total",
                        value = Formatters.formatCurrency(totalSalesAmount),
                        subtitle = "${orders.size} pedidos registrados",
                        icon = Icons.Default.TrendingUp,
                        iconTint = GreenSuccess,
                        modifier = Modifier.weight(1f).testTag("metric_total_sales"),
                        onClick = onNavigateToOrders
                    )
                    MetricCard(
                        title = "Lucro Bruto Est.",
                        value = Formatters.formatCurrency(totalProfit),
                        subtitle = "Margem pós-custo",
                        icon = Icons.Default.MonetizationOn,
                        iconTint = AmberPrimary,
                        modifier = Modifier.weight(1f).testTag("metric_profit")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "A Receber (Fiado)",
                        value = Formatters.formatCurrency(totalReceivables),
                        subtitle = "${clients.count { it.creditBalance > 0 }} clientes pendentes",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = if (totalReceivables > 0) RedDanger else GreenSuccess,
                        modifier = Modifier.weight(1f).testTag("metric_receivables"),
                        onClick = onNavigateToClients
                    )
                    MetricCard(
                        title = "Estoque Crítico",
                        value = "${lowStockProducts.size} itens",
                        subtitle = if (lowStockProducts.isEmpty()) "Tudo normal" else "Abaixo do mínimo!",
                        icon = Icons.Default.WarningAmber,
                        iconTint = if (lowStockProducts.isEmpty()) GreenSuccess else RedDanger,
                        modifier = Modifier.weight(1f).testTag("metric_low_stock"),
                        onClick = onNavigateToInventory
                    )
                }
            }
        }

        // Low Stock Alert Banner (if any item is below minimum)
        if (lowStockProducts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("low_stock_alert_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Atenção: Bebidas com Estoque Baixo!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reponha os itens abaixo antes que esgotem nas vendas:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        lowStockProducts.take(3).forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prod.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Restam: ${prod.stockQuantity} ${prod.unitType} (Mín: ${prod.minStock})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Button(
                                    onClick = { onQuickRestock(prod) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("restock_btn_${prod.id}")
                                ) {
                                    Text("+ Repor", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            if (prod != lowStockProducts.take(3).last()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }

        // Recent Orders Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimas Vendas & Pedidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = onNavigateToOrders,
                    modifier = Modifier.testTag("see_all_orders_btn")
                ) {
                    Text("Ver todos")
                }
            }

            if (orders.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Nenhuma venda registrada ainda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    orders.take(4).forEach { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToOrders() }
                                .testTag("recent_order_${order.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${order.orderNumber} - ${order.clientName}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${Formatters.formatDateTime(order.timestamp)} • ${order.paymentMethod}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = Formatters.formatCurrency(order.totalAmount),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = order.orderStatus,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
