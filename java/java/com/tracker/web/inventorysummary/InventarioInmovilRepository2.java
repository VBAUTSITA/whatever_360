package com.tracker.web.inventorysummary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.tracker.web.inventorysummary.InventarioInmovil2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface InventarioInmovilRepository2
        extends JpaRepository<InventarioInmovil2, String> {
    List<InventarioInmovil2> findBySemana(Integer semana);
}
