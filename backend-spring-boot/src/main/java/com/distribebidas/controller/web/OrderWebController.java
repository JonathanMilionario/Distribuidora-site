package com.distribebidas.controller.web;

import com.distribebidas.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class OrderWebController {

    private final OrderService orderService;

    public OrderWebController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String pedidos(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders";
    }

    @PostMapping("/{id}/status")
    public String alterarStatusPedido(@PathVariable Long id,
                                     @RequestParam String status,
                                     RedirectAttributes ra) {
        orderService.updateOrderStatus(id, status);
        ra.addFlashAttribute("successMessage", "Status do pedido atualizado para: " + status);
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/pagar")
    public String pagarPedido(@PathVariable Long id, RedirectAttributes ra) {
        orderService.markOrderPaid(id);
        ra.addFlashAttribute("successMessage", "Pagamento do pedido confirmado!");
        return "redirect:/pedidos";
    }
}
