package com.distribebidas.controller.web;

import com.distribebidas.service.StockMovementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movimentacoes")
public class MovementWebController {

    private final StockMovementService stockMovementService;

    public MovementWebController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public String movimentacoes(Model model) {
        model.addAttribute("movements", stockMovementService.getAllMovements());
        return "movements";
    }
}
