package com.distribebidas.repository;

import com.distribebidas.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Client c WHERE c.creditBalance > 0")
    List<Client> findClientsWithDebt();

    @Query("SELECT c FROM Client c WHERE c.returnableBottlesPending > 0")
    List<Client> findClientsWithPendingBottles();
}
