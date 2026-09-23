package com.distribebidas.model.dto;

public class DashboardStatsDto {

    private double totalSales;
    private double totalRealizedProfit;
    private double totalStockCost;
    private double totalStockSale;
    private double totalProjectedProfit;
    private double averageStockMargin;
    private double totalReceivables;
    private int totalPendingBottles;
    private long lowStockCount;
    private int totalProducts;
    private int totalOrders;
    private int totalClients;

    public DashboardStatsDto() {}

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalRealizedProfit() {
        return totalRealizedProfit;
    }

    public void setTotalRealizedProfit(double totalRealizedProfit) {
        this.totalRealizedProfit = totalRealizedProfit;
    }

    public double getTotalStockCost() {
        return totalStockCost;
    }

    public void setTotalStockCost(double totalStockCost) {
        this.totalStockCost = totalStockCost;
    }

    public double getTotalStockSale() {
        return totalStockSale;
    }

    public void setTotalStockSale(double totalStockSale) {
        this.totalStockSale = totalStockSale;
    }

    public double getTotalProjectedProfit() {
        return totalProjectedProfit;
    }

    public void setTotalProjectedProfit(double totalProjectedProfit) {
        this.totalProjectedProfit = totalProjectedProfit;
    }

    public double getAverageStockMargin() {
        return averageStockMargin;
    }

    public void setAverageStockMargin(double averageStockMargin) {
        this.averageStockMargin = averageStockMargin;
    }

    public double getTotalReceivables() {
        return totalReceivables;
    }

    public void setTotalReceivables(double totalReceivables) {
        this.totalReceivables = totalReceivables;
    }

    public int getTotalPendingBottles() {
        return totalPendingBottles;
    }

    public void setTotalPendingBottles(int totalPendingBottles) {
        this.totalPendingBottles = totalPendingBottles;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(int totalClients) {
        this.totalClients = totalClients;
    }
}
