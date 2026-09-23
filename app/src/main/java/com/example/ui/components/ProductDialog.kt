package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.ProductEntity
import com.example.ui.BEVERAGE_CATEGORIES
import com.example.ui.BEVERAGE_UNITS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    product: ProductEntity? = null,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: "Cervejas") }
    var barcode by remember { mutableStateOf(product?.barcode ?: "") }
    var selectedUnit by remember { mutableStateOf(product?.unitType ?: "Caixa c/ 24") }
    var costPriceStr by remember { mutableStateOf(if (product != null && product.costPrice > 0) product.costPrice.toString() else "") }
    var salePriceStr by remember { mutableStateOf(if (product != null && product.salePrice > 0) product.salePrice.toString() else "") }
    var stockQuantityStr by remember { mutableStateOf(if (product != null) product.stockQuantity.toString() else "0") }
    var minStockStr by remember { mutableStateOf(if (product != null) product.minStock.toString() else "10") }
    var isReturnable by remember { mutableStateOf(product?.isReturnable ?: false) }
    var supplier by remember { mutableStateOf(product?.supplier ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("product_dialog"),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (product == null) "Novo Produto de Bebida" else "Editar Produto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_product_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Produto / Marca") },
                        placeholder = { Text("Ex: Cerveja Heineken Long Neck 330ml") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_name_input"),
                        singleLine = true
                    )

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoria") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("product_category_select")
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            BEVERAGE_CATEGORIES.filter { it != "Todos" }.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Unit Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = unitDropdownExpanded,
                        onExpandedChange = { unitDropdownExpanded = !unitDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedUnit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Embalagem / Tipo de Unidade") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("product_unit_select")
                        )
                        ExposedDropdownMenu(
                            expanded = unitDropdownExpanded,
                            onDismissRequest = { unitDropdownExpanded = false }
                        ) {
                            BEVERAGE_UNITS.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        selectedUnit = unit
                                        unitDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Barcode & Supplier
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Código de Barras / EAN") },
                        placeholder = { Text("Ex: 7896045506049") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_barcode_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("Fornecedor / Fabricante") },
                        placeholder = { Text("Ex: Ambev, Heineken, Coca-Cola") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_supplier_input"),
                        singleLine = true
                    )

                    // Prices
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = costPriceStr,
                            onValueChange = { costPriceStr = it },
                            label = { Text("Preço Custo (R$)") },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_cost_price_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = salePriceStr,
                            onValueChange = { salePriceStr = it },
                            label = { Text("Preço Venda (R$)") },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_sale_price_input"),
                            singleLine = true
                        )
                    }

                    // Profit Preview Card
                    val parsedCost = costPriceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val parsedSale = salePriceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val parsedQty = stockQuantityStr.toIntOrNull() ?: 0
                    val unitProfit = parsedSale - parsedCost
                    val markupPct = if (parsedCost > 0) (unitProfit / parsedCost) * 100 else 0.0
                    val marginOnSalePct = if (parsedSale > 0) (unitProfit / parsedSale) * 100 else 0.0
                    val totalProjectedProfit = unitProfit * parsedQty

                    if (parsedCost > 0 || parsedSale > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (unitProfit >= 0)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().testTag("product_profit_preview_card")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Previsão de Lucro & Margem",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (unitProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = if (unitProfit >= 0) "+${String.format("%.1f", markupPct)}% Markup" else "Prejuízo!",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (unitProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Lucro por Unidade / Fardo", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = "${if (unitProfit >= 0) "+" else ""}${com.example.ui.util.Formatters.formatCurrency(unitProfit)}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (unitProfit >= 0) com.example.ui.theme.GreenSuccessDark else MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Margem s/ Venda", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = "${String.format("%.1f", marginOnSalePct)}%",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (parsedQty > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Lucro Total no Estoque Atual ($parsedQty un):",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${if (totalProjectedProfit >= 0) "+" else ""}${com.example.ui.util.Formatters.formatCurrency(totalProjectedProfit)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (totalProjectedProfit >= 0) com.example.ui.theme.GreenSuccessDark else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Quantities
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = stockQuantityStr,
                            onValueChange = { stockQuantityStr = it },
                            label = { Text("Estoque Atual") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_stock_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = minStockStr,
                            onValueChange = { minStockStr = it },
                            label = { Text("Estoque Mínimo") },
                            placeholder = { Text("10") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_min_stock_input"),
                            singleLine = true
                        )
                    }

                    // Returnable / Vasilhame toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Embalagem Retornável (Casco/Vasilhame)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ative para garrafas 600ml ou barris com controle de vasilhames",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isReturnable,
                            onCheckedChange = { isReturnable = it },
                            modifier = Modifier.testTag("product_returnable_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_product_btn")
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "O nome do produto é obrigatório."
                                return@Button
                            }
                            val cost = costPriceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val sale = salePriceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val stock = stockQuantityStr.toIntOrNull() ?: 0
                            val minStock = minStockStr.toIntOrNull() ?: 10

                            val updated = ProductEntity(
                                id = product?.id ?: 0L,
                                name = name.trim(),
                                category = selectedCategory,
                                barcode = barcode.trim(),
                                unitType = selectedUnit,
                                costPrice = cost,
                                salePrice = sale,
                                stockQuantity = stock,
                                minStock = minStock,
                                isReturnable = isReturnable,
                                supplier = supplier.trim()
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("save_product_btn")
                    ) {
                        Text("Salvar Produto")
                    }
                }
            }
        }
    }
}
