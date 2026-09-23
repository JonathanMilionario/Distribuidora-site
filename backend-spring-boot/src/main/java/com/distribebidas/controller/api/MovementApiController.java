package com.distribebidas.controller.api;

import com.distribebidas.model.entity.StockMovement;
import com.distribebidas.service.StockMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = "*")
public class MovementApiController {

    private final StockMovementService stockMovementService;

    public MovementApiController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public ResponseEntity<List<StockMovement>> getMovements(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long productId) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(stockMovementService.getMovementsByType(type));
        }
        if (productId != null) {
            return ResponseEntity.ok(stockMovementService.getMovementsByProductId(productId));
        }
        return ResponseEntity.ok(stockMovementService.getAllMovements());
    }
}
