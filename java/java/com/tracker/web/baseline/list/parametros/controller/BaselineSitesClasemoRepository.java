package com.tracker.web.baseline.list.parametros.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BaselineSitesClasemoRepository extends JpaRepository<BaselineSitesClasemo, Long> {

List<BaselineSitesClasemo> findByClase(String clase);

@Query(value = "SELECT clase, COUNT(*) FROM vw_plani_baseline_details02 GROUP BY clase", nativeQuery = true)
List<Object[]> countByClase();
}