package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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

@Composable
fun StockAdjustDialog(
    product: ProductEntity,
    initialType: String = "ENTRADA", // ENTRADA, SAIDA, AJUSTE
    onDismiss: () -> Unit,
    onConfirm: (delta: Int, type: String, reason: String) -> Unit
) {
    var movementType by remember { mutableStateOf(initialType) }
    var quantityStr by remember { mutableStateOf("") }
    var reason by remember {
        mutableStateOf(
            if (initialType == "ENTRADA") "Entrada de nota fiscal / Fornecedor"
            else "Avaria / Quebra de garrafa"
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("stock_adjust_dialog"),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Movimentar Estoque",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_stock_adjust_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product info badge with Cost vs Sale Price & Profit
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Estoque Atual: ${product.stockQuantity} ${product.unitType}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Mín: ${product.minStock}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Financial overview per unit
                        val unitProfit = product.salePrice - product.costPrice
                        val markupPct = if (product.costPrice > 0) (unitProfit / product.costPrice) * 100 else 0.0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Custo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    com.example.ui.util.Formatters.formatCurrency(product.costPrice),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text("Venda", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    com.example.ui.util.Formatters.formatCurrency(product.salePrice),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Lucro Unitário", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "+${com.example.ui.util.Formatters.formatCurrency(unitProfit)} (+${String.format("%.1f", markupPct)}%)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = com.example.ui.theme.GreenSuccessDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type selector
                Text(
                    text = "Tipo de Operação:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = movementType == "ENTRADA",
                        onClick = {
                            movementType = "ENTRADA"
                            reason = "Entrada de nota fiscal / Fornecedor"
                        },
                        label = { Text("+ Entrada (Compra)") },
                        modifier = Modifier.weight(1f).testTag("chip_stock_entry")
                    )
                    FilterChip(
                        selected = movementType == "SAIDA",
                        onClick = {
                            movementType = "SAIDA"
                            reason = "Avaria / Quebra / Validade"
                        },
                        label = { Text("- Saída (Perda)") },
                        modifier = Modifier.weight(1f).testTag("chip_stock_exit")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Quantidade (${product.unitType})") },
                    placeholder = { Text("Ex: 10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stock_adjust_quantity_input"),
                    singleLine = true
                )

                val inputQty = quantityStr.toIntOrNull() ?: 0
                if (inputQty > 0) {
                    val batchCost = product.costPrice * inputQty
                    val batchSale = product.salePrice * inputQty
                    val batchProfit = (product.salePrice - product.costPrice) * inputQty

                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (movementType == "ENTRADA")
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (movementType == "ENTRADA") "Impacto Financeiro da Carga:" else "Prejuízo da Avaria / Perda:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (movementType == "ENTRADA") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Custo ($inputQty un):", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    com.example.ui.util.Formatters.formatCurrency(batchCost),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (movementType == "ENTRADA") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Venda Projetada:", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        com.example.ui.util.Formatters.formatCurrency(batchSale),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Lucro a Gerar:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text(
                                        "+${com.example.ui.util.Formatters.formatCurrency(batchProfit)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = com.example.ui.theme.GreenSuccessDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motivo / Observação / NF") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stock_adjust_reason_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val qty = quantityStr.toIntOrNull()
                            if (qty == null || qty <= 0) {
                                errorMessage = "Informe uma quantidade válida maior que zero."
                                return@Button
                            }
                            if (movementType == "SAIDA" && qty > product.stockQuantity) {
                                errorMessage = "Quantidade de saída maior que o estoque atual (${product.stockQuantity})."
                                return@Button
                            }
                            val delta = if (movementType == "ENTRADA") qty else -qty
                            onConfirm(delta, movementType, reason.trim())
                        },
                        modifier = Modifier.testTag("confirm_stock_adjust_btn")
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}
