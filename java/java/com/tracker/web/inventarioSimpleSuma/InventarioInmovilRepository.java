package com.tracker.web.inventarioSimpleSuma;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface InventarioInmovilRepository extends JpaRepository<InventarioInmovil, String> {

	//List<InventarioInmovil> findBySemana(Integer semana);
    
	//Page<InventarioInmovil> findBySemana(Integer semana);

    Page<InventarioInmovil> findBySemana(Integer semana, Pageable pageable);
    
    List<InventarioInmovil> findBySemanaAndCategoria(Integer semana, String categoria);

    // Optional: contains search
    List<InventarioInmovil> findBySemanaAndCategoriaContainingIgnoreCase(Integer semana, String categoria);
    
    //Asimilar
    
	//Page<InventarioInmovil> search(@Param("semana") Integer semana, Pageable pageable);

}
