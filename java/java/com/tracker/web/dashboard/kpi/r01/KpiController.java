package com.tracker.web.dashboard.kpi.r01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@RestController
public class KpiController {

    @Autowired
    private KpiService kpiService;

    @PersistenceContext
    private EntityManager entityManager;

    // Endpoint principal para obtener datos del KPI
    @PostMapping("/api/kpi-data")
    public List<KpiResponseDTO> getKpiData(@RequestBody KpiRequestDTO request) {
        return kpiService.getKpiData(request);
    }

    // Endpoint para autocompletado por zona
    @GetMapping("/api/buscar-zona")
    public List<String> buscarZona(@RequestParam String tipo, @RequestParam String termino) {
        String columna;
        switch (tipo.toLowerCase()) {
            case "distrito": columna = "distrito"; break;
            case "departamento": columna = "departamento"; break;
            case "provincia": columna = "provincia"; break;
            case "region": columna = "region"; break;
            case "sub_region": columna = "sub_region"; break;
            default: throw new IllegalArgumentException("Tipo de zona no válido");
        }

        String sql = "SELECT DISTINCT " + columna + " FROM tm_conf_actual_celda_4g " +
                     "WHERE UPPER(" + columna + ") LIKE UPPER(:termino) ORDER BY 1 FETCH FIRST 10 ROWS ONLY";

        return entityManager.createNativeQuery(sql)
            .setParameter("termino", "%" + termino + "%")
            .getResultList();
    }

    // Endpoint para obtener códigos de sitio desde zona seleccionada
    @GetMapping("/api/obtener-codigos")
    public List<String> obtenerCodigosPorZona(@RequestParam String tipo, @RequestParam String valor) {
        String columna;
        switch (tipo.toLowerCase()) {
            case "distrito": columna = "distrito"; break;
            case "provincia": columna = "provincia"; break;
            case "departamento": columna = "departamento"; break;
            case "region": columna = "region"; break;
            case "sub_region": columna = "sub_region"; break;
            default: throw new IllegalArgumentException("Tipo de zona no válido");
        }

        String sql = "SELECT DISTINCT codigo_sitio FROM tm_conf_actual_celda_4g " +
                     "WHERE UPPER(" + columna + ") = UPPER(:valor)";

        return entityManager.createNativeQuery(sql)
            .setParameter("valor", valor)
            .getResultList();
    }
}

