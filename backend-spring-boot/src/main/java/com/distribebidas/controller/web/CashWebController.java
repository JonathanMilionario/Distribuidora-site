package com.distribebidas.controller.web;

import com.distribebidas.model.dto.DailyCashReportDto;
import com.distribebidas.service.DailyCashService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cash")
public class CashWebController {

    private final DailyCashService dailyCashService;

    public CashWebController(DailyCashService dailyCashService) {
        this.dailyCashService = dailyCashService;
    }

    @GetMapping
    public String showDailyCash(Model model) {
        DailyCashReportDto report = dailyCashService.getTodayReport();
        model.addAttribute("report", report);
        return "cash";
    }

    @PostMapping("/count")
    public String countCash(@RequestParam("countedCash") BigDecimal countedCash,
                            @RequestParam(value = "notes", required = false) String notes) {
        dailyCashService.countCash(countedCash, notes);
        return "redirect:/cash";
    }

    @PostMapping("/supply")
    public String addSupply(@RequestParam("amount") BigDecimal amount,
                            @RequestParam(value = "reason", required = false) String reason) {
        dailyCashService.addSupply(amount, reason);
        return "redirect:/cash";
    }

    @PostMapping("/bleeding")
    public String addBleeding(@RequestParam("amount") BigDecimal amount,
                             @RequestParam(value = "reason", required = false) String reason) {
        dailyCashService.addBleeding(amount, reason);
        return "redirect:/cash";
    }

    @PostMapping("/opening-float")
    public String updateOpeningFloat(@RequestParam("openingFloat") BigDecimal openingFloat) {
        dailyCashService.updateOpeningFloat(openingFloat);
        return "redirect:/cash";
    }

    @PostMapping("/close")
    public String closeRegister(@RequestParam(value = "countedCash", required = false) BigDecimal countedCash,
                               @RequestParam(value = "notes", required = false) String notes) {
        dailyCashService.closeRegister(countedCash, notes);
        return "redirect:/cash";
    }

    @PostMapping("/reopen")
    public String reopenRegister() {
        dailyCashService.reopenRegister();
        return "redirect:/cash";
    }
}
