package com.tracker.web.auditoriared.dashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.tracker.web.auditoriared.HallazgoAuditoriaDTO;

@Component
public final class CatalogoFiltrosDashboard {
    private final Map<String, FiltroDashboard> filtros = new LinkedHashMap<>();
    public CatalogoFiltrosDashboard(List<FiltroDashboard> registrados) {
        for (FiltroDashboard f : registrados) {
            if (filtros.put(f.id(), f) != null) throw new IllegalStateException("Filtro repetido: " + f.id());
        }
    }
    public void validar(Map<String, List<String>> valores) {
        if (valores == null || valores.size() > filtros.size()) throw new IllegalArgumentException("Filtros invalidos");
        valores.forEach((id, lista) -> {
            FiltroDashboard f = filtros.get(id);
            if (f == null) throw new IllegalArgumentException("Filtro no reconocido: " + id);
            f.validar(lista);
        });
    }
    public List<HallazgoAuditoriaDTO> aplicar(List<HallazgoAuditoriaDTO> universo, Map<String, List<String>> valores) {
        validar(valores);
        List<HallazgoAuditoriaDTO> salida = new ArrayList<>();
        for (HallazgoAuditoriaDTO h : universo) {
            boolean acepta = true;
            for (Map.Entry<String, List<String>> e : valores.entrySet()) {
                if (!filtros.get(e.getKey()).acepta(h, e.getValue())) { acepta = false; break; }
            }
            if (acepta) salida.add(h);
        }
        return salida;
    }
    public List<Map<String, Object>> describir(List<HallazgoAuditoriaDTO> universo) {
        List<Map<String, Object>> salida = new ArrayList<>();
        for (FiltroDashboard f : filtros.values()) salida.add(f.describir(universo));
        return salida;
    }
}
