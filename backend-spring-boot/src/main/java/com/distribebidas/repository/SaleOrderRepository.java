package com.distribebidas.repository;

import com.distribebidas.model.entity.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {
    List<SaleOrder> findAllByOrderByTimestampDesc();
    List<SaleOrder> findByOrderStatus(String orderStatus);
    List<SaleOrder> findByPaymentStatus(String paymentStatus);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM SaleOrder o")
    Double sumTotalSalesAmount();

    @Query("SELECT COALESCE(SUM(o.totalCost), 0.0) FROM SaleOrder o")
    Double sumTotalSalesCost();
}
