package com.tracker.web.baseline5g.list.parametros.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface Baseline5gSitesClasemoRepository extends JpaRepository<Baseline5gSitesClasemo, Long> {

List<Baseline5gSitesClasemo> findByClase(String clase);

@Query(value = "SELECT clase, COUNT(*) FROM vw_plani_baseline5g_details02 GROUP BY clase", nativeQuery = true)
List<Object[]> countByClase();
}