package com.tracker.web.baseline.maestro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EntidadRepository extends JpaRepository<Entidad, Long> {
Optional<Entidad> findByOrden(String orden);
boolean existsByOrden(String orden);
boolean existsByOrdenAndIdNot(String orden, Long id);

 
@Modifying
@Transactional
@Query(value = "DELETE FROM PLANI_BS_PARAMETROS_4G_DINAMIC", nativeQuery = true)
void deleteAllInBatch(); 
 
}