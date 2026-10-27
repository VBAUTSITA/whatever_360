package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;

public final class ResultadoAuditoria {
    private final Alcance alcance;
    private final List<ResumenTecnologia> resumen;
    private final List<CeldaAuditoriaDTO> celdas;
    private final List<HallazgoAuditoriaDTO> hallazgos;
    private final long duracionMs;
    private final String generadoEn;
    private final boolean desdeCache;
    private final List<String> avisos;
    private final List<CeldaAuditoriaDTO> contexto;
    private long generadoEnEpochMs;
    private long edadMs;

    public ResultadoAuditoria(Alcance alcance, List<ResumenTecnologia> resumen,
            List<CeldaAuditoriaDTO> celdas, List<HallazgoAuditoriaDTO> hallazgos,
            long duracionMs, String generadoEn, boolean desdeCache, List<String> avisos) {
        this(alcance, resumen, celdas, hallazgos, duracionMs, generadoEn, desdeCache, avisos, celdas);
    }

    public ResultadoAuditoria(Alcance alcance, List<ResumenTecnologia> resumen,
            List<CeldaAuditoriaDTO> celdas, List<HallazgoAuditoriaDTO> hallazgos,
            long duracionMs, String generadoEn, boolean desdeCache, List<String> avisos,
            List<CeldaAuditoriaDTO> contexto) {
        this.alcance = alcance;
        this.resumen = Collections.unmodifiableList(new ArrayList<>(resumen));
        this.celdas = Collections.unmodifiableList(new ArrayList<>(celdas));
        this.hallazgos = Collections.unmodifiableList(new ArrayList<>(hallazgos));
        this.duracionMs = duracionMs;
        this.generadoEn = generadoEn;
        this.desdeCache = desdeCache;
        this.avisos = Collections.unmodifiableList(new ArrayList<>(avisos));
        this.contexto = Collections.unmodifiableList(new ArrayList<>(contexto));
    }

    ResultadoAuditoria comoDesdeCache() {
        return copia(true);
    }

    /** Snapshot independiente: una respuesta nunca puede contaminar entradas posteriores de cache. */
    ResultadoAuditoria copia(boolean cache) {
        Map<CeldaAuditoriaDTO, CeldaAuditoriaDTO> copias = new IdentityHashMap<>();
        List<CeldaAuditoriaDTO> nuevoContexto = new ArrayList<>(), nuevasCeldas = new ArrayList<>();
        for (CeldaAuditoriaDTO c : contexto) {
            copias.put(c, c.copia()); nuevoContexto.add(copias.get(c));
        }
        for (CeldaAuditoriaDTO c : celdas) {
            if (!copias.containsKey(c)) copias.put(c, c.copia());
            nuevasCeldas.add(copias.get(c));
        }
        List<HallazgoAuditoriaDTO> nuevosHallazgos = new ArrayList<>();
        for (HallazgoAuditoriaDTO h : hallazgos) {
            if (!copias.containsKey(h.getCeldaA())) copias.put(h.getCeldaA(), h.getCeldaA().copia());
            if (!copias.containsKey(h.getCeldaB())) copias.put(h.getCeldaB(), h.getCeldaB().copia());
            nuevosHallazgos.add(new HallazgoAuditoriaDTO(h.getTipo(), h.getEtiqueta(), h.getValor(),
                    h.getDistanciaMetros(), h.getMotivo(), copias.get(h.getCeldaA()), copias.get(h.getCeldaB())));
        }
        List<ResumenTecnologia> nuevoResumen = new ArrayList<>();
        for (ResumenTecnologia r : resumen) nuevoResumen.add(r.copia());
        ResultadoAuditoria r = new ResultadoAuditoria(alcance, nuevoResumen, nuevasCeldas, nuevosHallazgos,
                duracionMs, generadoEn, cache, avisos, nuevoContexto);
        r.marcarTiempo(generadoEnEpochMs, edadMs);
        return r;
    }

    /** Contexto completo para inspeccionar vecinos; nunca se envia entero por JSON. */
    @JsonIgnore public List<CeldaAuditoriaDTO> getContexto() { return contexto; }

    public Alcance getAlcance() { return alcance; }
    public List<ResumenTecnologia> getResumen() { return resumen; }
    /** Celdas del area, mas las del margen que participan en algun hallazgo. */
    public List<CeldaAuditoriaDTO> getCeldas() { return celdas; }
    public List<HallazgoAuditoriaDTO> getHallazgos() { return hallazgos; }
    public long getDuracionMs() { return duracionMs; }
    public String getGeneradoEn() { return generadoEn; }
    public long getGeneradoEnEpochMs() { return generadoEnEpochMs; }
    public long getEdadSegundos() { return edadMs / 1000; }
    public String getZonaHoraria() { return "America/Lima"; }
    void marcarTiempo(long generadoMs, long edadMs) { this.generadoEnEpochMs = generadoMs; this.edadMs = edadMs; }
    public boolean isDesdeCache() { return desdeCache; }
    /** Lo que se dejo fuera y el usuario tiene que saber (p. ej. celdas 3G de un sitio). */
    public List<String> getAvisos() { return avisos; }

    public boolean isCapped() {
        for (ResumenTecnologia r : resumen) {
            if (r.isCapped()) return true;
        }
        return false;
    }

    public List<String> getCappedTipos() {
        List<String> tipos = new ArrayList<>();
        for (ResumenTecnologia r : resumen) tipos.addAll(r.getCappedTipos());
        return tipos;
    }
}
