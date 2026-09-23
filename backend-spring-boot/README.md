# DistriBebidas Web - Spring Boot & PostgreSQL (Arquitetura MVC)

Sistema Web Completo para Gestão de Distribuidora de Bebidas, estruturado rigorosamente no padrão de arquitetura **MVC (Model-View-Controller)** com **Spring Boot 3**, **Spring Data JPA**, **PostgreSQL**, **Thymeleaf** e **Bootstrap 5**.

---

## 🏛️ Arquitetura MVC do Backend

A estrutura foi organizada seguindo as melhores práticas de desenvolvimento corporativo Java/Spring, com responsabilidade única e desacoplamento entre dados, regras de negócio e apresentação:

```
backend-spring-boot/src/main/java/com/distribebidas
│
├── config/                          # Configurações do Spring e Carga Inicial
│   └── DataInitializer.java         # Seed inicial de dados para demonstração e testes
│
├── model/                           # [M - MODEL] Representação do Modelo de Dados
│   ├── entity/                      # Entidades persistidas no banco relacional (JPA/Hibernate)
│   │   ├── Product.java             # Mercadoria com Custo, Venda, Margem e Vasilhame
│   │   ├── Client.java              # Clientes, Fiado (Saldo Devedor) e Cascos Pendentes
│   │   ├── SaleOrder.java           # Pedidos de venda com cálculo de faturamento e lucro bruto
│   │   ├── SaleOrderItem.java       # Itens do pedido com snapshot de custo e preço
│   │   └── StockMovement.java       # Histórico de auditoria de movimentações de estoque
│   │
│   ├── dto/                         # Data Transfer Objects / ViewModels / Formulários
│   │   ├── CreateOrderRequestDto.java # Payload de criação de pedidos no PDV
│   │   ├── OrderItemRequestDto.java   # Itens do carrinho de vendas
│   │   ├── StockAdjustmentDto.java    # Dados de ajuste de entrada/baixa de estoque
│   │   ├── ClientPaymentDto.java      # Pagamento de dívida de cliente
│   │   ├── BottleReturnDto.java       # Devolução de vasilhames/garrafas retornáveis
│   │   └── DashboardStatsDto.java     # Consolidação de métricas e KPIs financeiros
│   │
│   └── enums/                       # Tipos Enumerados do Domínio
│       ├── PaymentMethod.java       # PIX, Dinheiro, Cartões, Faturado/Boleto
│       ├── PaymentStatus.java       # PAGO, PENDENTE, CANCELADO
│       ├── OrderStatus.java         # EM_SEPARACAO, EM_ROTA, CONCLUIDO, CANCELADO
│       └── MovementType.java        # ENTRADA, SAIDA, VENDA, AJUSTE
│
├── repository/                      # [M - MODEL / Data Access] Camada de Persistência (JPA)
│   ├── ProductRepository.java       # Consultas de produtos, estoque mínimo e filtros
│   ├── ClientRepository.java        # Consultas de clientes com dívidas e cascos
│   ├── SaleOrderRepository.java     # Consultas e consolidação financeira de vendas
│   └── StockMovementRepository.java # Histórico e auditoria de movimentações
│
├── service/                         # [M - MODEL / Business Logic] Camada de Regras de Negócio
│   ├── ProductService.java          # Gestão de produtos e ajustes manuais de estoque
│   ├── ClientService.java           # Gestão de clientes, quitação de débitos e cascos
│   ├── OrderService.java            # Fechamento de vendas, baixa atômica e auditoria
│   ├── StockMovementService.java    # Histórico de movimentações de estoque
│   └── DashboardService.java        # Consolidação de KPIs, margens e lucratividade
│
└── controller/                      # [C - CONTROLLER] Recepção de Requisições e Roteamento
    │
    ├── web/                         # Controllers Web MVC (renderizam Views Thymeleaf)
    │   ├── DashboardWebController.java # Rota "/" -> dashboard.html
    │   ├── InventoryWebController.java # Rotas "/estoque/**" -> inventory.html
    │   ├── PosWebController.java       # Rotas "/pdv/**" -> pos.html
    │   ├── OrderWebController.java     # Rotas "/pedidos/**" -> orders.html
    │   ├── ClientWebController.java    # Rotas "/clientes/**" -> clients.html
    │   └── MovementWebController.java  # Rotas "/movimentacoes" -> movements.html
    │
    └── api/                         # Controllers REST API (retornam JSON para Mobile/Android)
        ├── DashboardApiController.java # GET /api/dashboard/stats
        ├── ProductApiController.java   # CRUD + Ajustes /api/products
        ├── OrderApiController.java     # Pedidos e Pagamentos /api/orders
        ├── ClientApiController.java    # Clientes, Fiado e Cascos /api/clients
        └── MovementApiController.java  # Auditoria /api/movements

backend-spring-boot/src/main/resources
│
└── templates/                       # [V - VIEW] Templates HTML do Thymeleaf
    ├── layout.html                  # Layout mestre com Navbar, Bootstrap 5 e Rodapé
    ├── dashboard.html               # Visão geral, KPIs financeiros e alertas de estoque
    ├── inventory.html               # Catálogo com comparativo Custo vs Venda e Lucro
    ├── pos.html                     # Frente de Caixa PDV com cálculo de lucro em tempo real
    ├── orders.html                  # Gestão de pedidos, status de entrega e pagamentos
    ├── clients.html                 # Controle de clientes, saldo devedor e vasilhames
    └── movements.html               # Tela de auditoria e rastreabilidade de estoque
```

