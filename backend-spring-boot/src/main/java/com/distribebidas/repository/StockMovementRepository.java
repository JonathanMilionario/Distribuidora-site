package com.distribebidas.repository;

import com.distribebidas.model.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findAllByOrderByTimestampDesc();
    List<StockMovement> findByTypeOrderByTimestampDesc(String type);
    List<StockMovement> findByProductIdOrderByTimestampDesc(Long productId);
}
