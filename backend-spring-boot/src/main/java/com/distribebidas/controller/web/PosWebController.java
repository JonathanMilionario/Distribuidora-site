package com.distribebidas.controller.web;

import com.distribebidas.model.dto.OrderItemRequestDto;
import com.distribebidas.model.entity.SaleOrder;
import com.distribebidas.service.ClientService;
import com.distribebidas.service.OrderService;
import com.distribebidas.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pdv")
public class PosWebController {

    private final ProductService productService;
    private final ClientService clientService;
    private final OrderService orderService;

    public PosWebController(ProductService productService,
                            ClientService clientService,
                            OrderService orderService) {
        this.productService = productService;
        this.clientService = clientService;
        this.orderService = orderService;
    }

    @GetMapping
    public String pdv(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("clients", clientService.getAllClients());
        return "pos";
    }

    @PostMapping("/finalizar")
    public String finalizarVenda(@RequestParam(required = false) Long clientId,
                                 @RequestParam List<Long> productIds,
                                 @RequestParam List<Integer> quantities,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(defaultValue = "0.0") Double discount,
                                 @RequestParam(defaultValue = "false") boolean isDelivery,
                                 @RequestParam(required = false) String deliveryAddress,
                                 @RequestParam(required = false) String notes,
                                 RedirectAttributes ra) {
        try {
            List<OrderItemRequestDto> items = new ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                if (quantities.get(i) > 0) {
                    items.add(new OrderItemRequestDto(productIds.get(i), quantities.get(i)));
                }
            }
            if (items.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Selecione ao menos um produto para vender.");
                return "redirect:/pdv";
            }

            SaleOrder order = orderService.createSaleOrder(
                    clientId, items, paymentMethod, discount, isDelivery, deliveryAddress, notes
            );
            ra.addFlashAttribute("successMessage", "Venda " + order.getOrderNumber() + " finalizada com sucesso!");
            return "redirect:/pedidos";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Erro ao processar venda: " + e.getMessage());
            return "redirect:/pdv";
        }
    }
}
