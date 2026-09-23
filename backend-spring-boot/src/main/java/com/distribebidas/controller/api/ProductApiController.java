package com.distribebidas.controller.api;

import com.distribebidas.model.dto.StockAdjustmentDto;
import com.distribebidas.model.entity.Product;
import com.distribebidas.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/adjust-stock")
    public ResponseEntity<Product> adjustStock(
            @PathVariable Long id,
            @RequestParam int delta,
            @RequestParam String type,
            @RequestParam(defaultValue = "") String reason) {
        return ResponseEntity.ok(productService.adjustStock(id, delta, type, reason));
    }

    @PostMapping("/adjust")
    public ResponseEntity<Product> adjustStockWithDto(@Valid @RequestBody StockAdjustmentDto dto) {
        return ResponseEntity.ok(productService.adjustStock(
                dto.getProductId(),
                dto.getDelta(),
                dto.getType(),
                dto.getReason()
        ));
    }
}
