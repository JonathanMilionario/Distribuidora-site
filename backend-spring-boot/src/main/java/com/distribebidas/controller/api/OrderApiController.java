package com.distribebidas.controller.api;

import com.distribebidas.model.dto.CreateOrderRequestDto;
import com.distribebidas.model.entity.SaleOrder;
import com.distribebidas.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<SaleOrder>> getOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<SaleOrder> createOrder(@Valid @RequestBody CreateOrderRequestDto req) {
        SaleOrder order = orderService.createSaleOrder(
                req.getClientId(),
                req.getItems(),
                req.getPaymentMethod(),
                req.getDiscount(),
                req.getIsDelivery(),
                req.getDeliveryAddress(),
                req.getNotes()
        );
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SaleOrder> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<SaleOrder> markOrderPaid(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.markOrderPaid(id));
    }
}
