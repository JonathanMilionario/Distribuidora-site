package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RedDanger
import com.example.ui.theme.GreenSuccessDark
import com.example.ui.util.Formatters

@Composable
fun CashCountDialog(
    expectedAmount: Double,
    initialCounted: Double?,
    initialNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (counted: Double, notes: String) -> Unit
) {
    var countInput by remember { mutableStateOf(initialCounted?.let { String.format("%.2f", it).replace(",", ".") } ?: "") }
    var notesInput by remember { mutableStateOf(initialNotes) }

    val countedValue = countInput.replace(",", ".").toDoubleOrNull() ?: 0.0
    val diff = countedValue - expectedAmount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Conferência Física do Caixa", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Conte as notas e moedas físicas na gaveta e informe o valor total:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Saldo Esperado no Sistema:", style = MaterialTheme.typography.bodyMedium)
                            Text(Formatters.formatCurrency(expectedAmount), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = countInput,
                    onValueChange = { countInput = it },
                    label = { Text("Valor Total em Dinheiro Contado (R$)") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_counted_cash"),
                    singleLine = true
                )

                if (countInput.isNotBlank()) {
                    val isShortage = diff < -0.05
                    val isSurplus = diff > 0.05
                    val isBalanced = kotlin.math.abs(diff) <= 0.05

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isShortage -> MaterialTheme.colorScheme.errorContainer
                                isSurplus -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = when {
                                    isShortage -> "⚠️ QUEBRA DE CAIXA: Falta de ${Formatters.formatCurrency(kotlin.math.abs(diff))}"
                                    isSurplus -> "ℹ️ SOBRA DE CAIXA: Excedente de ${Formatters.formatCurrency(diff)}"
                                    else -> "✅ SEM QUEBRA: Caixa bate exatamente com o sistema!"
                                },
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isShortage -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Observação / Justificativa (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val counted = countInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                    onConfirm(counted, notesInput)
                },
                modifier = Modifier.testTag("btn_confirm_cash_count")
            ) {
                Text("Salvar Conferência")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CashMovementDialog(
    isBleeding: Boolean, // true = sangria, false = suprimento
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, reason: String) -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var reasonInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = if (isBleeding) Icons.Default.MoneyOff else Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = if (isBleeding) RedDanger else GreenSuccessDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBleeding) "Registrar Sangria (Retirada)" else "Registrar Suprimento (Reforço)",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isBleeding)
                        "Retirada de dinheiro da gaveta para cofre, pagamentos operacionais ou despesas."
                    else
                        "Adição de dinheiro físico na gaveta para reforço de troco.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Valor (R$)") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_movement_amount"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text(if (isBleeding) "Motivo da sangria (ex: Transferência para cofre)" else "Origem do suprimento") },
                    modifier = Modifier.fillMaxWidth().testTag("input_movement_reason"),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, reasonInput)
                    }
                },
                enabled = (amountInput.replace(",", ".").toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("btn_confirm_cash_movement")
            ) {
                Text(if (isBleeding) "Confirmar Sangria" else "Confirmar Suprimento")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun OpeningFloatDialog(
    currentFloat: Double,
    onDismiss: () -> Unit,
    onConfirm: (newFloat: Double) -> Unit
) {
    var floatInput by remember { mutableStateOf(String.format("%.2f", currentFloat).replace(",", ".")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fundo de Troco Inicial", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Defina o valor em dinheiro disponível na abertura do caixa como troco inicial:",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = floatInput,
                    onValueChange = { floatInput = it },
                    label = { Text("Fundo de Troco (R$)") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_opening_float"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = floatInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                    onConfirm(value)
                },
                modifier = Modifier.testTag("btn_confirm_opening_float")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
