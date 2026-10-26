package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResultadoAuditoria {
    private final Alcance alcance;
    private final List<ResumenTecnologia> resumen;
    private final List<CeldaAuditoriaDTO> celdas;
    private final List<HallazgoAuditoriaDTO> hallazgos;
    private final long duracionMs;
    private final String generadoEn;
    private final boolean desdeCache;
    private final List<String> avisos;

    public ResultadoAuditoria(Alcance alcance, List<ResumenTecnologia> resumen,
            List<CeldaAuditoriaDTO> celdas, List<HallazgoAuditoriaDTO> hallazgos,
            long duracionMs, String generadoEn, boolean desdeCache, List<String> avisos) {
        this.alcance = alcance;
        this.resumen = Collections.unmodifiableList(new ArrayList<>(resumen));
        this.celdas = Collections.unmodifiableList(new ArrayList<>(celdas));
        this.hallazgos = Collections.unmodifiableList(new ArrayList<>(hallazgos));
        this.duracionMs = duracionMs;
        this.generadoEn = generadoEn;
        this.desdeCache = desdeCache;
        this.avisos = Collections.unmodifiableList(new ArrayList<>(avisos));
    }

    ResultadoAuditoria comoDesdeCache() {
        return new ResultadoAuditoria(alcance, resumen, celdas, hallazgos, duracionMs, generadoEn, true, avisos);
    }

    public Alcance getAlcance() { return alcance; }
    public List<ResumenTecnologia> getResumen() { return resumen; }
    /** Celdas del area, mas las del margen que participan en algun hallazgo. */
    public List<CeldaAuditoriaDTO> getCeldas() { return celdas; }
    public List<HallazgoAuditoriaDTO> getHallazgos() { return hallazgos; }
    public long getDuracionMs() { return duracionMs; }
    public String getGeneradoEn() { return generadoEn; }
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
