package com.distribebidas.service;

import com.distribebidas.model.entity.Product;
import com.distribebidas.model.entity.StockMovement;
import com.distribebidas.repository.ProductRepository;
import com.distribebidas.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public ProductService(ProductRepository productRepository, StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product adjustStock(Long productId, int quantityDelta, String type, String reason) {
        Product product = getProductById(productId);
        int prevStock = product.getStockQuantity();
        int newStock = Math.max(0, prevStock + quantityDelta);

        product.setStockQuantity(newStock);
        productRepository.save(product);

        StockMovement movement = new StockMovement(
                product,
                type,
                Math.abs(quantityDelta),
                prevStock,
                newStock,
                reason
        );
        stockMovementRepository.save(movement);

        return product;
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }
}
