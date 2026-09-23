const http = require('http');
const url = require('url');

const PORT = 3000;

// Estado em memória com dados persistentes da Distribuidora de Bebidas
let state = {
    openingFloat: 200.00,
    suppliesAmount: 0.00,
    bleedingsAmount: 0.00,
    countedCash: null,
    cashStatus: "ABERTO",
    cashNotes: "",
    products: [
        { id: 1, name: "Cerveja Brahma Chopp 350ml", category: "CERVEJA", unitType: "Fardo c/ 12 latas", costPrice: 32.50, salePrice: 44.90, stockQuantity: 84, minStock: 25, isReturnable: false, barcode: "7891991000824" },
        { id: 2, name: "Cerveja Skol Pilsen 600ml", category: "CERVEJA", unitType: "Caixa c/ 24 garrafas", costPrice: 96.00, salePrice: 135.00, stockQuantity: 42, minStock: 20, isReturnable: true, barcode: "7891991001302" },
        { id: 3, name: "Cerveja Amstel Lager 350ml", category: "CERVEJA", unitType: "Fardo c/ 12 latas", costPrice: 35.00, salePrice: 48.00, stockQuantity: 65, minStock: 20, isReturnable: false, barcode: "7896045505417" },
        { id: 4, name: "Cerveja Heineken 330ml Long Neck", category: "CERVEJA", unitType: "Pack c/ 6 un", costPrice: 28.50, salePrice: 39.90, stockQuantity: 90, minStock: 30, isReturnable: false, barcode: "7896045506018" },
        { id: 5, name: "Chopp Pilsen Artesanal 50L (Barril)", category: "CHOPP", unitType: "Barril 50L", costPrice: 380.00, salePrice: 620.00, stockQuantity: 7, minStock: 5, isReturnable: true, barcode: "7898901234567" },
        { id: 6, name: "Refrigerante Coca-Cola 2L", category: "NAO_ALCOOLICO", unitType: "Fardo c/ 6 garrafas", costPrice: 48.00, salePrice: 66.00, stockQuantity: 50, minStock: 20, isReturnable: false, barcode: "7894900010015" },
        { id: 7, name: "Água Mineral Crystal sem Gás 500ml", category: "NAO_ALCOOLICO", unitType: "Fardo c/ 12 garrafas", costPrice: 14.00, salePrice: 24.00, stockQuantity: 110, minStock: 35, isReturnable: false, barcode: "7894900530018" },
        { id: 8, name: "Energético Red Bull 250ml", category: "DESTILADO_OUTROS", unitType: "Pack c/ 4 latas", costPrice: 26.00, salePrice: 38.00, stockQuantity: 40, minStock: 15, isReturnable: false, barcode: "90162602" }
    ],
    clients: [
        { id: 1, name: "Bar do Zé (Zé da Esquina)", phone: "(11) 98765-4321", address: "Av. Brasil, 450", creditBalance: 320.00, creditLimit: 2000.00, returnableBottlesPending: 18, notes: "Cliente semanal, paga toda sexta" },
        { id: 2, name: "Restaurante e Petiscaria do Silva", phone: "(11) 97654-3210", address: "Rua das Palmeiras, 112", creditBalance: 0.00, creditLimit: 3000.00, returnableBottlesPending: 6, notes: "Pagamentos em dia via PIX" },
        { id: 3, name: "Mercearia & Conveniência Central", phone: "(11) 96543-2109", address: "Rua do Comércio, 88", creditBalance: 485.50, creditLimit: 1500.00, returnableBottlesPending: 24, notes: "Tem 2 caixas de 600ml para devolver" },
        { id: 4, name: "Eventos & Festas VIP (Lúcia)", phone: "(11) 95432-1098", address: "Rua Primavera, 305", creditBalance: 0.00, creditLimit: 5000.00, returnableBottlesPending: 2, notes: "Locação de barris de chopp" }
    ],
    orders: [
        {
            id: 101,
            code: "PED-2026-001",
            clientName: "Bar do Zé (Zé da Esquina)",
            timestamp: new Date().toISOString(),
            paymentMethod: "DINHEIRO",
            paymentStatus: "PAGO",
            totalAmount: 449.00,
            totalCost: 325.00,
            items: [
                { productId: 1, productName: "Cerveja Brahma Chopp 350ml", unitType: "Fardo c/ 12 latas", quantity: 10, unitPrice: 44.90, unitCost: 32.50, subtotal: 449.00 }
            ]
        },
        {
            id: 102,
            code: "PED-2026-002",
            clientName: "Restaurante e Petiscaria do Silva",
            timestamp: new Date().toISOString(),
            paymentMethod: "PIX",
            paymentStatus: "PAGO",
            totalAmount: 620.00,
            totalCost: 380.00,
            items: [
                { productId: 5, productName: "Chopp Pilsen Artesanal 50L (Barril)", unitType: "Barril 50L", quantity: 1, unitPrice: 620.00, unitCost: 380.00, subtotal: 620.00 }
            ]
        },
        {
            id: 103,
            code: "PED-2026-003",
            clientName: "Consumidor Balcão",
            timestamp: new Date().toISOString(),
            paymentMethod: "CARTAO_CREDITO",
            paymentStatus: "PAGO",
            totalAmount: 180.00,
            totalCost: 122.00,
            items: [
                { productId: 4, productName: "Cerveja Heineken 330ml Long Neck", unitType: "Pack c/ 6 un", quantity: 3, unitPrice: 39.90, unitCost: 28.50, subtotal: 119.70 },
                { productId: 6, productName: "Refrigerante Coca-Cola 2L", unitType: "Fardo c/ 6 garrafas", quantity: 1, unitPrice: 66.00, unitCost: 48.00, subtotal: 66.00 }
            ]
        }
    ],
    movements: [
        { id: 1, productName: "Cerveja Brahma Chopp 350ml", type: "ENTRADA", quantity: 100, reason: "Carga Fábrica Ambev NF-12940", timestamp: new Date(Date.now() - 36000000).toLocaleString('pt-BR') },
        { id: 2, productName: "Chopp Pilsen Artesanal 50L", type: "ENTRADA", quantity: 10, reason: "Recebimento Cervejaria Parceira", timestamp: new Date(Date.now() - 25000000).toLocaleString('pt-BR') },
        { id: 3, productName: "Cerveja Skol Pilsen 600ml", type: "VENDA", quantity: 5, reason: "Pedido Balcão", timestamp: new Date(Date.now() - 10000000).toLocaleString('pt-BR') }
    ]
};

