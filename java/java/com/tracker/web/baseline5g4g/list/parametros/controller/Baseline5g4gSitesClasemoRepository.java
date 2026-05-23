package com.tracker.web.baseline5g4g.list.parametros.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface Baseline5g4gSitesClasemoRepository extends JpaRepository<Baseline5g4gSitesClasemo, Long> {

List<Baseline5g4gSitesClasemo> findByClase(String clase);

@Query(value = "SELECT clase, COUNT(*) FROM vw_plani_baseline5g4g_details02 GROUP BY clase", nativeQuery = true)
List<Object[]> countByClase();
}