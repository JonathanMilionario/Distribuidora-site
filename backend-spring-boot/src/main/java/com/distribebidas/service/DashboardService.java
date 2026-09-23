package com.distribebidas.service;

import com.distribebidas.model.dto.DashboardStatsDto;
import com.distribebidas.model.entity.Client;
import com.distribebidas.model.entity.Product;
import com.distribebidas.model.entity.SaleOrder;
import com.distribebidas.repository.ClientRepository;
import com.distribebidas.repository.ProductRepository;
import com.distribebidas.repository.SaleOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final SaleOrderRepository saleOrderRepository;

    public DashboardService(ProductRepository productRepository,
                            ClientRepository clientRepository,
                            SaleOrderRepository saleOrderRepository) {
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.saleOrderRepository = saleOrderRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        List<Product> products = productRepository.findAll();
        List<Client> clients = clientRepository.findAll();
        List<SaleOrder> orders = saleOrderRepository.findAll();

        double totalSales = orders.stream().mapToDouble(SaleOrder::getTotalAmount).sum();
        double totalSalesCost = orders.stream().mapToDouble(SaleOrder::getTotalCost).sum();
        double totalRealizedProfit = Math.max(0.0, totalSales - totalSalesCost);

        double totalStockCost = products.stream().mapToDouble(Product::getTotalStockCost).sum();
        double totalStockSale = products.stream().mapToDouble(Product::getTotalStockSale).sum();
        double totalProjectedProfit = Math.max(0.0, totalStockSale - totalStockCost);
        double averageMargin = totalStockCost > 0 ? ((totalProjectedProfit / totalStockCost) * 100) : 0.0;

        double totalReceivables = clients.stream().mapToDouble(c -> c.getCreditBalance() != null ? c.getCreditBalance() : 0.0).sum();
        int totalPendingBottles = clients.stream().mapToInt(c -> c.getReturnableBottlesPending() != null ? c.getReturnableBottlesPending() : 0).sum();
        long lowStockCount = products.stream().filter(Product::isLowStock).count();

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalSales(totalSales);
        stats.setTotalRealizedProfit(totalRealizedProfit);
        stats.setTotalStockCost(totalStockCost);
        stats.setTotalStockSale(totalStockSale);
        stats.setTotalProjectedProfit(totalProjectedProfit);
        stats.setAverageStockMargin(averageMargin);
        stats.setTotalReceivables(totalReceivables);
        stats.setTotalPendingBottles(totalPendingBottles);
        stats.setLowStockCount(lowStockCount);
        stats.setTotalProducts(products.size());
        stats.setTotalOrders(orders.size());
        stats.setTotalClients(clients.size());

        return stats;
    }
}
