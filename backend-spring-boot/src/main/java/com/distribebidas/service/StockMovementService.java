package com.distribebidas.service;

import com.distribebidas.model.entity.StockMovement;
import com.distribebidas.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAllByOrderByTimestampDesc();
    }

    public List<StockMovement> getMovementsByType(String type) {
        return stockMovementRepository.findByTypeOrderByTimestampDesc(type);
    }

    public List<StockMovement> getMovementsByProductId(Long productId) {
        return stockMovementRepository.findByProductIdOrderByTimestampDesc(productId);
    }
}
