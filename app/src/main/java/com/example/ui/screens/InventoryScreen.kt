package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ProductEntity
import com.example.ui.BEVERAGE_CATEGORIES
import com.example.ui.DistriBebidasViewModel
import com.example.ui.theme.*
import com.example.ui.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: DistriBebidasViewModel,
    onAddNewProduct: () -> Unit,
    onEditProduct: (ProductEntity) -> Unit,
    onAdjustStock: (ProductEntity, String) -> Unit
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val productSearchQuery by viewModel.productSearchQuery.collectAsStateWithLifecycle()

    var filterOnlyCritical by remember { mutableStateOf(false) }
    var filterOnlyReturnables by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("PADRAO") } // PADRAO, MAIOR_LUCRO_UNIT, MAIOR_LUCRO_TOTAL, MAIOR_MARGEM
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val baseFiltered = filteredProducts.filter {
        (!filterOnlyCritical || it.stockQuantity <= it.minStock) &&
        (!filterOnlyReturnables || it.isReturnable)
    }

    val displayedProducts = remember(baseFiltered, sortOption) {
        when (sortOption) {
            "MAIOR_LUCRO_UNIT" -> baseFiltered.sortedByDescending { it.salePrice - it.costPrice }
            "MAIOR_LUCRO_TOTAL" -> baseFiltered.sortedByDescending { (it.salePrice - it.costPrice) * it.stockQuantity }
            "MAIOR_MARGEM" -> baseFiltered.sortedByDescending {
                if (it.costPrice > 0) ((it.salePrice - it.costPrice) / it.costPrice) else 0.0
            }
            "MAIOR_CUSTO" -> baseFiltered.sortedByDescending { it.costPrice }
            else -> baseFiltered.sortedBy { it.name }
        }
    }

    val totalCostValue = products.sumOf { it.costPrice * it.stockQuantity }
    val totalSaleValue = products.sumOf { it.salePrice * it.stockQuantity }
    val totalProfitValue = (totalSaleValue - totalCostValue).coerceAtLeast(0.0)
    val averageMargin = if (totalCostValue > 0) ((totalProfitValue / totalCostValue) * 100) else 0.0
    val totalStockItems = products.sumOf { it.stockQuantity }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewProduct,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Cadastrar Bebida")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Inventory Value & Profit Analysis Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_summary_card"),
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
                            Text(
                                text = "Gestão de Custos & Lucro do Estoque",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = GreenSuccess.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+${String.format("%.1f", averageMargin)}% Margem Média",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccessDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid 2x2 of financial performance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Custo Total Investido",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = Formatters.formatCurrency(totalCostValue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Patrimônio estocado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Venda Total Prevista",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = Formatters.formatCurrency(totalSaleValue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Faturamento potencial",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Highlighted Total Projected Profit Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreenSuccess.copy(alpha = 0.12f))
                                .border(1.dp, GreenSuccess.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = GreenSuccessDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Lucro Bruto Previsto no Estoque:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenSuccessDark
                                        )
                                        Text(
                                            text = "${products.size} produtos (${totalStockItems} unidades totais)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = "+ ${Formatters.formatCurrency(totalProfitValue)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GreenSuccessDark
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = productSearchQuery,
                    onValueChange = { viewModel.productSearchQuery.value = it },
                    placeholder = { Text("Buscar bebidas no estoque...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (productSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.productSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_search_input")
                )
            }

            // Category Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BEVERAGE_CATEGORIES) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.selectedCategory.value = cat },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("inventory_cat_$cat")
                        )
                    }
                }
            }

            // Filters & Sort By Profit/Margin Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Ordenar e Filtrar Lucratividade:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = sortOption == "MAIOR_LUCRO_UNIT",
                                onClick = {
                                    sortOption = if (sortOption == "MAIOR_LUCRO_UNIT") "PADRAO" else "MAIOR_LUCRO_UNIT"
                                },
                                label = { Text("💰 Maior Lucro Unitário") },
                                modifier = Modifier.testTag("sort_profit_unit")
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == "MAIOR_LUCRO_TOTAL",
                                onClick = {
                                    sortOption = if (sortOption == "MAIOR_LUCRO_TOTAL") "PADRAO" else "MAIOR_LUCRO_TOTAL"
                                },
                                label = { Text("📈 Maior Lucro em Estoque") },
                                modifier = Modifier.testTag("sort_profit_total")
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == "MAIOR_MARGEM",
                                onClick = {
                                    sortOption = if (sortOption == "MAIOR_MARGEM") "PADRAO" else "MAIOR_MARGEM"
                                },
                                label = { Text("📊 Maior Margem %") },
                                modifier = Modifier.testTag("sort_margin")
                            )
                        }
                        item {
                            FilterChip(
                                selected = filterOnlyCritical,
                                onClick = { filterOnlyCritical = !filterOnlyCritical },
                                label = { Text("⚠️ Estoque Baixo") },
                                modifier = Modifier.testTag("filter_critical_stock")
                            )
                        }
                        item {
                            FilterChip(
                                selected = filterOnlyReturnables,
                                onClick = { filterOnlyReturnables = !filterOnlyReturnables },
                                label = { Text("🔄 Retornáveis") },
                                modifier = Modifier.testTag("filter_returnables")
                            )
                        }
                    }
                }
            }

            // Empty State
            if (displayedProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Liquor,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhuma bebida cadastrada neste filtro.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Products List with Detailed Cost vs Sale Price & Profit
            items(displayedProducts, key = { it.id }) { product ->
                val isCritical = product.stockQuantity <= product.minStock
                val unitProfit = product.salePrice - product.costPrice
                val markupPct = if (product.costPrice > 0) ((unitProfit / product.costPrice) * 100) else 0.0
                val marginOnSale = if (product.salePrice > 0) ((unitProfit / product.salePrice) * 100) else 0.0
                val totalStockCost = product.costPrice * product.stockQuantity
                val totalStockSale = product.salePrice * product.stockQuantity
                val totalStockProfit = unitProfit * product.stockQuantity

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_item_${product.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Title row with stock badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (product.isReturnable) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = PurpleKeg.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Casco Retornável",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PurpleKeg,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${product.category} • ${product.unitType}${if (product.supplier.isNotBlank()) " • ${product.supplier}" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (product.barcode.isNotBlank()) {
                                    Text(
                                        text = "Cód: ${product.barcode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Stock status pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isCritical) RedDanger.copy(alpha = 0.15f)
                                        else GreenSuccess.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${product.stockQuantity} un",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCritical) RedDanger else GreenSuccessDark
                                    )
                                    Text(
                                        text = if (isCritical) "Mín: ${product.minStock}" else "OK",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isCritical) RedDanger else GreenSuccessDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // COST vs SALE COMPARISON & PROFIT CONTAINER
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Unit Price Comparison Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Cost
                                    Column {
                                        Text(
                                            text = "Preço de Custo",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = Formatters.formatCurrency(product.costPrice),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Sale Price
                                    Column {
                                        Text(
                                            text = "Preço de Venda",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = Formatters.formatCurrency(product.salePrice),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Unit Profit Badge
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Lucro por Unidade",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (unitProfit >= 0) GreenSuccessDark else RedDanger
                                        )
                                        Text(
                                            text = "${if (unitProfit >= 0) "+" else ""}${Formatters.formatCurrency(unitProfit)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (unitProfit >= 0) GreenSuccessDark else RedDanger
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Margins Badges
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = if (markupPct >= 0) GreenSuccess.copy(alpha = 0.12f) else RedDanger.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Markup s/ Custo:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                "${if (markupPct >= 0) "+" else ""}${String.format("%.1f", markupPct)}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (markupPct >= 0) GreenSuccessDark else RedDanger
                                            )
                                        }
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Margem s/ Venda:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                "${String.format("%.1f", marginOnSale)}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Total in Stock: Cost, Sale, and Total Profit Projection
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Estoque Total (${product.stockQuantity} un):",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Custo: ${Formatters.formatCurrency(totalStockCost)} | Venda: ${Formatters.formatCurrency(totalStockSale)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Lucro no Estoque",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenSuccessDark
                                        )
                                        Text(
                                            text = "${if (totalStockProfit >= 0) "+" else ""}${Formatters.formatCurrency(totalStockProfit)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (totalStockProfit >= 0) GreenSuccessDark else RedDanger
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: + Entrada, - Saída, Editar, Excluir
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { onAdjustStock(product, "ENTRADA") },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccessDark),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("stock_entry_btn_${product.id}")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Carga", style = MaterialTheme.typography.labelMedium)
                                }

                                FilledTonalButton(
                                    onClick = { onAdjustStock(product, "SAIDA") },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("stock_exit_btn_${product.id}")
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("- Avaria", style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = { onEditProduct(product) },
                                    modifier = Modifier.testTag("edit_product_${product.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
                                }
                                IconButton(
                                    onClick = { productToDelete = product },
                                    modifier = Modifier.testTag("delete_product_${product.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Excluir",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Excluir Produto") },
            text = { Text("Deseja realmente remover '${prod.name}' do catálogo da distribuidora?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(prod)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_product_btn")
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
