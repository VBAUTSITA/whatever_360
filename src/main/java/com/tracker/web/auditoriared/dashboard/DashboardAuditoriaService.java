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
        visibles.sort(Comparator.comparingDouble(HallazgoAuditoriaDTO::getDistanciaMetros)
                .thenComparing(HallazgoAuditoriaDTO::getTipo).thenComparing(h -> h.getCeldaA().getNombre())
                .thenComparing(h -> h.getCeldaB().getNombre()));
        int paginas = Math.max(1, (visibles.size() + s.getTamanoPagina() - 1) / s.getTamanoPagina());
        if (s.getPagina() > paginas) throw new IllegalArgumentException("La pagina solicitada no existe");
        int desde = (s.getPagina() - 1) * s.getTamanoPagina();
        List<String> avisos = new ArrayList<>(r.getAvisos());
        avisos.add("Los indicadores describen reglas PCI/RSI y geometria estimada; no son mediciones de interferencia RF.");
        if (r.isCapped()) avisos.add("Resultado incompleto: se alcanzo el limite de hallazgos. Reduzca el area antes de decidir.");
        int invalidas = r.getResumen().stream().mapToInt(ResumenTecnologia::getDescartadasSinDatos).sum();
        if (invalidas > 0) avisos.add(invalidas + " filas fueron excluidas por datos incompletos o invalidos.");
        int duplicadas = r.getResumen().stream().mapToInt(ResumenTecnologia::getDuplicadas).sum();
        if (duplicadas > 0) avisos.add(duplicadas + " filas duplicadas por nombre fueron descartadas; se conserva la primera fila valida leida.");
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
            avisos.add("Vecinas del mismo carrier/banda dentro del contexto cargado (area y margen); las estimaciones no cubren toda la red.");
        }
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("version", 1); respuesta.put("alcance", r.getAlcance()); respuesta.put("resumen", r.getResumen());
        respuesta.put("generadoEn", r.getGeneradoEn()); respuesta.put("desdeCache", r.isDesdeCache());
        respuesta.put("incompleto", r.isCapped()); respuesta.put("avisos", avisos);
        respuesta.put("celdas", celdas); respuesta.put("objetivo", objetivo); respuesta.put("vecinos", vecinos);
        respuesta.put("filtros", filtros.describir(r.getHallazgos())); respuesta.put("totalHallazgos", r.getHallazgos().size());
        respuesta.put("totalFiltrados", visibles.size()); respuesta.put("pagina", s.getPagina()); respuesta.put("totalPaginas", paginas);
        respuesta.put("hallazgos", new ArrayList<>(visibles.subList(desde, Math.min(desde + s.getTamanoPagina(), visibles.size()))));
        return respuesta;
    }
}
