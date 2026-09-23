package com.distribebidas.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos_venda")
public class SaleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost = 0.0;

    private Double discount = 0.0;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // PIX, DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, FATURADO_BOLETO

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // PAGO, PENDENTE, CANCELADO

    @Column(name = "order_status", nullable = false)
    private String orderStatus; // CONCLUIDO, EM_SEPARACAO, EM_ROTA, CANCELADO

    @Column(name = "is_delivery", nullable = false)
    private Boolean isDelivery = false;

    @Column(name = "delivery_address")
    private String deliveryAddress = "";

    private String notes = "";

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleOrderItem> items = new ArrayList<>();

    public SaleOrder() {}

    public Double getGrossProfit() {
        return (totalAmount != null && totalCost != null) ? (totalAmount - totalCost) : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Boolean getIsDelivery() { return isDelivery != null && isDelivery; }
    public void setIsDelivery(Boolean delivery) { isDelivery = delivery; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<SaleOrderItem> getItems() { return items; }
    public void setItems(List<SaleOrderItem> items) { this.items = items; }

    public void addItem(SaleOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
