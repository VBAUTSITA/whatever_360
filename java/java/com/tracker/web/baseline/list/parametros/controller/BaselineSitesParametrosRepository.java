package com.tracker.web.baseline.list.parametros.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BaselineSitesParametrosRepository extends JpaRepository<BaselineSitesParametros, Long> {

List<BaselineSitesParametros> findByParametros(String parametros);

@Query(value = "SELECT parametros, COUNT(*) FROM vw_plani_baseline_details01 GROUP BY parametros", nativeQuery = true)
List<Object[]> countByParametros();
}