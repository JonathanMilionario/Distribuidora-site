package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.ui.DistriBebidasViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

enum class AppDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Início", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    POS("PDV", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale, "nav_pos"),
    CASH("Caixa", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_cash"),
    INVENTORY("Estoque", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, "nav_inventory"),
    ORDERS("Pedidos", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_orders"),
    CLIENTS("Clientes", Icons.Filled.People, Icons.Outlined.People, "nav_clients"),
    AUDIT("Auditoria", Icons.Filled.History, Icons.Outlined.History, "nav_audit")
}

class MainActivity : ComponentActivity() {
    private val viewModel: DistriBebidasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DistriBebidasApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistriBebidasApp(viewModel: DistriBebidasViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    // Dialog States
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var isProductDialogOpen by remember { mutableStateOf(false) }

    var stockAdjustProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var stockAdjustType by remember { mutableStateOf("ENTRADA") }

    var clientToEdit by remember { mutableStateOf<ClientEntity?>(null) }
    var isClientDialogOpen by remember { mutableStateOf(false) }

    var clientForPayment by remember { mutableStateOf<ClientEntity?>(null) }
    var clientForBottles by remember { mutableStateOf<ClientEntity?>(null) }

    // Observed States
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val completedReceipt by viewModel.completedOrderReceipt.collectAsStateWithLifecycle()
    val selectedDetailOrder by viewModel.selectedDetailOrder.collectAsStateWithLifecycle()
    val selectedDetailOrderItems by viewModel.selectedDetailOrderItems.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (currentDestination != AppDestination.POS && currentDestination != AppDestination.CASH) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentDestination) {
                                AppDestination.DASHBOARD -> "DistriBebidas"
                                AppDestination.INVENTORY -> "Controle de Estoque"
                                AppDestination.ORDERS -> "Vendas & Pedidos"
                                AppDestination.CLIENTS -> "Gestão de Clientes"
                                AppDestination.AUDIT -> "Auditoria de Estoque"
                                else -> "DistriBebidas"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ais-dev-wdyzjysgxsioufqonqnvo5-843185410408.us-east1.run.app"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.testTag("top_bar_open_web")
                        ) {
                            Icon(Icons.Default.Language, contentDescription = "Acessar Site Web")
                        }
                        if (currentDestination == AppDestination.INVENTORY) {
                            IconButton(
                                onClick = {
                                    productToEdit = null
                                    isProductDialogOpen = true
                                },
                                modifier = Modifier.testTag("top_bar_add_product")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Novo Produto")
                            }
                        } else if (currentDestination == AppDestination.CLIENTS) {
                            IconButton(
                                onClick = {
                                    clientToEdit = null
                                    isClientDialogOpen = true
                                },
                                modifier = Modifier.testTag("top_bar_add_client")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = "Novo Cliente")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_bottom_nav"),
                windowInsets = WindowInsets.navigationBars
            ) {
                AppDestination.values().forEach { destination ->
                    val selected = currentDestination == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag(destination.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { destination ->
                when (destination) {
                    AppDestination.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToPos = { currentDestination = AppDestination.POS },
                            onNavigateToDailyCash = { currentDestination = AppDestination.CASH },
                            onNavigateToInventory = { currentDestination = AppDestination.INVENTORY },
                            onNavigateToOrders = { currentDestination = AppDestination.ORDERS },
                            onNavigateToClients = { currentDestination = AppDestination.CLIENTS },
                            onOpenAddProduct = {
                                productToEdit = null
                                isProductDialogOpen = true
                            },
                            onOpenAddClient = {
                                clientToEdit = null
                                isClientDialogOpen = true
                            },
                            onQuickRestock = { prod ->
                                stockAdjustProduct = prod
                                stockAdjustType = "ENTRADA"
                            }
                        )
                    }
                    AppDestination.CASH -> {
                        DailyCashScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppDestination.DASHBOARD },
                            onNavigateToPos = { currentDestination = AppDestination.POS }
                        )
                    }
                    AppDestination.POS -> {
                        PosScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppDestination.DASHBOARD }
                        )
                    }
                    AppDestination.INVENTORY -> {
                        InventoryScreen(
                            viewModel = viewModel,
                            onAddNewProduct = {
                                productToEdit = null
                                isProductDialogOpen = true
                            },
                            onEditProduct = { prod ->
                                productToEdit = prod
                                isProductDialogOpen = true
                            },
                            onAdjustStock = { prod, type ->
                                stockAdjustProduct = prod
                                stockAdjustType = type
                            }
                        )
                    }
                    AppDestination.ORDERS -> {
                        OrdersScreen(
                            viewModel = viewModel,
                            onSelectOrder = { order ->
                                viewModel.loadOrderDetails(order)
                            }
                        )
                    }
                    AppDestination.CLIENTS -> {
                        ClientsScreen(
                            viewModel = viewModel,
                            onAddNewClient = {
                                clientToEdit = null
                                isClientDialogOpen = true
                            },
                            onEditClient = { client ->
                                clientToEdit = client
                                isClientDialogOpen = true
                            },
                            onReceivePayment = { client ->
                                clientForPayment = client
                            },
                            onReturnBottles = { client ->
                                clientForBottles = client
                            }
                        )
                    }
                    AppDestination.AUDIT -> {
                        MovementsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Product Dialog (Add / Edit)
    if (isProductDialogOpen) {
        ProductDialog(
            product = productToEdit,
            onDismiss = { isProductDialogOpen = false },
            onSave = { updatedProduct ->
                isProductDialogOpen = false
                viewModel.saveProduct(updatedProduct)
            }
        )
    }

    // Stock Adjust Dialog
    stockAdjustProduct?.let { product ->
        StockAdjustDialog(
            product = product,
            initialType = stockAdjustType,
            onDismiss = { stockAdjustProduct = null },
            onConfirm = { delta, type, reason ->
                stockAdjustProduct = null
                viewModel.adjustStockQuantity(product, delta, type, reason)
            }
        )
    }

    // Client Dialog (Add / Edit)
    if (isClientDialogOpen) {
        ClientDialog(
            client = clientToEdit,
            onDismiss = { isClientDialogOpen = false },
            onSave = { updatedClient ->
                isClientDialogOpen = false
                viewModel.saveClient(updatedClient)
            }
        )
    }

    // Client Payment Dialog
    clientForPayment?.let { client ->
        ClientPaymentDialog(
            client = client,
            onDismiss = { clientForPayment = null },
            onConfirmPayment = { amount ->
                clientForPayment = null
                viewModel.payClientDebt(client.id, amount)
            }
        )
    }

    // Client Return Bottles Dialog
    clientForBottles?.let { client ->
        ClientReturnBottlesDialog(
            client = client,
            onDismiss = { clientForBottles = null },
            onConfirmReturn = { count ->
                clientForBottles = null
                viewModel.returnClientBottles(client.id, count)
            }
        )
    }

    // Order Details Dialog
    selectedDetailOrder?.let { order ->
        OrderDetailDialog(
            order = order,
            items = selectedDetailOrderItems,
            onDismiss = { viewModel.clearOrderDetails() },
            onUpdateStatus = { newStatus ->
                viewModel.updateOrderStatus(order.id, newStatus)
                viewModel.clearOrderDetails()
            },
            onMarkPaid = {
                viewModel.markOrderPaid(order.id)
                viewModel.clearOrderDetails()
            }
        )
    }

    // Order Receipt Dialog
    completedReceipt?.let { order ->
        OrderReceiptDialog(
            order = order,
            onDismiss = { viewModel.clearCompletedReceipt() }
        )
    }
}
