package com.distribebidas.service;

import com.distribebidas.model.dto.DailyCashReportDto;
import com.distribebidas.model.dto.DailySoldProductDto;
import com.distribebidas.model.entity.SaleOrder;
import com.distribebidas.model.entity.SaleOrderItem;
import com.distribebidas.repository.SaleOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyCashService {

    private final SaleOrderRepository saleOrderRepository;

    private BigDecimal openingFloat = new BigDecimal("200.00");
    private BigDecimal suppliesAmount = BigDecimal.ZERO;
    private BigDecimal bleedingsAmount = BigDecimal.ZERO;
    private BigDecimal countedCash = null;
    private String status = "ABERTO";
    private String notes = "";

    public DailyCashService(SaleOrderRepository saleOrderRepository) {
        this.saleOrderRepository = saleOrderRepository;
    }

    public DailyCashReportDto getTodayReport() {
        LocalDate today = LocalDate.now();
        List<SaleOrder> allOrders = saleOrderRepository.findAll();

        List<SaleOrder> todayOrders = allOrders.stream()
                .filter(o -> o.getTimestamp() != null && o.getTimestamp().toLocalDate().equals(today))
                .toList();

        DailyCashReportDto report = new DailyCashReportDto();
        report.setDate(today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        report.setOpeningFloat(openingFloat);
        report.setSuppliesAmount(suppliesAmount);
        report.setBleedingsAmount(bleedingsAmount);
        report.setStatus(status);
        report.setNotes(notes);

        double totalSales = 0.0;
        double totalCost = 0.0;
        double cashSales = 0.0;
        double pixSales = 0.0;
        double creditSales = 0.0;
        double debitSales = 0.0;
        double fiadoSales = 0.0;

        List<SaleOrderItem> todayItems = new ArrayList<>();

        for (SaleOrder order : todayOrders) {
            double orderAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
            double orderCost = order.getTotalCost() != null ? order.getTotalCost() : 0.0;

            totalSales += orderAmount;
            totalCost += orderCost;

            String method = order.getPaymentMethod() != null ? order.getPaymentMethod().toUpperCase() : "";
            switch (method) {
                case "DINHEIRO":
                    cashSales += orderAmount;
                    break;
                case "PIX":
                    pixSales += orderAmount;
                    break;
                case "CARTAO_CREDITO":
                    creditSales += orderAmount;
                    break;
                case "CARTAO_DEBITO":
                    debitSales += orderAmount;
                    break;
                case "FATURADO_BOLETO":
                    fiadoSales += orderAmount;
                    break;
                default:
                    cashSales += orderAmount;
                    break;
            }

            if (order.getItems() != null) {
                todayItems.addAll(order.getItems());
            }
        }

        double totalProfit = Math.max(0.0, totalSales - totalCost);

        report.setTotalSales(BigDecimal.valueOf(totalSales).setScale(2, RoundingMode.HALF_UP));
        report.setTotalCost(BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP));
        report.setTotalProfit(BigDecimal.valueOf(totalProfit).setScale(2, RoundingMode.HALF_UP));
        report.setTotalOrdersCount(todayOrders.size());

        report.setCashSales(BigDecimal.valueOf(cashSales).setScale(2, RoundingMode.HALF_UP));
        report.setPixSales(BigDecimal.valueOf(pixSales).setScale(2, RoundingMode.HALF_UP));
        report.setCreditSales(BigDecimal.valueOf(creditSales).setScale(2, RoundingMode.HALF_UP));
        report.setDebitSales(BigDecimal.valueOf(debitSales).setScale(2, RoundingMode.HALF_UP));
        report.setFiadoSales(BigDecimal.valueOf(fiadoSales).setScale(2, RoundingMode.HALF_UP));

        // Group by product to list sold merchandise, unit price and profit
        Map<Long, List<SaleOrderItem>> grouped = todayItems.stream()
                .filter(i -> i.getProduct() != null)
                .collect(Collectors.groupingBy(i -> i.getProduct().getId()));

        List<DailySoldProductDto> soldList = new ArrayList<>();
        for (Map.Entry<Long, List<SaleOrderItem>> entry : grouped.entrySet()) {
            List<SaleOrderItem> items = entry.getValue();
            SaleOrderItem first = items.get(0);
            int qty = items.stream().mapToInt(SaleOrderItem::getQuantity).sum();
            double rev = items.stream().mapToDouble(SaleOrderItem::getSubtotal).sum();
            double cost = first.getUnitCost() != null ? first.getUnitCost() : 0.0;
            double unitPrice = qty > 0 ? (rev / qty) : (first.getUnitPrice() != null ? first.getUnitPrice() : 0.0);
            double profit = Math.max(0.0, rev - (cost * qty));
            double margin = cost > 0 ? ((unitPrice - cost) / cost) * 100 : 0.0;

            soldList.add(new DailySoldProductDto(
                    entry.getKey(),
                    first.getProductName(),
                    first.getUnitType(),
                    BigDecimal.valueOf(unitPrice).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP),
                    qty,
                    BigDecimal.valueOf(rev).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(profit).setScale(2, RoundingMode.HALF_UP),
                    Math.round(margin * 10.0) / 10.0
            ));
        }
        soldList.sort((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()));
        report.setSoldProducts(soldList);

        // Expected Cash in Drawer Calculation
        BigDecimal expected = openingFloat
                .add(report.getCashSales())
                .add(suppliesAmount)
                .subtract(bleedingsAmount);
        if (expected.compareTo(BigDecimal.ZERO) < 0) {
            expected = BigDecimal.ZERO;
        }
        report.setExpectedCash(expected.setScale(2, RoundingMode.HALF_UP));

        // Quebra de caixa / Cash discrepancy logic
        if (countedCash != null) {
            report.setCounted(true);
            report.setCountedCash(countedCash.setScale(2, RoundingMode.HALF_UP));
            BigDecimal diff = countedCash.subtract(expected).setScale(2, RoundingMode.HALF_UP);
            report.setCashDifference(diff);

            if (diff.compareTo(new BigDecimal("-0.05")) < 0) {
                report.setHasCashShortage(true); // TEVE QUEBRA DE CAIXA
                report.setCashBalanced(false);
            } else if (diff.compareTo(new BigDecimal("0.05")) > 0) {
                report.setHasCashSurplus(true); // SOBRA DE CAIXA
                report.setCashBalanced(false);
            } else {
                report.setCashBalanced(true); // SEM QUEBRA DE CAIXA
            }
        } else {
            report.setCounted(false);
            report.setCashBalanced(false);
        }

        return report;
    }

    public void countCash(BigDecimal counted, String notes) {
        this.countedCash = counted;
        this.notes = notes != null ? notes : "";
    }

    public void addSupply(BigDecimal amount, String reason) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.suppliesAmount = this.suppliesAmount.add(amount);
        }
    }

    public void addBleeding(BigDecimal amount, String reason) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.bleedingsAmount = this.bleedingsAmount.add(amount);
        }
    }

    public void updateOpeningFloat(BigDecimal newFloat) {
        if (newFloat != null && newFloat.compareTo(BigDecimal.ZERO) >= 0) {
            this.openingFloat = newFloat;
        }
    }

    public void closeRegister(BigDecimal counted, String notes) {
        this.status = "FECHADO";
        if (counted != null) {
            this.countedCash = counted;
        }
        this.notes = notes != null ? notes : "";
    }

    public void reopenRegister() {
        this.status = "ABERTO";
    }
}
