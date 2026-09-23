package com.distribebidas.model.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DailyCashReportDto {
    private String date;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalProfit = BigDecimal.ZERO;
    private Integer totalOrdersCount = 0;

    private BigDecimal cashSales = BigDecimal.ZERO;
    private BigDecimal pixSales = BigDecimal.ZERO;
    private BigDecimal creditSales = BigDecimal.ZERO;
    private BigDecimal debitSales = BigDecimal.ZERO;
    private BigDecimal fiadoSales = BigDecimal.ZERO;

    private BigDecimal openingFloat = new BigDecimal("200.00");
    private BigDecimal suppliesAmount = BigDecimal.ZERO;
    private BigDecimal bleedingsAmount = BigDecimal.ZERO;
    private BigDecimal expectedCash = BigDecimal.ZERO;
    private BigDecimal countedCash;
    private BigDecimal cashDifference = BigDecimal.ZERO;

    private boolean hasCashShortage = false; // Teve quebra de caixa?
    private boolean hasCashSurplus = false;
    private boolean isCashBalanced = true;
    private boolean isCounted = false;

    private String status = "ABERTO";
    private String notes = "";

    private List<DailySoldProductDto> soldProducts = new ArrayList<>();

    public DailyCashReportDto() {}

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public Integer getTotalOrdersCount() { return totalOrdersCount; }
    public void setTotalOrdersCount(Integer totalOrdersCount) { this.totalOrdersCount = totalOrdersCount; }

    public BigDecimal getCashSales() { return cashSales; }
    public void setCashSales(BigDecimal cashSales) { this.cashSales = cashSales; }

    public BigDecimal getPixSales() { return pixSales; }
    public void setPixSales(BigDecimal pixSales) { this.pixSales = pixSales; }

    public BigDecimal getCreditSales() { return creditSales; }
    public void setCreditSales(BigDecimal creditSales) { this.creditSales = creditSales; }

    public BigDecimal getDebitSales() { return debitSales; }
    public void setDebitSales(BigDecimal debitSales) { this.debitSales = debitSales; }

    public BigDecimal getFiadoSales() { return fiadoSales; }
    public void setFiadoSales(BigDecimal fiadoSales) { this.fiadoSales = fiadoSales; }

    public BigDecimal getOpeningFloat() { return openingFloat; }
    public void setOpeningFloat(BigDecimal openingFloat) { this.openingFloat = openingFloat; }

    public BigDecimal getSuppliesAmount() { return suppliesAmount; }
    public void setSuppliesAmount(BigDecimal suppliesAmount) { this.suppliesAmount = suppliesAmount; }

    public BigDecimal getBleedingsAmount() { return bleedingsAmount; }
    public void setBleedingsAmount(BigDecimal bleedingsAmount) { this.bleedingsAmount = bleedingsAmount; }

    public BigDecimal getExpectedCash() { return expectedCash; }
    public void setExpectedCash(BigDecimal expectedCash) { this.expectedCash = expectedCash; }

    public BigDecimal getCountedCash() { return countedCash; }
    public void setCountedCash(BigDecimal countedCash) { this.countedCash = countedCash; }

    public BigDecimal getCashDifference() { return cashDifference; }
    public void setCashDifference(BigDecimal cashDifference) { this.cashDifference = cashDifference; }

    public boolean isHasCashShortage() { return hasCashShortage; }
    public void setHasCashShortage(boolean hasCashShortage) { this.hasCashShortage = hasCashShortage; }

    public boolean isHasCashSurplus() { return hasCashSurplus; }
    public void setHasCashSurplus(boolean hasCashSurplus) { this.hasCashSurplus = hasCashSurplus; }

    public boolean isCashBalanced() { return isCashBalanced; }
    public void setCashBalanced(boolean cashBalanced) { isCashBalanced = cashBalanced; }

    public boolean isCounted() { return isCounted; }
    public void setCounted(boolean counted) { isCounted = counted; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<DailySoldProductDto> getSoldProducts() { return soldProducts; }
    public void setSoldProducts(List<DailySoldProductDto> soldProducts) { this.soldProducts = soldProducts; }
}
