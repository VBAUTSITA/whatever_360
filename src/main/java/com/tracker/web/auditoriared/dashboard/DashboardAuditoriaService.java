package com.tracker.web.auditoriared.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.tracker.web.auditoriared.*;

@Service
public class DashboardAuditoriaService {
    private final AuditoriaRedService auditoria;
    private final CatalogoFiltrosDashboard filtros;
    public DashboardAuditoriaService(AuditoriaRedService auditoria, CatalogoFiltrosDashboard filtros) {
        this.auditoria = auditoria; this.filtros = filtros;
    }
    public AuditoriaRedService getAuditoria() { return auditoria; }
    public List<Map<String, Object>> definiciones() { return filtros.describir(Collections.emptyList()); }

    public Map<String, Object> consultar(SolicitudDashboard s) {
        if (s == null || s.getPagina() < 1 || s.getTamanoPagina() < 1 || s.getTamanoPagina() > 100
                || s.getLimiteVecinas() < 1 || s.getLimiteVecinas() > 200
                || (s.getCeldaObjetivo() != null && s.getCeldaObjetivo().length() > 200)) {
            throw new IllegalArgumentException("Solicitud de panel invalida; use paginas de 1 a 100 filas y de 1 a 200 vecinas");
        }
        filtros.validar(s.getFiltros());
        ResultadoAuditoria r = auditoria.auditar(s);
        List<HallazgoAuditoriaDTO> visibles = filtros.aplicar(r.getHallazgos(), s.getFiltros());
        visibles.sort(Comparator.comparingInt((HallazgoAuditoriaDTO h) -> prioridad(h.getTipo()))
                .thenComparingDouble(HallazgoAuditoriaDTO::getDistanciaMetros)
                .thenComparing(HallazgoAuditoriaDTO::getTipo).thenComparing(h -> h.getCeldaA().getNombre())
                .thenComparing(h -> h.getCeldaB().getNombre()));
        int paginas = Math.max(1, (visibles.size() + s.getTamanoPagina() - 1) / s.getTamanoPagina());
        if (s.getPagina() > paginas) throw new IllegalArgumentException("La pagina solicitada no existe");
        int desde = (s.getPagina() - 1) * s.getTamanoPagina();
        List<String> avisos = new ArrayList<>(r.getAvisos());
        avisos.add("Los indicadores describen reglas PCI/RSI y geometria estimada; no son mediciones de interferencia RF.");
        avisos.add("Frecuencia: carrier/banda no confirma un canal RF común; los hallazgos son potenciales hasta validar EARFCN/NRARFCN. Confusión PCI real no evaluada: falta relación servidora/dos vecinas y frecuencia.");
        avisos.add("RSI se deriva del lookup TABLA_RSI del motor; no se lee RSI/PRACH configurado. Las reservas guardadas de Recursos no se incorporan automáticamente.");
        avisos.add("El margen cubre pares PCI/RSI hasta el radio de reuso. PSS y respaldo sin TA usan únicamente el contexto cargado, que puede omitir vecinas externas; no acreditan que la red esté libre de riesgo.");
        avisos.add("Fecha y edad corresponden a esta extracción/cálculo; las consultas no aportan fecha de actualización de la configuración de red ni de sus histogramas TA.");
        avisos.add("Azimut efectivo y ancho de haz son supuestos del planner por nombre/TXRX; por ejemplo, 5G usa 100° por defecto. No se consulta patrón de antena ni beamforming real.");
        avisos.add("Filas con coordenadas no convertibles quedan fuera de la consulta geográfica antes del conteo leído; no se puede ubicarlas ni cuantificarlas para esta área.");
        if (r.isCapped()) avisos.add("Resultado incompleto: se alcanzo el limite de hallazgos. Reduzca el area antes de decidir.");
        int invalidas = r.getResumen().stream().mapToInt(ResumenTecnologia::getDescartadasSinDatos).sum();
        if (invalidas > 0) avisos.add(invalidas + " filas fueron excluidas por datos incompletos o invalidos.");
        int duplicadas = r.getResumen().stream().mapToInt(ResumenTecnologia::getDuplicadas).sum();
        if (duplicadas > 0) avisos.add(duplicadas + " filas adicionales comparten identidad tecnología/nombre; sólo se consolidan configuraciones equivalentes.");
        int conflictivas = r.getResumen().stream().mapToInt(ResumenTecnologia::getDuplicadasConflictivas).sum();
        if (conflictivas > 0) avisos.add(conflictivas + " identidades con configuraciones contradictorias fueron excluidas por completo; revise la fuente oficial.");
        int sinRadio = r.getResumen().stream().mapToInt(ResumenTecnologia::getGeometriaSinRadio).sum();
        if (sinRadio > 0) avisos.add(sinRadio + " celdas mostradas no tienen radio calculable; TA abierto/desbordado o sin vecinas. Ausencia de PSS no significa ausencia de riesgo.");
        List<CeldaAuditoriaDTO> celdas = new ArrayList<>();
        for (CeldaAuditoriaDTO c : r.getCeldas()) if (c.isDentroArea()) celdas.add(c);
        celdas.sort(Comparator.comparing(CeldaAuditoriaDTO::getTecnologia).thenComparing(CeldaAuditoriaDTO::getNombre));
        CeldaAuditoriaDTO objetivo = null;
        List<InteraccionVecinaDTO> vecinos = Collections.emptyList();
        if (s.getCeldaObjetivo() != null && !s.getCeldaObjetivo().trim().isEmpty()) {
            for (CeldaAuditoriaDTO c : celdas) if (c.getNombre().equalsIgnoreCase(s.getCeldaObjetivo().trim())
                    && c.getTecnologia().equals(s.getTecnologiaObjetivo())) objetivo = c;
            if (objetivo == null) throw new IllegalArgumentException("Seleccione una celda del area y su tecnologia");
            vecinos = auditoria.vecinos(r, objetivo.getTecnologia(), objetivo.getNombre(), s.getLimiteVecinas());
            avisos.add("Vecinas compatibles por canal explícito o grupo candidato carrier/banda dentro del contexto cargado (área y margen); las estimaciones no cubren toda la red.");
        }
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("version", 1); respuesta.put("alcance", r.getAlcance()); respuesta.put("resumen", r.getResumen());
        respuesta.put("generadoEn", r.getGeneradoEn()); respuesta.put("desdeCache", r.isDesdeCache());
        respuesta.put("generadoEnEpochMs", r.getGeneradoEnEpochMs()); respuesta.put("edadSegundos", r.getEdadSegundos());
        respuesta.put("zonaHoraria", r.getZonaHoraria());
        respuesta.put("criterioOrden", "PCI potencial, PSS, RSI derivada; luego distancia y nombres. Orden de revisión, sin severidad RF calculada.");
        respuesta.put("incompleto", r.isCapped()); respuesta.put("avisos", avisos);
        respuesta.put("celdas", celdas); respuesta.put("objetivo", objetivo); respuesta.put("vecinos", vecinos);
        respuesta.put("filtros", filtros.describir(r.getHallazgos())); respuesta.put("totalHallazgos", r.getHallazgos().size());
        respuesta.put("totalFiltrados", visibles.size()); respuesta.put("pagina", s.getPagina()); respuesta.put("totalPaginas", paginas);
        respuesta.put("hallazgos", new ArrayList<>(visibles.subList(desde, Math.min(desde + s.getTamanoPagina(), visibles.size()))));
        return respuesta;
    }
    private static int prioridad(String tipo) {
        if ("PCI_COLLISION".equals(tipo)) return 0;
        if ("PCI_MOD3".equals(tipo)) return 1;
        if ("RSI_COLLISION".equals(tipo)) return 2;
        return 3;
    }
}
