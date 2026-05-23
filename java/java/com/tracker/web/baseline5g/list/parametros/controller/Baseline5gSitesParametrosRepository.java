package com.tracker.web.baseline5g.list.parametros.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface Baseline5gSitesParametrosRepository extends JpaRepository<Baseline5gSitesParametros, Long> {

List<Baseline5gSitesParametros> findByParametros(String parametros);

@Query(value = "SELECT parametros, COUNT(*) FROM vw_plani_baseline5g_details01 GROUP BY parametros", nativeQuery = true)
List<Object[]> countByParametros();
}