---

## 🚀 Como Executar

### Opção 1: Via Docker Compose (Recomendado - sobe PostgreSQL + App)

```bash
docker compose up -d
```

Acesse o sistema no navegador:
👉 **http://localhost:8080**

---

### Opção 2: Com PostgreSQL Local ou em Nuvem

1. Crie o banco de dados no PostgreSQL:
   ```sql
   CREATE DATABASE distribebidas;
   ```

2. Configure suas credenciais em `src/main/resources/application.properties` ou por variáveis de ambiente:
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=distribebidas
   export DB_USER=postgres
   export DB_PASSWORD=sua_senha
   ```

3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

---

### Opção 3: Modo Dev com Banco H2 (Sem precisar de PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Console do H2: `http://localhost:8080/h2-console`

---

## 🌟 Funcionalidades da Gestão de Bebidas

1. **Gestão de Custos & Lucro em Estoque (Model / Entity / Service)**:
   - Comparativo claro de **Preço de Custo** vs. **Preço de Venda** em cada item.
   - Cálculo automático de **Lucro Unitário (R$)**, **Markup (%)** e **Lucro Total em Estoque**.
   - Filtros e ordenação rápida por Maior Lucro Unitário, Maior Lucro Total e Maior Margem.
   - Botões de **Entrada de Carga (+)** com recálculo financeiro e **Baixa por Avaria (-)**.

2. **PDV Web (Frente de Caixa)**:
   - Carrinho dinâmico com subtotal, desconto e **Lucro Previsto em tempo real**.
   - Formas de pagamento: PIX, Dinheiro, Cartões e **Faturado/Boleto (Fiado)** que vincula automaticamente o débito ao cliente.
   - Baixa automática e atômica no estoque com geração de registro de auditoria.

3. **Carteira de Clientes, Fiado & Vasilhames Retornáveis**:
   - Controle de saldo devedor e contagem de garrafas/cascos retornáveis pendentes.
   - Abatimento de dívida e recebimento de devolução de vasilhames.

4. **Auditoria de Estoque**:
   - Rastreabilidade total de todas as movimentações: Entradas de carga de fornecedor, Vendas no PDV e Baixas por quebra/avaria.

5. **API REST JSON para Aplicativo Android / Integrações**:
   - Endpoints sob `/api/**` prontos para consumo pelo app Android com DTOs tipados.
