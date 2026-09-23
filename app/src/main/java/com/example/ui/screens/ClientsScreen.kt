package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ClientEntity
import com.example.ui.DistriBebidasViewModel
import com.example.ui.theme.*
import com.example.ui.util.Formatters

@Composable
fun ClientsScreen(
    viewModel: DistriBebidasViewModel,
    onAddNewClient: () -> Unit,
    onEditClient: (ClientEntity) -> Unit,
    onReceivePayment: (ClientEntity) -> Unit,
    onReturnBottles: (ClientEntity) -> Unit
) {
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val filteredClients by viewModel.filteredClients.collectAsStateWithLifecycle()
    val searchQuery by viewModel.clientSearchQuery.collectAsStateWithLifecycle()

    var clientToDelete by remember { mutableStateOf<ClientEntity?>(null) }

    val totalReceivables = clients.sumOf { it.creditBalance }
    val totalPendingBottles = clients.sumOf { it.returnableBottlesPending }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewClient,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("add_client_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Cadastrar Cliente")
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
            // Header Financial Summary
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clients_summary_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Carteira de Clientes & Cobrança",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Clientes Ativos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${clients.size} cadastros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Total Fiado / A Receber", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    Formatters.formatCurrency(totalReceivables),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalReceivables > 0) RedDanger else GreenSuccessDark
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Cascos com Clientes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$totalPendingBottles un",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleKeg
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.clientSearchQuery.value = it },
                    placeholder = { Text("Buscar cliente por nome, CNPJ, telefone...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clientSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clients_search_input")
                )
            }

            // Empty state
            if (filteredClients.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PersonOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhum cliente cadastrado com esse critério.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Client Cards
            items(filteredClients, key = { it.id }) { client ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_item_${client.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = client.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (client.tradeName.isNotBlank()) {
                                    Text(
                                        text = client.tradeName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (client.document.isNotBlank()) {
                                    Text(
                                        text = "Doc: ${client.document}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = { onEditClient(client) },
                                    modifier = Modifier.testTag("edit_client_btn_${client.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
                                }
                                IconButton(
                                    onClick = { clientToDelete = client },
                                    modifier = Modifier.testTag("delete_client_btn_${client.id}")
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

                        if (client.phone.isNotBlank() || client.address.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            if (client.phone.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(client.phone, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            if (client.address.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(client.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pending balances
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Saldo Devedor:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = Formatters.formatCurrency(client.creditBalance),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (client.creditBalance > 0) RedDanger else GreenSuccessDark
                                )
                            }

                            Column {
                                Text("Cascos Pendentes:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${client.returnableBottlesPending} un",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleKeg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions: Receber pagamento / Devolver cascos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (client.creditBalance > 0) {
                                Button(
                                    onClick = { onReceivePayment(client) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccessDark),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f).testTag("pay_client_btn_${client.id}")
                                ) {
                                    Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Receber Pagamento", style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            if (client.returnableBottlesPending > 0) {
                                FilledTonalButton(
                                    onClick = { onReturnBottles(client) },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f).testTag("return_bottles_btn_${client.id}")
                                ) {
                                    Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Devolver Cascos", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation
    clientToDelete?.let { client ->
        AlertDialog(
            onDismissRequest = { clientToDelete = null },
            title = { Text("Excluir Cliente") },
            text = { Text("Deseja remover '${client.name}' da lista de clientes?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteClient(client)
                        clientToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_client_btn")
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { clientToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
