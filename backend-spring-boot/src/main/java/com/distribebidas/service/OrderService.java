package com.distribebidas.service;

import com.distribebidas.model.dto.OrderItemRequestDto;
import com.distribebidas.model.entity.Client;
import com.distribebidas.model.entity.Product;
import com.distribebidas.model.entity.SaleOrder;
import com.distribebidas.model.entity.SaleOrderItem;
import com.distribebidas.model.entity.StockMovement;
import com.distribebidas.repository.ClientRepository;
import com.distribebidas.repository.ProductRepository;
import com.distribebidas.repository.SaleOrderRepository;
import com.distribebidas.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {

    private final SaleOrderRepository saleOrderRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final StockMovementRepository stockMovementRepository;

    public OrderService(SaleOrderRepository saleOrderRepository,
                        ProductRepository productRepository,
                        ClientRepository clientRepository,
                        StockMovementRepository stockMovementRepository) {
        this.saleOrderRepository = saleOrderRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<SaleOrder> getAllOrders() {
        return saleOrderRepository.findAllByOrderByTimestampDesc();
    }

    public SaleOrder getOrderById(Long id) {
        return saleOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
    }

    @Transactional
    public SaleOrder createSaleOrder(Long clientId,
                                    List<OrderItemRequestDto> itemRequests,
                                    String paymentMethod,
                                    Double discount,
                                    boolean isDelivery,
                                    String deliveryAddress,
                                    String notes) {
        Client client = clientId != null ? clientRepository.findById(clientId).orElse(null) : null;
        String clientName = client != null ? client.getName() : "Consumidor Balcão";

        SaleOrder order = new SaleOrder();
        order.setOrderNumber("#DIST-" + (1000 + new Random().nextInt(9000)));
        order.setClient(client);
        order.setClientName(clientName);
        order.setTimestamp(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "DINHEIRO");
        order.setDiscount(discount != null ? discount : 0.0);
        order.setIsDelivery(isDelivery);
        order.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : "");
        order.setNotes(notes != null ? notes : "");

        boolean isCredit = "FATURADO_BOLETO".equalsIgnoreCase(paymentMethod);
        order.setPaymentStatus(isCredit ? "PENDENTE" : "PAGO");
        order.setOrderStatus(isDelivery ? "EM_SEPARACAO" : "CONCLUIDO");

        double totalAmount = 0.0;
        double totalCost = 0.0;
        int returnableItemsCount = 0;

        for (OrderItemRequestDto req : itemRequests) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + req.getProductId()));

            if (product.getStockQuantity() < req.getQuantity()) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + product.getName() +
                        ". Disponível: " + product.getStockQuantity() + ", Solicitado: " + req.getQuantity());
            }

            int prevStock = product.getStockQuantity();
            int newStock = prevStock - req.getQuantity();
            product.setStockQuantity(newStock);
            productRepository.save(product);

            // Registra movimento de auditoria
            StockMovement movement = new StockMovement(
                    product,
                    "VENDA",
                    req.getQuantity(),
                    prevStock,
                    newStock,
                    "Venda Pedido " + order.getOrderNumber()
            );
            stockMovementRepository.save(movement);

            SaleOrderItem item = new SaleOrderItem(
                    product,
                    req.getQuantity(),
                    product.getSalePrice(),
                    product.getCostPrice(),
                    product.getUnitType()
            );
            order.addItem(item);

            totalAmount += (product.getSalePrice() * req.getQuantity());
            totalCost += (product.getCostPrice() * req.getQuantity());

            if (Boolean.TRUE.equals(product.getIsReturnable())) {
                returnableItemsCount += req.getQuantity();
            }
        }

        double finalTotal = Math.max(0.0, totalAmount - (discount != null ? discount : 0.0));
        order.setTotalAmount(finalTotal);
        order.setTotalCost(totalCost);

        SaleOrder savedOrder = saleOrderRepository.save(order);

        // Se faturado / boleto a prazo, atualiza dívida do cliente
        if (client != null) {
            if (isCredit) {
                double currentDebt = client.getCreditBalance() != null ? client.getCreditBalance() : 0.0;
                client.setCreditBalance(currentDebt + finalTotal);
            }
            if (returnableItemsCount > 0) {
                int currentBottles = client.getReturnableBottlesPending() != null ? client.getReturnableBottlesPending() : 0;
                client.setReturnableBottlesPending(currentBottles + returnableItemsCount);
            }
            clientRepository.save(client);
        }

        return savedOrder;
    }

    @Transactional
    public SaleOrder updateOrderStatus(Long orderId, String newStatus) {
        SaleOrder order = getOrderById(orderId);
        order.setOrderStatus(newStatus);
        return saleOrderRepository.save(order);
    }

    @Transactional
    public SaleOrder markOrderPaid(Long orderId) {
        SaleOrder order = getOrderById(orderId);
        order.setPaymentStatus("PAGO");
        return saleOrderRepository.save(order);
    }
}
