package com.tracker.web.inventarioV3.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.web.inventario.stock.InventarioStock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestStockRepository extends JpaRepository<TestStock, Long> {
	

	 @Query(value ="SELECT * FROM PLANI_INVENTORY_STOCK_TEST r WHERE " +
				"LOWER(r.CENTRO) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(r.NOMBRE_CENTRO) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(r.DENOMINACION_ALMCN) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(r.MATERIAL) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(r.LOTE) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(r.CATEGORIA) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(CAST(r.ID AS VARCHAR2(30))) LIKE LOWER('%' || :searchTerm || '%') OR "+
				"LOWER(r.DESCRIP_MATERIAL) LIKE LOWER('%' || :searchTerm || '%')" +
				"order by ID asc", nativeQuery = true)
		//Page<InventarioStock> search(@Param("searchTerm") String searchTerm, Pageable pageable);
		Page<TestStock> search(@Param("searchTerm") String searchTerm, Pageable pageable);
	 
	 /*
	 r.CENTRO,
	 r.ALMACEN,
	 r.NOMBRE_CENTRO,
	 r.DENOMINACION_ALMCN,
	 r.MATERIAL,
	 r.LOTE,
	 r.DESCRIP_MATERIAL,
	 r.CATEGORIA,        
	 r.STOCK_REAL*/
	 
	}