function formatCurrency(val) {
    return Number(val || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function calculateDailyCash() {
    let todayOrders = state.orders;
    let totalSales = 0;
    let totalCost = 0;
    let cashSales = 0;
    let pixSales = 0;
    let creditSales = 0;
    let debitSales = 0;
    let fiadoSales = 0;

    let productMap = {};

    todayOrders.forEach(order => {
        const amt = Number(order.totalAmount || 0);
        const cost = Number(order.totalCost || 0);
        totalSales += amt;
        totalCost += cost;

        switch (order.paymentMethod) {
            case "DINHEIRO": cashSales += amt; break;
            case "PIX": pixSales += amt; break;
            case "CARTAO_CREDITO": creditSales += amt; break;
            case "CARTAO_DEBITO": debitSales += amt; break;
            case "FATURADO_BOLETO": fiadoSales += amt; break;
            default: cashSales += amt; break;
        }

        (order.items || []).forEach(item => {
            if (!productMap[item.productId]) {
                productMap[item.productId] = {
                    productId: item.productId,
                    productName: item.productName,
                    unitType: item.unitType,
                    quantity: 0,
                    revenue: 0,
                    cost: item.unitCost || 0,
                    unitPrice: item.unitPrice || 0
                };
            }
            productMap[item.productId].quantity += item.quantity;
            productMap[item.productId].revenue += item.subtotal;
        });
    });

    const totalProfit = Math.max(0, totalSales - totalCost);
    const expectedCash = Math.max(0, state.openingFloat + cashSales + state.suppliesAmount - state.bleedingsAmount);

    let isCounted = state.countedCash !== null;
    let cashDifference = isCounted ? (state.countedCash - expectedCash) : 0;
    let hasCashShortage = isCounted && cashDifference < -0.05;
    let hasCashSurplus = isCounted && cashDifference > 0.05;
    let isCashBalanced = isCounted && !hasCashShortage && !hasCashSurplus;

    const soldProducts = Object.values(productMap).map(p => {
        const profit = Math.max(0, p.revenue - (p.cost * p.quantity));
        const margin = p.cost > 0 ? (((p.unitPrice - p.cost) / p.cost) * 100) : 0;
        return {
            ...p,
            profit,
            marginPercent: Math.round(margin * 10) / 10
        };
    }).sort((a, b) => b.revenue - a.revenue);

    return {
        date: new Date().toLocaleDateString('pt-BR'),
        status: state.cashStatus,
        notes: state.cashNotes,
        openingFloat: state.openingFloat,
        suppliesAmount: state.suppliesAmount,
        bleedingsAmount: state.bleedingsAmount,
        totalSales,
        totalCost,
        totalProfit,
        totalOrdersCount: todayOrders.length,
        cashSales,
        pixSales,
        creditSales,
        debitSales,
        fiadoSales,
        expectedCash,
        isCounted,
        countedCash: state.countedCash,
        cashDifference,
        hasCashShortage,
        hasCashSurplus,
        isCashBalanced,
        soldProducts
    };
}

// Layout Base idêntico ao Jetpack Compose Android (Material 3)
function renderAppLayout(title, subtitle, activeTab, content, topBarActions = '') {
    return `<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
    <meta name="theme-color" content="#FFFFFF">
    <title>${title} - DistriBebidas</title>
    <!-- Google Fonts & Material Symbols (Idênticos ao Jetpack Compose) -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --md-sys-color-primary: #D97706;
            --md-sys-color-on-primary: #FFFFFF;
            --md-sys-color-primary-container: #FEF3C7;
            --md-sys-color-on-primary-container: #92400E;
            --md-sys-color-secondary: #1E293B;
            --md-sys-color-background: #F8FAFC;
            --md-sys-color-surface: #FFFFFF;
            --md-sys-color-surface-variant: #F1F5F9;
            --md-sys-color-on-surface-variant: #64748B;
            --md-sys-color-outline: #E2E8F0;
            --md-sys-color-error: #EF4444;
            --md-sys-color-error-container: #FEE2E2;
            --md-sys-color-success: #10B981;
            --md-sys-color-success-container: #D1FAE5;
            --md-sys-color-blue: #2563EB;
        }

        * {
            box-sizing: border-box;
            -webkit-tap-highlight-color: transparent;
        }

        body {
            font-family: 'Roboto', system-ui, -apple-system, sans-serif;
            background-color: var(--md-sys-color-background);
            color: #0F172A;
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
        }

        /* Container que simula e responde como o App Android */
        .android-app-container {
            width: 100%;
            max-width: 680px; /* Adaptação perfeita para mobile e tablet */
            min-height: 100vh;
            background-color: var(--md-sys-color-background);
            display: flex;
            flex-direction: column;
            position: relative;
            box-shadow: 0 0 24px rgba(0,0,0,0.06);
        }

        /* TopAppBar estilo Jetpack Compose Material 3 */
        .m3-top-bar {
            height: 64px;
            background-color: var(--md-sys-color-surface);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 16px;
            position: sticky;
            top: 0;
            z-index: 1020;
            border-bottom: 1px solid rgba(226, 232, 240, 0.8);
        }

        .m3-top-bar-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: #0F172A;
            margin: 0;
            line-height: 1.2;
        }

        .m3-top-bar-subtitle {
            font-size: 0.75rem;
            color: var(--md-sys-color-on-surface-variant);
            margin: 0;
        }

        .m3-icon-btn {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            border: none;
            background: transparent;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #1E293B;
            transition: background-color 0.2s;
            text-decoration: none;
            cursor: pointer;
        }
        .m3-icon-btn:hover, .m3-icon-btn:active {
            background-color: rgba(0,0,0,0.05);
            color: var(--md-sys-color-primary);
        }

        /* Área de conteúdo rolável com espaçamento para a barra inferior */
        .m3-content {
            flex: 1;
            padding: 16px 16px 100px 16px;
        }

        /* Material 3 Bottom Navigation Bar (Exata do Compose) */
        .m3-bottom-nav {
            position: fixed;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 100%;
            max-width: 680px;
            height: 72px;
            background-color: var(--md-sys-color-surface);
            display: flex;
            align-items: center;
            justify-content: space-around;
            padding: 0 4px;
            z-index: 1030;
            border-top: 1px solid var(--md-sys-color-outline);
            box-shadow: 0 -2px 10px rgba(0,0,0,0.04);
        }

        .m3-nav-item {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: var(--md-sys-color-on-surface-variant);
            padding: 4px 0;
            position: relative;
            cursor: pointer;
        }

        .m3-nav-pill {
            width: 52px;
            height: 30px;
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s cubic-bezier(0.2, 0, 0, 1);
        }

        .m3-nav-item.active .m3-nav-pill {
            background-color: var(--md-sys-color-primary-container);
            color: var(--md-sys-color-on-primary-container);
        }

        .m3-nav-item.active {
            color: var(--md-sys-color-primary);
            font-weight: 700;
        }

        .m3-nav-icon {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
            font-size: 22px;
            line-height: 1;
        }

        .m3-nav-item.active .m3-nav-icon {
            font-variation-settings: 'FILL' 1, 'wght' 600, 'GRAD' 0, 'opsz' 24;
            color: var(--md-sys-color-primary);
        }

        .m3-nav-label {
            font-size: 0.70rem;
            margin-top: 3px;
            line-height: 1;
        }

        /* Material 3 Card */
        .m3-card {
            background-color: var(--md-sys-color-surface);
            border-radius: 20px;
            border: 1px solid rgba(226, 232, 240, 0.9);
            box-shadow: 0 1px 3px rgba(0,0,0,0.03);
            overflow: hidden;
            margin-bottom: 16px;
        }

        /* Hero Banner Gradient (Exato do Compose) */
        .m3-hero-card {
            border-radius: 20px;
            background: linear-gradient(135deg, #B45309 0%, #D97706 100%);
            color: #FFFFFF;
            padding: 20px;
            margin-bottom: 16px;
            box-shadow: 0 4px 14px rgba(217, 119, 6, 0.25);
        }

        .m3-hero-badge {
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(8px);
            border-radius: 12px;
            padding: 10px 14px;
            flex: 1;
            text-align: center;
        }

        /* Botões de Ações Rápidas (Exatos do Compose) */
        .m3-quick-btn {
            background-color: var(--md-sys-color-surface);
            border: 1px solid var(--md-sys-color-outline);
            border-radius: 16px;
            padding: 12px 6px;
            text-align: center;
            text-decoration: none;
            color: #0F172A;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            flex: 1;
            box-shadow: 0 1px 2px rgba(0,0,0,0.04);
            transition: all 0.2s;
        }
        .m3-quick-btn:hover, .m3-quick-btn:active {
            transform: translateY(-2px);
            background-color: var(--md-sys-color-surface-variant);
            color: var(--md-sys-color-primary);
        }

        .m3-quick-icon-wrap {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 6px;
        }

        /* Chips M3 */
        .m3-assist-chip {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 0.8rem;
            font-weight: 600;
            text-decoration: none;
            border: 1px solid transparent;
            cursor: pointer;
        }

        /* Modais estilizados como Material 3 */
        .modal-content {
            border-radius: 28px;
            border: none;
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
        }
        .modal-header {
            border-bottom: 1px solid var(--md-sys-color-surface-variant);
            padding: 20px 24px;
        }
        .modal-body {
            padding: 20px 24px;
        }
        .modal-footer {
            border-top: 1px solid var(--md-sys-color-surface-variant);
            padding: 16px 24px;
        }

        /* Botões M3 */
        .btn-m3-primary {
            background-color: var(--md-sys-color-primary);
            color: #FFFFFF;
            font-weight: 700;
            border-radius: 50rem;
            padding: 10px 22px;
            border: none;
        }
        .btn-m3-primary:hover, .btn-m3-primary:active {
            background-color: #B45309;
            color: #FFFFFF;
        }
    </style>
</head>
<body>

<div class="android-app-container">
    <!-- TopAppBar Material 3 -->
    <header class="m3-top-bar">
        <div class="d-flex align-items-center">
            <span class="material-symbols-rounded text-warning fs-2 me-2" style="font-variation-settings: 'FILL' 1;">sports_bar</span>
            <div>
                <h1 class="m3-top-bar-title">${title}</h1>
                <p class="m3-top-bar-subtitle">${subtitle}</p>
            </div>
        </div>
        <div class="d-flex align-items-center gap-1">
            ${topBarActions}
        </div>
    </header>

    <!-- Conteúdo da Tela -->
    <main class="m3-content">
        ${content}
    </main>

    <!-- Bottom Navigation Bar Material 3 (Idêntico ao Compose) -->
    <nav class="m3-bottom-nav">
        <a href="/" class="m3-nav-item ${activeTab === 'dashboard' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">dashboard</span>
            </div>
            <span class="m3-nav-label">Início</span>
        </a>
        <a href="/pdv" class="m3-nav-item ${activeTab === 'pdv' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">point_of_sale</span>
            </div>
            <span class="m3-nav-label">PDV</span>
        </a>
        <a href="/cash" class="m3-nav-item ${activeTab === 'cash' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">account_balance_wallet</span>
            </div>
            <span class="m3-nav-label">Caixa</span>
        </a>
        <a href="/estoque" class="m3-nav-item ${activeTab === 'estoque' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">inventory_2</span>
            </div>
            <span class="m3-nav-label">Estoque</span>
        </a>
        <a href="/pedidos" class="m3-nav-item ${activeTab === 'pedidos' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">receipt_long</span>
            </div>
            <span class="m3-nav-label">Pedidos</span>
        </a>
        <a href="/clientes" class="m3-nav-item ${activeTab === 'clientes' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">group</span>
            </div>
            <span class="m3-nav-label">Clientes</span>
        </a>
        <a href="/movimentacoes" class="m3-nav-item ${activeTab === 'movimentacoes' ? 'active' : ''}">
            <div class="m3-nav-pill">
                <span class="material-symbols-rounded m3-nav-icon">history</span>
            </div>
            <span class="m3-nav-label">Auditoria</span>
        </a>
    </nav>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>`;
}

// 1. Dashboard View (Idêntico ao DashboardScreen.kt)
function renderDashboard() {
    const report = calculateDailyCash();
    const totalStockUnits = state.products.reduce((acc, p) => acc + p.stockQuantity, 0);
    const totalReturnables = state.clients.reduce((acc, c) => acc + c.returnableBottlesPending, 0);
    const lowStock = state.products.filter(p => p.stockQuantity <= p.minStock);

    let cashIndicatorBackground = 'rgba(254, 243, 199, 0.7)';
    let cashIndicatorIcon = 'help';
    let cashIndicatorIconColor = '#D97706';
    let cashIndicatorTitle = 'Conferência de Caixa Pendente';
    let cashIndicatorSubtitle = 'Conte o dinheiro físico para apurar o saldo';

    if (report.hasCashShortage) {
        cashIndicatorBackground = 'rgba(254, 226, 226, 0.8)';
        cashIndicatorIcon = 'warning';
        cashIndicatorIconColor = '#EF4444';
        cashIndicatorTitle = `TEVE QUEBRA: -R$ ${formatCurrency(Math.abs(report.cashDifference))}`;
        cashIndicatorSubtitle = `Falta dinheiro na gaveta (Contado: R$ ${formatCurrency(report.countedCash)})`;
    } else if (report.isCounted && report.isCashBalanced) {
        cashIndicatorBackground = 'rgba(209, 250, 229, 0.8)';
        cashIndicatorIcon = 'check_circle';
        cashIndicatorIconColor = '#10B981';
        cashIndicatorTitle = 'SEM QUEBRA: Saldo 100% Correto';
        cashIndicatorSubtitle = 'O dinheiro físico na gaveta confere exatamente!';
    } else if (report.hasCashSurplus) {
        cashIndicatorBackground = 'rgba(219, 234, 254, 0.8)';
        cashIndicatorIcon = 'info';
        cashIndicatorIconColor = '#2563EB';
        cashIndicatorTitle = `SOBRA DE CAIXA: +R$ ${formatCurrency(report.cashDifference)}`;
        cashIndicatorSubtitle = `Há mais dinheiro que o previsto no sistema`;
    }

    const soldRows = report.soldProducts.map(p => `
        <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
            <div>
                <strong class="d-block text-dark">${p.productName}</strong>
                <small class="text-muted">${p.unitType} • Custo: R$ ${formatCurrency(p.cost)}</small>
            </div>
            <div class="text-end">
                <span class="badge bg-warning text-dark fw-bold px-2 py-1 mb-1">${p.quantity} un vendidas</span>
                <div class="fw-bold text-success small">+ R$ ${formatCurrency(p.profit)} lucro (${p.marginPercent}%)</div>
                <div class="fw-bold text-primary">R$ ${formatCurrency(p.revenue)}</div>
            </div>
        </div>
    `).join('') || '<div class="text-center py-4 text-muted small">Nenhuma venda realizada hoje ainda.</div>';

    return `
        <!-- Hero Banner Gradient (Idêntico ao Dashboard Compose) -->
        <div class="m3-hero-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <h2 class="h5 fw-bold mb-1">DistriBebidas Gestão</h2>
                    <p class="small mb-0 opacity-75">Painel Geral & Controle Operacional</p>
                </div>
                <div style="width: 48px; height: 48px; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center;">
                    <span class="material-symbols-rounded fs-2 text-white">sports_bar</span>
                </div>
            </div>

            <div class="d-flex gap-2">
                <div class="m3-hero-badge">
                    <small class="d-block text-uppercase opacity-75" style="font-size: 0.65rem;">Estoque Total</small>
                    <strong class="fs-5">${totalStockUnits} un</strong>
                </div>
                <div class="m3-hero-badge">
                    <small class="d-block text-uppercase opacity-75" style="font-size: 0.65rem;">Cascos no Mercado</small>
                    <strong class="fs-5">${totalReturnables} un</strong>
                </div>
            </div>
        </div>

        <!-- Ações Rápidas (Horizontal Quick Action Buttons) -->
        <div class="mb-3">
            <h3 class="h6 fw-bold mb-2 text-secondary">Ações Rápidas</h3>
            <div class="d-flex gap-2">
                <a href="/pdv" class="m3-quick-btn">
                    <div class="m3-quick-icon-wrap" style="background-color: #FEF3C7; color: #D97706;">
                        <span class="material-symbols-rounded">point_of_sale</span>
                    </div>
                    <small class="fw-bold" style="font-size: 0.72rem;">Nova Venda</small>
                </a>
                <a href="/cash" class="m3-quick-btn">
                    <div class="m3-quick-icon-wrap" style="background-color: #D1FAE5; color: #047857;">
                        <span class="material-symbols-rounded">account_balance_wallet</span>
                    </div>
                    <small class="fw-bold" style="font-size: 0.72rem;">Caixa do Dia</small>
                </a>
                <a href="/estoque" class="m3-quick-btn">
                    <div class="m3-quick-icon-wrap" style="background-color: #DBEAFE; color: #2563EB;">
                        <span class="material-symbols-rounded">add_box</span>
                    </div>
                    <small class="fw-bold" style="font-size: 0.72rem;">Estoque</small>
                </a>
                <a href="/clientes" class="m3-quick-btn">
                    <div class="m3-quick-icon-wrap" style="background-color: #EDE9FE; color: #7C3AED;">
                        <span class="material-symbols-rounded">person_add</span>
                    </div>
                    <small class="fw-bold" style="font-size: 0.72rem;">Clientes</small>
                </a>
            </div>
        </div>

        <!-- Indicador de Caixa do Dia (Card com Status de Quebra) -->
        <div class="m3-card p-3" style="cursor: pointer;" onclick="window.location.href='/cash'">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <div class="d-flex align-items-center">
                    <div style="width: 40px; height: 40px; border-radius: 50%; background: #FEF3C7; display: flex; align-items: center; justify-content: center; margin-right: 10px;">
                        <span class="material-symbols-rounded text-warning">price_check</span>
                    </div>
                    <div>
                        <strong class="d-block text-dark">Indicador de Caixa do Dia</strong>
                        <small class="text-muted">Data: ${report.date} • Status: ${report.status}</small>
                    </div>
                </div>
                <span class="m3-assist-chip bg-light text-dark border">
                    Conferir <span class="material-symbols-rounded fs-6">arrow_forward</span>
                </span>
            </div>

            <!-- Banner Quebra / Sem Quebra -->
            <div class="p-3 rounded-3 d-flex align-items-center justify-content-between" style="background-color: ${cashIndicatorBackground};">
                <div class="d-flex align-items-center">
                    <span class="material-symbols-rounded me-2" style="color: ${cashIndicatorIconColor}; font-size: 28px; font-variation-settings: 'FILL' 1;">${cashIndicatorIcon}</span>
                    <div>
                        <strong class="d-block" style="color: ${cashIndicatorIconColor};">${cashIndicatorTitle}</strong>
                        <small class="text-muted">${cashIndicatorSubtitle}</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Grade de Indicadores (2x2 KPIs) -->
        <div class="row g-2 mb-3">
            <div class="col-6">
                <div class="m3-card p-3 h-100">
                    <div class="d-flex align-items-center justify-content-between mb-1">
                        <small class="text-muted fw-bold" style="font-size: 0.68rem;">VENDAS HOJE</small>
                        <span class="material-symbols-rounded text-success fs-5">trending_up</span>
                    </div>
                    <h4 class="fw-bold text-success mb-0 fs-5">R$ ${formatCurrency(report.totalSales)}</h4>
                    <small class="text-muted" style="font-size: 0.7rem;">${report.totalOrdersCount} pedidos</small>
                </div>
            </div>
            <div class="col-6">
                <div class="m3-card p-3 h-100">
                    <div class="d-flex align-items-center justify-content-between mb-1">
                        <small class="text-muted fw-bold" style="font-size: 0.68rem;">LUCRO ESTIMADO</small>
                        <span class="material-symbols-rounded text-warning fs-5">payments</span>
                    </div>
                    <h4 class="fw-bold text-warning mb-0 fs-5">R$ ${formatCurrency(report.totalProfit)}</h4>
                    <small class="text-muted" style="font-size: 0.7rem;">Sobre o custo</small>
                </div>
            </div>
            <div class="col-6">
                <div class="m3-card p-3 h-100">
                    <div class="d-flex align-items-center justify-content-between mb-1">
                        <small class="text-muted fw-bold" style="font-size: 0.68rem;">SALDO GAVETA</small>
                        <span class="material-symbols-rounded text-dark fs-5">point_of_sale</span>
                    </div>
                    <h4 class="fw-bold text-dark mb-0 fs-5">R$ ${formatCurrency(report.expectedCash)}</h4>
                    <small class="text-muted" style="font-size: 0.7rem;">Teórico Dinheiro</small>
                </div>
            </div>
            <div class="col-6">
                <div class="m3-card p-3 h-100">
                    <div class="d-flex align-items-center justify-content-between mb-1">
                        <small class="text-muted fw-bold" style="font-size: 0.68rem;">CASCOS / CLIENTES</small>
                        <span class="material-symbols-rounded text-info fs-5">recycling</span>
                    </div>
                    <h4 class="fw-bold text-info mb-0 fs-5">${totalReturnables} un</h4>
                    <small class="text-muted" style="font-size: 0.7rem;">Vasilhames fora</small>
                </div>
            </div>
        </div>

        <!-- Mercadorias Vendidas no Dia (Lista idêntica ao Compose) -->
        <div class="m3-card">
            <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
                <div class="d-flex align-items-center">
                    <span class="material-symbols-rounded text-warning me-2">inventory</span>
                    <strong class="text-dark">Mercadorias Vendidas no Dia</strong>
                </div>
                <a href="/pdv" class="m3-assist-chip bg-warning text-dark border-0">
                    + Vender
                </a>
            </div>
            <div>
                ${soldRows}
            </div>
        </div>

        <!-- Alerta de Estoque Baixo -->
        ${lowStock.length > 0 ? `
            <div class="m3-card border-danger">
                <div class="p-3 border-bottom d-flex justify-content-between align-items-center bg-danger-subtle text-danger">
                    <div class="d-flex align-items-center">
                        <span class="material-symbols-rounded me-2">warning</span>
                        <strong>Estoque Baixo (${lowStock.length})</strong>
                    </div>
                    <a href="/estoque" class="btn btn-sm btn-outline-danger py-0 px-2">Ver</a>
                </div>
                <div class="p-2">
                    ${lowStock.map(p => `
                        <div class="p-2 border-bottom d-flex justify-content-between align-items-center">
                            <div>
                                <strong class="small text-dark d-block">${p.name}</strong>
                                <small class="text-muted">Mínimo: ${p.minStock} un</small>
                            </div>
                            <span class="badge bg-danger">${p.stockQuantity} un restantes</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        ` : ''}
    `;
}

// 2. Caixa do Dia View (Idêntico ao DailyCashScreen.kt)
function renderCash() {
    const report = calculateDailyCash();

    let discrepancyCard = '';
    if (report.hasCashShortage) {
        discrepancyCard = `
            <div class="m3-card border-danger p-3 mb-3" style="background-color: #FEF2F2;">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <div class="d-flex align-items-center">
                        <span class="material-symbols-rounded text-danger fs-2 me-2" style="font-variation-settings: 'FILL' 1;">error</span>
                        <div>
                            <strong class="text-danger h6 mb-0 d-block">TEVE QUEBRA DE CAIXA</strong>
                            <small class="text-muted">Falta dinheiro na gaveta</small>
                        </div>
                    </div>
                    <button class="btn btn-sm btn-danger rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#modalCount">
                        Recontar
                    </button>
                </div>
                <div class="p-2 rounded bg-white border border-danger-subtle d-flex justify-content-between align-items-center">
                    <span class="text-muted small">Valor da Quebra:</span>
                    <strong class="text-danger fs-5">- R$ ${formatCurrency(Math.abs(report.cashDifference))}</strong>
                </div>
            </div>
        `;
    } else if (report.isCounted && report.isCashBalanced) {
        discrepancyCard = `
            <div class="m3-card border-success p-3 mb-3" style="background-color: #F0FDF4;">
                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center">
                        <span class="material-symbols-rounded text-success fs-2 me-2" style="font-variation-settings: 'FILL' 1;">check_circle</span>
                        <div>
                            <strong class="text-success h6 mb-0 d-block">SEM QUEBRA DE CAIXA</strong>
                            <small class="text-muted">Saldo 100% Correto (Diferença R$ 0,00)</small>
                        </div>
                    </div>
                    <button class="btn btn-sm btn-outline-success rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#modalCount">
                        Ajustar
                    </button>
                </div>
            </div>
        `;
    } else {
        discrepancyCard = `
            <div class="m3-card border-warning p-3 mb-3" style="background-color: #FFFBEB;">
                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center">
                        <span class="material-symbols-rounded text-warning fs-2 me-2">help</span>
                        <div>
                            <strong class="text-dark h6 mb-0 d-block">Conferência Pendente</strong>
                            <small class="text-muted">Conte o dinheiro físico para apurar</small>
                        </div>
                    </div>
                    <button class="btn btn-sm btn-warning rounded-pill px-3 fw-bold" data-bs-toggle="modal" data-bs-target="#modalCount">
                        Contar Gaveta
                    </button>
                </div>
            </div>
        `;
    }

    return `
        ${discrepancyCard}

        <!-- Botões de Ação do Caixa (Sangria, Suprimento, Fundo) -->
        <div class="d-flex gap-2 mb-3">
            <button class="m3-quick-btn py-2" data-bs-toggle="modal" data-bs-target="#modalCount">
                <span class="material-symbols-rounded text-primary fs-4">check2_square</span>
                <small class="fw-bold" style="font-size: 0.72rem;">Contar Físico</small>
            </button>
            <button class="m3-quick-btn py-2" data-bs-toggle="modal" data-bs-target="#modalBleeding">
                <span class="material-symbols-rounded text-danger fs-4">remove_circle</span>
                <small class="fw-bold" style="font-size: 0.72rem;">Sangria (-)</small>
            </button>
            <button class="m3-quick-btn py-2" data-bs-toggle="modal" data-bs-target="#modalSupply">
                <span class="material-symbols-rounded text-success fs-4">add_circle</span>
                <small class="fw-bold" style="font-size: 0.72rem;">Suprimento (+)</small>
            </button>
            <button class="m3-quick-btn py-2" data-bs-toggle="modal" data-bs-target="#modalFloat">
                <span class="material-symbols-rounded text-secondary fs-4">tune</span>
                <small class="fw-bold" style="font-size: 0.72rem;">Fundo Troco</small>
            </button>
        </div>

        <!-- Tabela Demonstrativa de Fechamento -->
        <div class="m3-card p-3 mb-3">
            <h6 class="fw-bold mb-3 text-secondary d-flex align-items-center">
                <span class="material-symbols-rounded me-2 text-warning">calculate</span>
                Conferência e Movimentações da Gaveta
            </h6>

            <div class="d-flex justify-content-between py-2 border-bottom">
                <span class="text-muted">Fundo de Troco Inicial</span>
                <strong class="text-dark">R$ ${formatCurrency(report.openingFloat)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span class="text-muted">(+) Vendas em Dinheiro Hoje</span>
                <strong class="text-success">+ R$ ${formatCurrency(report.cashSales)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span class="text-muted">(+) Suprimentos de Caixa</span>
                <strong class="text-success">+ R$ ${formatCurrency(report.suppliesAmount)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span class="text-muted">(-) Sangrias (Retiradas)</span>
                <strong class="text-danger">- R$ ${formatCurrency(report.bleedingsAmount)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom bg-light px-2 rounded">
                <strong>(=) Saldo Esperado na Gaveta</strong>
                <strong class="fs-6 text-dark">R$ ${formatCurrency(report.expectedCash)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span>Dinheiro Físico Contado</span>
                <strong>${report.isCounted ? 'R$ ' + formatCurrency(report.countedCash) : '<span class="text-muted">Não conferido</span>'}</strong>
            </div>
            <div class="d-flex justify-content-between py-2">
                <strong class="${report.hasCashShortage ? 'text-danger' : 'text-success'}">Diferença (Quebra/Sobra)</strong>
                <strong class="${report.hasCashShortage ? 'text-danger' : 'text-success'}">
                    ${report.isCounted ? (report.cashDifference === 0 ? 'R$ 0,00 (Exato)' : (report.cashDifference > 0 ? '+ R$ ' + formatCurrency(report.cashDifference) : '- R$ ' + formatCurrency(Math.abs(report.cashDifference)))) : '---'}
                </strong>
            </div>
        </div>

        <!-- Formas de Pagamento -->
        <div class="m3-card p-3 mb-3">
            <h6 class="fw-bold mb-3 text-secondary d-flex align-items-center">
                <span class="material-symbols-rounded me-2 text-primary">pie_chart</span>
                Total por Forma de Pagamento
            </h6>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span><span class="material-symbols-rounded fs-6 align-middle text-success me-1">payments</span> Dinheiro Físico</span>
                <strong class="text-success">R$ ${formatCurrency(report.cashSales)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span><span class="material-symbols-rounded fs-6 align-middle text-primary me-1">qr_code</span> PIX Instantâneo</span>
                <strong class="text-primary">R$ ${formatCurrency(report.pixSales)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span><span class="material-symbols-rounded fs-6 align-middle text-info me-1">credit_card</span> Cartão de Crédito</span>
                <strong>R$ ${formatCurrency(report.creditSales)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2 border-bottom">
                <span><span class="material-symbols-rounded fs-6 align-middle text-secondary me-1">credit_card</span> Cartão de Débito</span>
                <strong>R$ ${formatCurrency(report.debitSales)}</strong>
            </div>
            <div class="d-flex justify-content-between py-2">
                <span><span class="material-symbols-rounded fs-6 align-middle text-warning me-1">receipt</span> Fiado / A Prazo</span>
                <strong class="text-warning">R$ ${formatCurrency(report.fiadoSales)}</strong>
            </div>
        </div>

        <!-- Modais de Contagem, Sangria, Suprimento e Fundo -->
        <div class="modal fade" id="modalCount" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form action="/cash/count" method="POST">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold">Conferência Física da Gaveta</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-secondary py-2 small mb-3">
                                Saldo Teórico Esperado: <strong>R$ ${formatCurrency(report.expectedCash)}</strong>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Valor em Dinheiro Contado (R$)</label>
                                <input type="number" step="0.01" class="form-control form-control-lg rounded-pill" name="countedCash" value="${report.countedCash || report.expectedCash}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small text-muted">Observação (Opcional)</label>
                                <input type="text" class="form-control rounded-pill" name="notes" placeholder="Ex: Fechamento turno">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-m3-primary">Salvar Conferência</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="modalBleeding" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form action="/cash/bleeding" method="POST">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold text-danger">Registrar Sangria (Retirada)</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Valor da Retirada (R$)</label>
                                <input type="number" step="0.01" class="form-control rounded-pill" name="amount" placeholder="0,00" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small text-muted">Motivo</label>
                                <input type="text" class="form-control rounded-pill" name="reason" placeholder="Ex: Pagamento fornecedor">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-danger rounded-pill px-4">Confirmar Sangria</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="modalSupply" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form action="/cash/supply" method="POST">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold text-success">Registrar Suprimento (Entrada)</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Valor do Reforço (R$)</label>
                                <input type="number" step="0.01" class="form-control rounded-pill" name="amount" placeholder="0,00" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-success rounded-pill px-4">Confirmar Suprimento</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="modalFloat" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form action="/cash/opening-float" method="POST">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold">Fundo de Troco Inicial</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Valor Inicial de Fundo (R$)</label>
                                <input type="number" step="0.01" class="form-control rounded-pill" name="openingFloat" value="${report.openingFloat}" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-m3-primary">Salvar Fundo</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
}

// 3. PDV View (Idêntico ao PosScreen.kt do Compose com Bottom Cart Sheet)
function renderPos() {
    const productsHtml = state.products.map(p => `
        <div class="col-6 mb-2">
            <div class="m3-card p-2 h-100 d-flex flex-direction-column justify-content-between" style="border-radius: 16px;">
                <div>
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="badge ${p.isReturnable ? 'bg-warning text-dark' : 'bg-light text-dark border'}" style="font-size: 0.6rem;">${p.isReturnable ? 'Retornável' : 'Descartável'}</span>
                        <span class="badge ${p.stockQuantity <= p.minStock ? 'bg-danger' : 'bg-secondary'}" style="font-size: 0.6rem;">${p.stockQuantity} un</span>
                    </div>
                    <strong class="d-block small text-dark line-clamp-2" style="font-size: 0.82rem; line-height: 1.2;">${p.name}</strong>
                    <small class="text-muted" style="font-size: 0.7rem;">${p.unitType}</small>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                    <strong class="text-primary" style="font-size: 0.95rem;">R$ ${formatCurrency(p.salePrice)}</strong>
                    <button class="btn btn-sm btn-warning rounded-circle p-1 d-flex align-items-center justify-content-center" style="width: 32px; height: 32px;" onclick="addToCart(${p.id}, '${p.name.replace(/'/g, "\\'")}', ${p.salePrice}, ${p.costPrice}, '${p.unitType}', ${p.stockQuantity})">
                        <span class="material-symbols-rounded fs-5 text-dark">add</span>
                    </button>
                </div>
            </div>
        </div>
    `).join('');

    const clientOptions = state.clients.map(c => `
        <option value="${c.id}">${c.name} (Fiado: R$ ${formatCurrency(c.creditBalance)})</option>
    `).join('');

    return `
        <!-- Seletor Rápido de Cliente e Busca -->
        <div class="m3-card p-3 mb-3">
            <div class="mb-2">
                <input type="text" id="searchBox" class="form-control rounded-pill px-3" placeholder="🔍 Buscar bebida ou código de barras..." onkeyup="filterProducts()">
            </div>
        </div>

        <!-- Grade de Produtos -->
        <div class="row g-2 mb-5" id="productGrid">
            ${productsHtml}
        </div>

        <!-- Barra Flutuante de Carrinho Estilo Jetpack Compose PosScreen -->
        <div id="floatingCartBar" class="fixed-bottom p-3" style="left: 50%; transform: translateX(-50%); width: 100%; max-width: 680px; bottom: 72px; z-index: 1025; display: none;">
            <div class="p-3 rounded-4 shadow-lg d-flex justify-content-between align-items-center" style="background-color: #0F172A; color: white;">
                <div>
                    <small class="text-muted d-block" id="cartItemCountLabel">0 itens no carrinho</small>
                    <strong class="fs-5 text-warning" id="cartTotalLabel">R$ 0,00</strong>
                </div>
                <button class="btn btn-warning rounded-pill px-4 fw-bold d-flex align-items-center gap-1" data-bs-toggle="modal" data-bs-target="#modalCart">
                    <span class="material-symbols-rounded fs-5">shopping_cart</span>
                    Ver Carrinho
                </button>
            </div>
        </div>

        <!-- Modal / Bottom Sheet do Carrinho e Finalização -->
        <div class="modal fade" id="modalCart" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form action="/pdv/checkout" method="POST" id="checkoutForm">
                        <input type="hidden" name="cartData" id="cartDataInput">

                        <div class="modal-header">
                            <h5 class="modal-title fw-bold">Fechar Pedido de Venda</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Cliente</label>
                                <select class="form-select rounded-pill" name="clientId">
                                    <option value="0">Consumidor Balcão (Avulso)</option>
                                    ${clientOptions}
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label small fw-bold">Forma de Pagamento</label>
                                <select class="form-select rounded-pill" name="paymentMethod" required>
                                    <option value="DINHEIRO">Dinheiro em Espécie (Gaveta)</option>
                                    <option value="PIX">PIX Instantâneo</option>
                                    <option value="CARTAO_CREDITO">Cartão de Crédito</option>
                                    <option value="CARTAO_DEBITO">Cartão de Débito</option>
                                    <option value="FATURADO_BOLETO">Fiado / A Prazo</option>
                                </select>
                            </div>

                            <hr>
                            <h6 class="fw-bold small text-muted mb-2">Itens Selecionados:</h6>
                            <div id="cartModalItems" class="mb-3" style="max-height: 200px; overflow-y: auto;"></div>

                            <div class="border-top pt-2">
                                <div class="d-flex justify-content-between align-items-center fs-5 fw-bold">
                                    <span>TOTAL:</span>
                                    <span class="text-primary" id="modalCartTotal">R$ 0,00</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center small text-success">
                                    <span>Lucro Estimado:</span>
                                    <span id="modalCartProfit">+ R$ 0,00</span>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Continuar Comprando</button>
                            <button type="submit" class="btn btn-m3-primary" id="btnSubmitOrder">Finalizar Venda</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script>
            let cart = [];

            function addToCart(id, name, price, cost, unit, stock) {
                const item = cart.find(i => i.id === id);
                if (item) {
                    if (item.quantity >= stock) {
                        alert('Estoque máximo disponível: ' + stock + ' un');
                        return;
                    }
                    item.quantity++;
                } else {
                    cart.push({ id, name, price, cost, unit, quantity: 1, stock });
                }
                updateCartUI();
            }

            function changeQty(id, delta) {
                const item = cart.find(i => i.id === id);
                if (!item) return;
                item.quantity += delta;
                if (item.quantity <= 0) {
                    cart = cart.filter(i => i.id !== id);
                } else if (item.quantity > item.stock) {
                    item.quantity = item.stock;
                    alert('Limite do estoque atingido.');
                }
                updateCartUI();
            }

            function updateCartUI() {
                const floatingBar = document.getElementById('floatingCartBar');
                const countLabel = document.getElementById('cartItemCountLabel');
                const totalLabel = document.getElementById('cartTotalLabel');
                const modalList = document.getElementById('cartModalItems');
                const modalTotal = document.getElementById('modalCartTotal');
                const modalProfit = document.getElementById('modalCartProfit');
                const input = document.getElementById('cartDataInput');

                if (cart.length === 0) {
                    floatingBar.style.display = 'none';
                    input.value = '';
                    return;
                }

                floatingBar.style.display = 'block';

                let total = 0;
                let totalCost = 0;
                let totalItems = 0;

                modalList.innerHTML = cart.map(item => {
                    const sub = item.price * item.quantity;
                    total += sub;
                    totalCost += (item.cost * item.quantity);
                    totalItems += item.quantity;

                    return \`
                        <div class="d-flex justify-content-between align-items-center mb-2 pb-1 border-bottom">
                            <div>
                                <strong class="small d-block">\${item.name}</strong>
                                <small class="text-muted">R$ \${item.price.toFixed(2)} un</small>
                            </div>
                            <div class="d-flex align-items-center gap-1">
                                <button type="button" class="btn btn-sm btn-outline-secondary px-2 py-0" onclick="changeQty(\${item.id}, -1)">-</button>
                                <span class="fw-bold px-1">\${item.quantity}</span>
                                <button type="button" class="btn btn-sm btn-outline-secondary px-2 py-0" onclick="changeQty(\${item.id}, 1)">+</button>
                                <strong class="ms-2 small">R$ \${sub.toFixed(2)}</strong>
                            </div>
                        </div>
                    \`;
                }).join('');

                const profit = Math.max(0, total - totalCost);
                countLabel.textContent = totalItems + (totalItems === 1 ? ' item selecionado' : ' itens selecionados');
                totalLabel.textContent = 'R$ ' + total.toLocaleString('pt-BR', { minimumFractionDigits: 2 });
                modalTotal.textContent = 'R$ ' + total.toLocaleString('pt-BR', { minimumFractionDigits: 2 });
                modalProfit.textContent = '+ R$ ' + profit.toLocaleString('pt-BR', { minimumFractionDigits: 2 });
                input.value = JSON.stringify(cart);
            }

            function filterProducts() {
                const query = document.getElementById('searchBox').value.toLowerCase();
                const items = document.querySelectorAll('#productGrid > div');
                items.forEach(el => {
                    const text = el.textContent.toLowerCase();
                    el.style.display = text.includes(query) ? '' : 'none';
                });
            }
        </script>
    `;
}

// 4. Estoque View (Idêntico ao InventoryScreen.kt)
function renderInventory() {
    const listHtml = state.products.map(p => {
        const isLow = p.stockQuantity <= p.minStock;
        return `
            <div class="m3-card p-3 mb-2">
                <div class="d-flex justify-content-between align-items-start mb-1">
                    <div>
                        <strong class="d-block text-dark">${p.name}</strong>
                        <small class="text-muted">${p.unitType} • Cód: ${p.barcode}</small>
                    </div>
                    <span class="badge ${p.isReturnable ? 'bg-warning text-dark' : 'bg-light text-dark border'}">${p.isReturnable ? 'Vasilhame' : 'Descartável'}</span>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                    <div>
                        <small class="text-muted d-block">Preço de Venda</small>
                        <strong class="text-primary">R$ ${formatCurrency(p.salePrice)}</strong>
                        <small class="text-muted ms-2">(Custo R$ ${formatCurrency(p.costPrice)})</small>
                    </div>
                    <div class="text-end">
                        <span class="badge ${isLow ? 'bg-danger' : 'bg-success'} fs-6">${p.stockQuantity} un</span>
                        <button class="btn btn-sm btn-outline-secondary rounded-pill py-0 px-2 d-block mt-1" data-bs-toggle="modal" data-bs-target="#modalAjuste${p.id}">
                            Ajustar
                        </button>
                    </div>
                </div>
            </div>

            <!-- Modal Ajuste Estoque -->
            <div class="modal fade" id="modalAjuste${p.id}" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form action="/estoque/ajuste" method="POST">
                            <input type="hidden" name="productId" value="${p.id}">
                            <div class="modal-header">
                                <h5 class="modal-title fw-bold">Ajustar: ${p.name}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <p class="small text-muted mb-3">Estoque atual: <strong>${p.stockQuantity} un</strong></p>
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Operação</label>
                                    <select class="form-select rounded-pill" name="type">
                                        <option value="ENTRADA">Entrada / Reposição de Carga (+)</option>
                                        <option value="SAIDA">Baixa / Perda / Avaria (-)</option>
                                        <option value="BALANCO">Inventário Manual (=)</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Quantidade</label>
                                    <input type="number" class="form-control rounded-pill" name="quantity" placeholder="0" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label small text-muted">Motivo</label>
                                    <input type="text" class="form-control rounded-pill" name="reason" placeholder="Ex: Carga Ambev NF-1294">
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Cancelar</button>
                                <button type="submit" class="btn btn-m3-primary">Salvar Ajuste</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    return `
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0 text-secondary">Catálogo Geral de Mercadorias</h6>
            <span class="badge bg-secondary">${state.products.length} itens</span>
        </div>
        ${listHtml}
    `;
}

// 5. Pedidos View (Idêntico ao OrdersScreen.kt)
function renderOrders() {
    const ordersHtml = state.orders.map(o => `
        <div class="m3-card p-3 mb-2">
            <div class="d-flex justify-content-between align-items-start mb-1">
                <div>
                    <strong class="d-block text-dark">${o.code}</strong>
                    <small class="text-muted">${o.clientName} • ${new Date(o.timestamp).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}</small>
                </div>
                <span class="badge bg-success">${o.paymentStatus}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                <span class="badge bg-light text-dark border">${o.paymentMethod}</span>
                <div class="text-end">
                    <strong class="text-primary fs-6">R$ ${formatCurrency(o.totalAmount)}</strong>
                    <div class="text-success small fw-bold">+ R$ ${formatCurrency(o.totalAmount - o.totalCost)} lucro</div>
                </div>
            </div>
        </div>
    `).join('') || '<div class="text-center py-5 text-muted">Nenhum pedido realizado.</div>';

    return `
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0 text-secondary">Histórico de Pedidos Fechados</h6>
            <a href="/pdv" class="btn btn-sm btn-m3-primary py-1 px-3">+ Venda</a>
        </div>
        ${ordersHtml}
    `;
}

// 6. Clientes View (Idêntico ao ClientsScreen.kt)
function renderClients() {
    const clientsHtml = state.clients.map(c => `
        <div class="m3-card p-3 mb-2">
            <div class="d-flex justify-content-between align-items-start mb-1">
                <div>
                    <strong class="d-block text-dark">${c.name}</strong>
                    <small class="text-muted">${c.phone} • ${c.address}</small>
                </div>
                <span class="badge ${c.returnableBottlesPending > 0 ? 'bg-warning text-dark' : 'bg-light text-muted border'}">
                    ${c.returnableBottlesPending} cascos
                </span>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                <div>
                    <small class="text-muted d-block">Saldo Devedor (Fiado)</small>
                    <strong class="${c.creditBalance > 0 ? 'text-danger' : 'text-success'}">
                        R$ ${formatCurrency(c.creditBalance)}
                    </strong>
                    <small class="text-muted ms-1">(Limite: R$ ${formatCurrency(c.creditLimit)})</small>
                </div>
                <button class="btn btn-sm btn-outline-primary rounded-pill py-1 px-3" data-bs-toggle="modal" data-bs-target="#modalPagto${c.id}">
                    Receber
                </button>
            </div>
        </div>

        <!-- Modal Pagamento Cliente -->
        <div class="modal fade" id="modalPagto${c.id}" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form action="/clientes/pagamento" method="POST">
                        <input type="hidden" name="clientId" value="${c.id}">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold">Receber de: ${c.name}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <p class="small text-muted mb-2">Saldo devedor atual: <strong class="text-danger">R$ ${formatCurrency(c.creditBalance)}</strong></p>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Valor Pago (R$)</label>
                                <input type="number" step="0.01" class="form-control rounded-pill" name="amount" value="${c.creditBalance}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Forma de Recebimento</label>
                                <select class="form-select rounded-pill" name="paymentMethod">
                                    <option value="DINHEIRO">Dinheiro (Entra na Gaveta)</option>
                                    <option value="PIX">PIX</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light rounded-pill px-3" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-success rounded-pill px-4">Baixar Débito</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `).join('');

    return `
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0 text-secondary">Clientes, Fiado & Devolução de Cascos</h6>
            <span class="badge bg-secondary">${state.clients.length} cadastrados</span>
        </div>
        ${clientsHtml}
    `;
}

// 7. Auditoria View (Idêntico ao MovementsScreen.kt)
function renderMovements() {
    const listHtml = state.movements.map(m => `
        <div class="m3-card p-3 mb-2">
            <div class="d-flex justify-content-between align-items-start mb-1">
                <div>
                    <strong class="d-block text-dark">${m.productName}</strong>
                    <small class="text-muted">${m.timestamp}</small>
                </div>
                <span class="badge ${m.type === 'ENTRADA' ? 'bg-success' : (m.type === 'VENDA' ? 'bg-primary' : 'bg-danger')}">${m.type}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                <small class="text-muted">${m.reason}</small>
                <strong class="text-dark fs-6">${m.quantity} un</strong>
            </div>
        </div>
    `).join('');

    return `
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0 text-secondary">Histórico de Auditoria & Movimentações</h6>
        </div>
        ${listHtml}
    `;
}

// Helper para parse de formulário POST
function parseFormData(req) {
    return new Promise((resolve) => {
        let body = '';
        req.on('data', chunk => { body += chunk.toString(); });
        req.on('end', () => {
            const params = new URLSearchParams(body);
            const data = {};
            for (const [key, value] of params.entries()) {
                data[key] = value;
            }
            resolve(data);
        });
    });
}

// Router Principal
const server = http.createServer(async (req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;
    const method = req.method;

    // API JSON
    if (pathname.startsWith('/api/')) {
        res.setHeader('Content-Type', 'application/json; charset=utf-8');
        res.setHeader('Access-Control-Allow-Origin', '*');
        if (pathname === '/api/cash/today') return res.end(JSON.stringify(calculateDailyCash()));
        if (pathname === '/api/products') return res.end(JSON.stringify(state.products));
        if (pathname === '/api/clients') return res.end(JSON.stringify(state.clients));
        if (pathname === '/api/orders') return res.end(JSON.stringify(state.orders));
    }

    // Ações POST
    if (method === 'POST') {
        const formData = await parseFormData(req);

        if (pathname === '/cash/count') {
            state.countedCash = parseFloat(formData.countedCash || 0);
            state.cashNotes = formData.notes || '';
            res.writeHead(302, { Location: '/cash' });
            return res.end();
        }
        if (pathname === '/cash/supply') {
            const amt = parseFloat(formData.amount || 0);
            if (amt > 0) state.suppliesAmount += amt;
            res.writeHead(302, { Location: '/cash' });
            return res.end();
        }
        if (pathname === '/cash/bleeding') {
            const amt = parseFloat(formData.amount || 0);
            if (amt > 0) state.bleedingsAmount += amt;
            res.writeHead(302, { Location: '/cash' });
            return res.end();
        }
        if (pathname === '/cash/opening-float') {
            const amt = parseFloat(formData.openingFloat || 0);
            if (amt >= 0) state.openingFloat = amt;
            res.writeHead(302, { Location: '/cash' });
            return res.end();
        }
        if (pathname === '/pdv/checkout') {
            try {
                const cart = JSON.parse(formData.cartData || '[]');
                const clientId = parseInt(formData.clientId || '0', 10);
                const paymentMethod = formData.paymentMethod || 'DINHEIRO';

                let clientName = 'Consumidor Balcão';
                let client = state.clients.find(c => c.id === clientId);
                if (client) clientName = client.name;

                let totalAmount = 0;
                let totalCost = 0;
                let orderItems = [];

                cart.forEach(item => {
                    const subtotal = item.price * item.quantity;
                    totalAmount += subtotal;
                    totalCost += (item.cost * item.quantity);

                    orderItems.push({
                        productId: item.id,
                        productName: item.name,
                        unitType: item.unit,
                        quantity: item.quantity,
                        unitPrice: item.price,
                        unitCost: item.cost,
                        subtotal
                    });

                    const prod = state.products.find(p => p.id === item.id);
                    if (prod) {
                        prod.stockQuantity = Math.max(0, prod.stockQuantity - item.quantity);
                        state.movements.unshift({
                            id: state.movements.length + 1,
                            productName: prod.name,
                            type: 'VENDA',
                            quantity: item.quantity,
                            reason: `Venda para ${clientName}`,
                            timestamp: new Date().toLocaleString('pt-BR')
                        });
                        if (prod.isReturnable && client) {
                            client.returnableBottlesPending += item.quantity;
                        }
                    }
                });

                if (paymentMethod === 'FATURADO_BOLETO' && client) {
                    client.creditBalance += totalAmount;
                }

                const newOrder = {
                    id: state.orders.length + 101,
                    code: `PED-${new Date().getFullYear()}-${String(state.orders.length + 1).padStart(3, '0')}`,
                    clientName,
                    timestamp: new Date().toISOString(),
                    paymentMethod,
                    paymentStatus: 'PAGO',
                    totalAmount,
                    totalCost,
                    items: orderItems
                };
                state.orders.unshift(newOrder);

            } catch (err) {
                console.error("Erro checkout:", err);
            }
            res.writeHead(302, { Location: '/pedidos' });
            return res.end();
        }
        if (pathname === '/estoque/ajuste') {
            const prodId = parseInt(formData.productId, 10);
            const qty = parseInt(formData.quantity, 10) || 0;
            const type = formData.type;
            const reason = formData.reason || 'Ajuste manual';
            const prod = state.products.find(p => p.id === prodId);
            if (prod) {
                if (type === 'ENTRADA') prod.stockQuantity += qty;
                else if (type === 'SAIDA') prod.stockQuantity = Math.max(0, prod.stockQuantity - qty);
                else if (type === 'BALANCO') prod.stockQuantity = qty;

                state.movements.unshift({
                    id: state.movements.length + 1,
                    productName: prod.name,
                    type: type,
                    quantity: qty,
                    reason: reason,
                    timestamp: new Date().toLocaleString('pt-BR')
                });
            }
            res.writeHead(302, { Location: '/estoque' });
            return res.end();
        }
        if (pathname === '/clientes/pagamento') {
            const clientId = parseInt(formData.clientId, 10);
            const amount = parseFloat(formData.amount || 0);
            const client = state.clients.find(c => c.id === clientId);
            if (client && amount > 0) {
                client.creditBalance = Math.max(0, client.creditBalance - amount);
                if (formData.paymentMethod === 'DINHEIRO') {
                    state.suppliesAmount += amount;
                }
            }
            res.writeHead(302, { Location: '/clientes' });
            return res.end();
        }
    }

    // Rotas GET HTML
    res.setHeader('Content-Type', 'text/html; charset=utf-8');

    if (pathname === '/' || pathname === '/dashboard') {
        return res.end(renderAppLayout('DistriBebidas', 'Painel Geral & Controle', 'dashboard', renderDashboard()));
    }
    if (pathname === '/pdv') {
        return res.end(renderAppLayout('PDV - Nova Venda', 'Frente de Caixa & Pedidos', 'pdv', renderPos()));
    }
    if (pathname === '/cash') {
        return res.end(renderAppLayout('Caixa do Dia', 'Conferência & Movimentações', 'cash', renderCash()));
    }
    if (pathname === '/estoque') {
        return res.end(renderAppLayout('Controle de Estoque', 'Mercadorias & Vasilhames', 'estoque', renderInventory()));
    }
    if (pathname === '/pedidos') {
        return res.end(renderAppLayout('Vendas & Pedidos', 'Faturamento & Comprovantes', 'pedidos', renderOrders()));
    }
    if (pathname === '/clientes') {
        return res.end(renderAppLayout('Gestão de Clientes', 'Fiado & Cascos Retornáveis', 'clientes', renderClients()));
    }
    if (pathname === '/movimentacoes') {
        return res.end(renderAppLayout('Auditoria de Estoque', 'Histórico de Entradas e Saídas', 'movimentacoes', renderMovements()));
    }

    // 404
    res.statusCode = 404;
    res.end(renderAppLayout('404', 'Página não encontrada', '', `
        <div class="text-center py-5">
            <h2 class="fw-bold text-muted">404</h2>
            <p>Página não encontrada.</p>
            <a href="/" class="btn btn-m3-primary">Voltar ao Início</a>
        </div>
    `));
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`DistriBebidas Material 3 Web Server running on port ${PORT}`);
});
