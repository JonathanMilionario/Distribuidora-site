package com.distribebidas.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "itens_pedido_venda")
public class SaleOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private SaleOrder order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "unit_cost", nullable = false)
    private Double unitCost;

    @Column(nullable = false)
    private Double subtotal;

    @Column(name = "unit_type", nullable = false)
    private String unitType;

    public SaleOrderItem() {}

    public SaleOrderItem(Product product, Integer quantity, Double unitPrice, Double unitCost, String unitType) {
        this.product = product;
        this.productName = product != null ? product.getName() : "";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.unitCost = unitCost;
        this.subtotal = unitPrice * quantity;
        this.unitType = unitType;
    }

    public Double getItemProfit() {
        return (unitPrice != null && unitCost != null && quantity != null) ?
                ((unitPrice - unitCost) * quantity) : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SaleOrder getOrder() { return order; }
    public void setOrder(SaleOrder order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }
}
