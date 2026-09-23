package com.distribebidas.controller.web;

import com.distribebidas.model.dto.DashboardStatsDto;
import com.distribebidas.model.dto.DailyCashReportDto;
import com.distribebidas.model.entity.Product;
import com.distribebidas.model.entity.SaleOrder;
import com.distribebidas.service.DailyCashService;
import com.distribebidas.service.DashboardService;
import com.distribebidas.service.OrderService;
import com.distribebidas.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardWebController {

    private final DashboardService dashboardService;
    private final ProductService productService;
    private final OrderService orderService;
    private final DailyCashService dailyCashService;

    public DashboardWebController(DashboardService dashboardService,
                                  ProductService productService,
                                  OrderService orderService,
                                  DailyCashService dailyCashService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
        this.orderService = orderService;
        this.dailyCashService = dailyCashService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        List<Product> lowStock = productService.getLowStockProducts();
        List<SaleOrder> orders = orderService.getAllOrders();
        DailyCashReportDto dailyReport = dailyCashService.getTodayReport();

        model.addAttribute("totalSales", stats.getTotalSales());
        model.addAttribute("totalRealizedProfit", stats.getTotalRealizedProfit());
        model.addAttribute("totalStockCost", stats.getTotalStockCost());
        model.addAttribute("totalStockSale", stats.getTotalStockSale());
        model.addAttribute("totalProjectedProfit", stats.getTotalProjectedProfit());
        model.addAttribute("averageMargin", stats.getAverageStockMargin());
        model.addAttribute("totalReceivables", stats.getTotalReceivables());
        model.addAttribute("totalPendingBottles", stats.getTotalPendingBottles());
        model.addAttribute("lowStockProducts", lowStock);
        model.addAttribute("recentOrders", orders.stream().limit(5).toList());
        model.addAttribute("productsCount", stats.getTotalProducts());
        model.addAttribute("dailyReport", dailyReport);

        return "dashboard";
    }
}
