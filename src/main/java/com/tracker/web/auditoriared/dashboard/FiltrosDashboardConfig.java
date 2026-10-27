package com.tracker.web.auditoriared.dashboard;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tracker.web.auditoriared.HallazgoAuditoriaDTO;
import static com.tracker.web.auditoriared.dashboard.FiltroCampoDashboard.Tipo.*;

@Configuration
public class FiltrosDashboardConfig {
    @Bean public FiltroDashboard filtroTipoAuditoria() {
        return new FiltroCampoDashboard("tipo", "Tipo de hallazgo", SELECCION, h -> Collections.singletonList(h.getEtiqueta()));
    }
    @Bean public FiltroDashboard filtroTecnologiaAuditoria() {
        return new FiltroCampoDashboard("tecnologia", "Tecnologia", SELECCION, h -> Collections.singletonList(h.getCeldaA().getTecnologia()));
    }
    @Bean public FiltroDashboard filtroCarrierAuditoria() {
        return new FiltroCampoDashboard("carrier", "Carrier", SELECCION, h -> Collections.singletonList(h.getCeldaA().getCarrier()));
    }
    @Bean public FiltroDashboard filtroBandaAuditoria() {
        return new FiltroCampoDashboard("banda", "Banda", SELECCION, h -> Collections.singletonList(h.getCeldaA().getBanda()));
    }
    @Bean public FiltroDashboard filtroCeldaAuditoria() {
        return new FiltroCampoDashboard("celda", "Celda contiene", TEXTO, h -> Arrays.asList(h.getCeldaA().getNombre(), h.getCeldaB().getNombre()));
    }
    @Bean public FiltroDashboard filtroDistanciaAuditoria() {
        return new FiltroCampoDashboard("distanciaMaxM", "Distancia maxima (m)", MAXIMO, h -> Collections.singletonList(String.valueOf(h.getDistanciaMetros())));
    }
    @Bean public FiltroDashboard filtroCalidadAuditoria() {
        return new FiltroCampoDashboard("calidad", "Evidencia de cobertura", SELECCION, h -> Collections.singletonList(calidad(h)));
    }
    private static String calidad(HallazgoAuditoriaDTO h) {
        Boolean a = h.getCeldaA().getSinTa(), b = h.getCeldaB().getSinTa();
        if (a == null || b == null || h.getCeldaA().getRadioM() == null || h.getCeldaB().getRadioM() == null) return "Geometria no disponible";
        return a || b ? "Incluye estimacion sin TA" : "Ambas celdas con TA";
    }
}
