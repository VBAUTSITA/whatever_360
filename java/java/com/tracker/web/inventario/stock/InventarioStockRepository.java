package com.tracker.web.inventario.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InventarioStockRepository extends JpaRepository<InventarioStock, Long> {
//Optional<InventarioStock> findByOrden(String orden);
//boolean existsByOrden(String orden);
//boolean existsByOrdenAndIdNot(String orden, Long id);

 
@Modifying
@Transactional
@Query(value = "DELETE FROM PLANI_INVENTORY_STOCK", nativeQuery = true)
void deleteAllInBatch(); 
 
}