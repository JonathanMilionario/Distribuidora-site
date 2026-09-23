package com.distribebidas.model.dto;

import java.math.BigDecimal;

public class DailySoldProductDto {
    private Long productId;
    private String productName;
    private String unitType;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private Integer quantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Double marginPercent;

    public DailySoldProductDto() {}

    public DailySoldProductDto(Long productId, String productName, String unitType,
                              BigDecimal unitPrice, BigDecimal costPrice, Integer quantitySold,
                              BigDecimal totalRevenue, BigDecimal totalProfit, Double marginPercent) {
        this.productId = productId;
        this.productName = productName;
        this.unitType = unitType;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
        this.totalProfit = totalProfit;
        this.marginPercent = marginPercent;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public Double getMarginPercent() { return marginPercent; }
    public void setMarginPercent(Double marginPercent) { this.marginPercent = marginPercent; }
}
