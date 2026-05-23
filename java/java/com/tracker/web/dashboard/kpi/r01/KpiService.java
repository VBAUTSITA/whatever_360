package com.tracker.web.dashboard.kpi.r01;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Service
public class KpiService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<KpiResponseDTO> getKpiData(KpiRequestDTO request) {
        String kpi = request.getKpi();
        String aggregation = request.getAggregation();

        if (kpi.toUpperCase().endsWith("_DEN")) {
            throw new IllegalArgumentException("Por favor ingrese un KPI válido (campo NUM), no el DENOMINADOR.");
        }

        boolean isComposed = kpi.toUpperCase().endsWith("_NUM");
        StringBuilder sql = new StringBuilder(
                "SELECT result_time, region, sub_region, departamento, provincia, cluster_name, distrito, " +
                "codigo_sitio, enodob_name, sector_name, cellname, carrier, ");
        
        if (isComposed) {
            String base = kpi.substring(0, kpi.length() - 4);
            sql.append("SUM(CASE WHEN ").append(base).append("_DEN = 0 THEN 1 ELSE ").append(base).append("_NUM END) AS total_num, ")
               .append("SUM(CASE WHEN ").append(base).append("_DEN = 0 THEN 1 ELSE ").append(base).append("_DEN END) AS total_den ");
        } else {
            String agg = "AVG";
            if ("sum".equalsIgnoreCase(aggregation)) agg = "SUM";
            sql.append(agg).append("(").append(kpi).append(") AS kpi_value ");
        }

        sql.append("FROM vw_report_4g_tracker_kpi01 WHERE result_time BETWEEN :startDate AND :endDate ")
           .append("AND codigo_sitio IN :codigos ")
           .append("GROUP BY result_time, region, sub_region, departamento, provincia, cluster_name, distrito, ")
           .append("codigo_sitio, enodob_name, sector_name, cellname, carrier ")
           .append("ORDER BY result_time");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("startDate", request.getStartDate());
        query.setParameter("endDate", request.getEndDate());
        query.setParameter("codigos", request.getCodigos());

        List<Object[]> results = query.getResultList();
        List<KpiResponseDTO> response = new ArrayList<>();

        for (Object[] row : results) {
            int i = 0;
            String resultTime   = row[i++].toString();
            String region       = row[i++] != null ? row[i - 1].toString() : "";
            String subRegion    = row[i++] != null ? row[i - 1].toString() : "";
            String departamento = row[i++] != null ? row[i - 1].toString() : "";
            String provincia    = row[i++] != null ? row[i - 1].toString() : "";
            String clusterName  = row[i++] != null ? row[i - 1].toString() : "";
            String distrito     = row[i++] != null ? row[i - 1].toString() : "";
            String codigoSitio  = row[i++] != null ? row[i - 1].toString() : "";
            String enodobName   = row[i++] != null ? row[i - 1].toString() : "";
            String sectorName   = row[i++] != null ? row[i - 1].toString() : "";
            String cellname     = row[i++] != null ? row[i - 1].toString() : "";
            String carrier      = row[i++] != null ? row[i - 1].toString() : "";

            Double kpiValue;
            if (isComposed) {
                Double totalNum = row[i++] != null ? ((Number) row[i - 1]).doubleValue() : null;
                Double totalDen = row[i++] != null ? ((Number) row[i - 1]).doubleValue() : null;
                kpiValue = (totalDen == null || totalDen == 0) ? null : totalNum / totalDen;
            } else {
                kpiValue = row[i++] != null ? ((Number) row[i - 1]).doubleValue() : null;
            }

            response.add(new KpiResponseDTO(resultTime, region, subRegion, departamento, provincia, clusterName,
                    distrito, codigoSitio, enodobName, sectorName, cellname, carrier, kpiValue));
        }

        return response;
    }
}

