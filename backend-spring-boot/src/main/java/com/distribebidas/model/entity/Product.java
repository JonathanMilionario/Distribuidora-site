package com.distribebidas.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "produtos")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(name = "unit_type", nullable = false)
    private String unitType; // Caixa c/ 24, Fardo c/ 12, etc.

    @NotNull(message = "O preço de custo é obrigatório")
    @Column(name = "cost_price", nullable = false)
    private Double costPrice = 0.0;

    @NotNull(message = "O preço de venda é obrigatório")
    @Column(name = "sale_price", nullable = false)
    private Double salePrice = 0.0;

    @NotNull
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @NotNull
    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 5;

    @Column(name = "is_returnable", nullable = false)
    private Boolean isReturnable = false;

    private String barcode = "";

    private String supplier = "";

    public Product() {}

    public Product(String name, String category, String unitType, Double costPrice, Double salePrice, Integer stockQuantity, Integer minStock, Boolean isReturnable, String barcode, String supplier) {
        this.name = name;
        this.category = category;
        this.unitType = unitType;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.minStock = minStock;
        this.isReturnable = isReturnable;
        this.barcode = barcode;
        this.supplier = supplier;
    }

    // Cálculos de Lucro e Margem no padrão Model
    public Double getUnitProfit() {
        return (salePrice != null && costPrice != null) ? (salePrice - costPrice) : 0.0;
    }

    public Double getMarkupPercentage() {
        if (costPrice != null && costPrice > 0) {
            double profit = getUnitProfit();
            return (profit / costPrice) * 100.0;
        }
        return 0.0;
    }

    public Double getMarginOnSalePercentage() {
        if (salePrice != null && salePrice > 0) {
            double profit = getUnitProfit();
            return (profit / salePrice) * 100.0;
        }
        return 0.0;
    }

    public Double getTotalStockCost() {
        return (costPrice != null && stockQuantity != null) ? (costPrice * stockQuantity) : 0.0;
    }

    public Double getTotalStockSale() {
        return (salePrice != null && stockQuantity != null) ? (salePrice * stockQuantity) : 0.0;
    }

    public Double getTotalStockProfit() {
        return (getUnitProfit() != null && stockQuantity != null) ? (getUnitProfit() * stockQuantity) : 0.0;
    }

    public boolean isLowStock() {
        return stockQuantity != null && minStock != null && stockQuantity <= minStock;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }

    public Double getSalePrice() { return salePrice; }
    public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getMinStock() { return minStock; }
    public void setMinStock(Integer minStock) { this.minStock = minStock; }

    public Boolean getIsReturnable() { return isReturnable; }
    public void setIsReturnable(Boolean returnable) { isReturnable = returnable; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}
