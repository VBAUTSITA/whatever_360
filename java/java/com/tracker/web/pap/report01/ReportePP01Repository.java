package com.tracker.web.pap.report01;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReportePP01Repository extends JpaRepository<ReportePP01, Long> {

    @Query(value = "SELECT * FROM vw_PLANI_SEGUIM_PP_DASH_01 r WHERE " +
            "LOWER(r.tipo_seguimiento) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "CAST(r.ot AS VARCHAR2(100)) LIKE '%' || :searchTerm || '%' OR " +
            "CAST(r.req_alta AS VARCHAR2(100)) LIKE '%' || :searchTerm || '%' OR " +
            "LOWER(r.codigosite) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.nombresite) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.estado_sitio) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.tipo_estacion) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.etiqueta) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.proyecto) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.sub_proyecto) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.region) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.subregion) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.contrata) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.status_ot) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.estado_total) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.fase_proyecto) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.etapa_evolucion) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.subproy_const) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "TO_CHAR(r.forecast_etapa, 'YYYY-MM-DD') LIKE '%' || :searchTerm || '%' OR " +
            "TO_CHAR(r.forecast_proyecto, 'YYYY-MM-DD') LIKE '%' || :searchTerm || '%' OR " +
            "CAST(r.cambios_forecast_proyecto AS VARCHAR2(100)) LIKE '%' || :searchTerm || '%' OR " +
            "CAST(r.latitud AS VARCHAR2(100)) LIKE '%' || :searchTerm || '%' OR " +
            "CAST(r.longitud AS VARCHAR2(100)) LIKE '%' || :searchTerm || '%' OR " +
            "TO_CHAR(r.fecha_activacion, 'YYYY-MM-DD') LIKE '%' || :searchTerm || '%' OR " +
            "TO_CHAR(r.fecha_on_air, 'YYYY-MM-DD') LIKE '%' || :searchTerm || '%' OR " +
            "LOWER(r.seguimiento_historico) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.departamento) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.provincia) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "LOWER(r.distrito) LIKE LOWER('%' || :searchTerm || '%') OR " +
            "TO_CHAR(r.fecha_carga, 'YYYY-MM-DD') LIKE '%' || :searchTerm || '%' OR " + 
            "CAST(r.WEEK_FECHA_ACTIVACION AS VARCHAR2(100)) LIKE '%' || :searchTerm || '%' " +
            "ORDER BY r.OT ASC", nativeQuery = true)
    Page<ReportePP01> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}

