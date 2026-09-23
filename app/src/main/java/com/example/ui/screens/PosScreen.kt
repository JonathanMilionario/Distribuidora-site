package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.ui.BEVERAGE_CATEGORIES
import com.example.ui.DistriBebidasViewModel
import com.example.ui.theme.*
import com.example.ui.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    viewModel: DistriBebidasViewModel,
    onNavigateBack: () -> Unit
) {
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val productSearchQuery by viewModel.productSearchQuery.collectAsStateWithLifecycle()
    val posState by viewModel.posState.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()

    var showCartBottomSheet by remember { mutableStateOf(false) }
    var showClientPickerModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PDV - Nova Venda", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = posState.selectedClient?.name ?: "Consumidor Balcão",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("pos_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showClientPickerModal = true },
                        modifier = Modifier.testTag("pos_select_client_btn")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Selecionar Cliente")
                    }
                    if (posState.cartItems.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearCart() },
                            modifier = Modifier.testTag("pos_clear_cart_btn")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Limpar Carrinho")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (posState.cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.testTag("pos_bottom_cart_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${posState.totalItemCount} itens no carrinho",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatCurrency(posState.total),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { showCartBottomSheet = true },
                            modifier = Modifier.testTag("open_cart_sheet_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Carrinho")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Client Status Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { showClientPickerModal = true }
                    .testTag("pos_client_status_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = posState.selectedClient?.name ?: "Consumidor Balcão (À Vista)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (posState.selectedClient != null && posState.selectedClient!!.creditBalance > 0) {
                                Text(
                                    text = "Possui dívida pendente: ${Formatters.formatCurrency(posState.selectedClient!!.creditBalance)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "Alterar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = productSearchQuery,
                onValueChange = { viewModel.productSearchQuery.value = it },
                placeholder = { Text("Buscar bebidas por nome, código ou marca...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (productSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.productSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar busca")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("pos_search_product_input")
            )

            // Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BEVERAGE_CATEGORIES) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { viewModel.selectedCategory.value = cat },
                        label = { Text(cat) },
                        modifier = Modifier.testTag("pos_cat_chip_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Products List
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhuma bebida encontrada.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        val cartItem = posState.cartItems.find { it.product.id == product.id }
                        val inCartQty = cartItem?.quantity ?: 0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pos_product_item_${product.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (inCartQty > 0)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (product.isReturnable) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = PurpleKeg.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Retornável",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = PurpleKeg,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = product.unitType,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Estoque: ${product.stockQuantity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (product.stockQuantity <= product.minStock)
                                                MaterialTheme.colorScheme.error
                                            else GreenSuccessDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = Formatters.formatCurrency(product.salePrice),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Add / Stepper controls
                                if (inCartQty > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        FilledTonalIconButton(
                                            onClick = { viewModel.updateCartItemQuantity(product.id, -1) },
                                            modifier = Modifier.size(36.dp).testTag("pos_minus_btn_${product.id}")
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Diminuir", modifier = Modifier.size(18.dp))
                                        }

                                        Text(
                                            text = "$inCartQty",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )

                                        FilledTonalIconButton(
                                            onClick = { viewModel.updateCartItemQuantity(product.id, 1) },
                                            enabled = inCartQty < product.stockQuantity,
                                            modifier = Modifier.size(36.dp).testTag("pos_plus_btn_${product.id}")
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.addToCart(product) },
                                        enabled = product.stockQuantity > 0,
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("pos_add_to_cart_btn_${product.id}")
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (product.stockQuantity > 0) "Adicionar" else "Esgotado")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Select Client
    if (showClientPickerModal) {
        AlertDialog(
            onDismissRequest = { showClientPickerModal = false },
            title = { Text("Selecionar Cliente da Venda") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    TextButton(
                        onClick = {
                            viewModel.setPosClient(null)
                            showClientPickerModal = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("select_balcao_client_btn")
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonOutline, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consumidor Final (Balcão - Sem cadastro)")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(clients) { client ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setPosClient(client)
                                        showClientPickerModal = false
                                    }
                                    .testTag("client_picker_item_${client.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (posState.selectedClient?.id == client.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(client.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    if (client.tradeName.isNotBlank()) {
                                        Text(client.tradeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (client.creditBalance > 0) {
                                        Text(
                                            "Saldo Devedor: ${Formatters.formatCurrency(client.creditBalance)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showClientPickerModal = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    // Modal / BottomSheet: Complete Sale Checkout
    if (showCartBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartBottomSheet = false },
            modifier = Modifier.testTag("checkout_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Carrinho e Fechamento de Venda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cart items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(posState.cartItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${item.quantity}x ${Formatters.formatCurrency(item.unitPrice)} = ${Formatters.formatCurrency(item.subtotal)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartItemQuantity(item.product.id, -1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
                                }
                                Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                                IconButton(
                                    onClick = { viewModel.updateCartItemQuantity(item.product.id, 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Mais", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.removeCartItem(item.product.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selector
                Text(
                    text = "Forma de Pagamento:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val methods = listOf(
                        "PIX" to "PIX Instantâneo",
                        "DINHEIRO" to "Dinheiro",
                        "CARTAO_CREDITO" to "Crédito",
                        "CARTAO_DEBITO" to "Débito",
                        "FATURADO_BOLETO" to "A Prazo (Boleto/Fiado)"
                    )
                    items(methods) { (key, label) ->
                        FilterChip(
                            selected = posState.paymentMethod == key,
                            onClick = { viewModel.setPosPaymentMethod(key) },
                            label = { Text(label) },
                            modifier = Modifier.testTag("payment_method_$key")
                        )
                    }
                }

                // Delivery option
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Entrega no Endereço do Cliente", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = posState.isDelivery,
                        onCheckedChange = { viewModel.setPosDelivery(it) },
                        modifier = Modifier.testTag("pos_delivery_switch")
                    )
                }

                if (posState.isDelivery) {
                    OutlinedTextField(
                        value = posState.deliveryAddress,
                        onValueChange = { viewModel.setPosDeliveryAddress(it) },
                        label = { Text("Endereço de Entrega") },
                        modifier = Modifier.fillMaxWidth().testTag("pos_delivery_address_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Discount Input
                var discountStr by remember { mutableStateOf(if (posState.discount > 0) posState.discount.toString() else "") }
                OutlinedTextField(
                    value = discountStr,
                    onValueChange = {
                        discountStr = it
                        val disc = it.replace(",", ".").toDoubleOrNull() ?: 0.0
                        viewModel.setPosDiscount(disc)
                    },
                    label = { Text("Desconto (R$)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("pos_discount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Summary Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Final a Pagar:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        Formatters.formatCurrency(posState.total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showCartBottomSheet = false
                        viewModel.completeSale()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_and_complete_sale_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finalizar Venda & Baixar Estoque", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
