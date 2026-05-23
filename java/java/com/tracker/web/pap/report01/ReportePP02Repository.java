package com.tracker.web.pap.report01;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReportePP02Repository extends JpaRepository<ReportePP02, Long> {

    @Query(value = "SELECT * FROM vw_PLANI_SEGUIM_PP_DASH_02 r WHERE " +
        "LOWER(r.codigo) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.nombre) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.responsable) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.proyecto) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.sub_proyecto) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.jefatura_rf) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.sub_region) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.contratista) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.region) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.sector) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.ot_pap) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.req) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "CAST(r.anio AS VARCHAR2(4)) LIKE '%' || :searchTerm || '%' OR " +
        "CAST(r.semana AS VARCHAR2(3)) LIKE '%' || :searchTerm || '%' OR " +  // <<--- NUEVO
        "LOWER(r.estatus_general) LIKE LOWER('%' || :searchTerm || '%') OR " +
        "LOWER(r.mes) LIKE LOWER('%' || :searchTerm || '%')" +
        " ORDER BY r.id ASC",
        nativeQuery = true)
    Page<ReportePP02> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}


