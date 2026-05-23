package com.tracker.web.baseline5g4g.list.parametros.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface Baseline5g4gSitesParametrosRepository extends JpaRepository<Baseline5g4gSitesParametros, Long> {

List<Baseline5g4gSitesParametros> findByParametros(String parametros);

@Query(value = "SELECT parametros, COUNT(*) FROM vw_plani_baseline5g4g_details01 GROUP BY parametros", nativeQuery = true)
List<Object[]> countByParametros();
}