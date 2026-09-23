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
import com.example.data.entity.ClientEntity

@Composable
fun ClientDialog(
    client: ClientEntity? = null,
    onDismiss: () -> Unit,
    onSave: (ClientEntity) -> Unit
) {
    var name by remember { mutableStateOf(client?.name ?: "") }
    var tradeName by remember { mutableStateOf(client?.tradeName ?: "") }
    var document by remember { mutableStateOf(client?.document ?: "") }
    var phone by remember { mutableStateOf(client?.phone ?: "") }
    var address by remember { mutableStateOf(client?.address ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("client_dialog"),
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
                        text = if (client == null) "Novo Cliente / Estabelecimento" else "Editar Cliente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_client_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
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

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Fantasia / Bar / Restaurante") },
                        placeholder = { Text("Ex: Bar e Mercearia do Zé") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tradeName,
                        onValueChange = { tradeName = it },
                        label = { Text("Razão Social / Nome Completo") },
                        placeholder = { Text("Ex: José Pereira Alimentos ME") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_tradename_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = document,
                        onValueChange = { document = it },
                        label = { Text("CNPJ ou CPF") },
                        placeholder = { Text("Ex: 12.345.678/0001-90") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_doc_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone / WhatsApp") },
                        placeholder = { Text("Ex: (11) 98765-4321") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_phone_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Endereço de Entrega Completo") },
                        placeholder = { Text("Rua, Número, Bairro, Ponto de ref.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_address_input"),
                        minLines = 2,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            if (name.isBlank()) {
                                errorMessage = "O nome do cliente é obrigatório."
                                return@Button
                            }
                            val updated = ClientEntity(
                                id = client?.id ?: 0L,
                                name = name.trim(),
                                tradeName = tradeName.trim(),
                                document = document.trim(),
                                phone = phone.trim(),
                                address = address.trim(),
                                creditBalance = client?.creditBalance ?: 0.0,
                                returnableBottlesPending = client?.returnableBottlesPending ?: 0
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("save_client_btn")
                    ) {
                        Text("Salvar Cliente")
                    }
                }
            }
        }
    }
}
