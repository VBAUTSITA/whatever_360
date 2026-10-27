package com.tracker.web.auditoriared.dashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import com.tracker.web.auditoriared.HallazgoAuditoriaDTO;

/** Reusable field filter. Only supported operators are exposed; no request value becomes SQL. */
public final class FiltroCampoDashboard implements FiltroDashboard {
    public enum Tipo { SELECCION, TEXTO, MAXIMO }
    private final String id;
    private final String etiqueta;
    private final Tipo tipo;
    private final Function<HallazgoAuditoriaDTO, List<String>> extractor;

    public FiltroCampoDashboard(String id, String etiqueta, Tipo tipo,
            Function<HallazgoAuditoriaDTO, List<String>> extractor) {
        if (id == null || !id.matches("[a-zA-Z][a-zA-Z0-9]*") || etiqueta == null || tipo == null || extractor == null) {
            throw new IllegalArgumentException("Definicion de filtro invalida");
        }
        this.id = id; this.etiqueta = etiqueta; this.tipo = tipo; this.extractor = extractor;
    }
    @Override public String id() { return id; }
    private static String normalizar(String s) { return s == null ? "" : s.trim().toUpperCase(Locale.ROOT); }

    @Override public void validar(List<String> valores) {
        if (valores == null || valores.size() > 50 || (tipo != Tipo.SELECCION && valores.size() > 1)) {
            throw new IllegalArgumentException("Valores invalidos para el filtro " + id);
        }
        for (String v : valores) {
            if (v == null || v.length() > 200 || v.trim().isEmpty()) {
                throw new IllegalArgumentException("Valor vacio o demasiado largo en " + id);
            }
            if (tipo == Tipo.MAXIMO) {
                try {
                    double n = Double.parseDouble(v);
                    if (!Double.isFinite(n) || n < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) { throw new IllegalArgumentException("El filtro " + id + " requiere un numero positivo o cero"); }
            }
        }
    }

    @Override public boolean acepta(HallazgoAuditoriaDTO h, List<String> valores) {
        if (valores.isEmpty()) return true;
        List<String> campos = extractor.apply(h);
        for (String campo : campos) {
            for (String esperado : valores) {
                if (tipo == Tipo.TEXTO && normalizar(campo).contains(normalizar(esperado))) return true;
                if (tipo == Tipo.SELECCION && normalizar(campo).equals(normalizar(esperado))) return true;
                if (tipo == Tipo.MAXIMO && campo != null
                        && Double.parseDouble(campo) <= Double.parseDouble(esperado)) return true;
            }
        }
        return false;
    }

    @Override public Map<String, Object> describir(List<HallazgoAuditoriaDTO> universo) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("id", id); d.put("etiqueta", etiqueta); d.put("tipo", tipo.name());
        TreeSet<String> opciones = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (tipo == Tipo.SELECCION) for (HallazgoAuditoriaDTO h : universo) {
            for (String valor : extractor.apply(h)) if (valor != null && !valor.trim().isEmpty()) opciones.add(valor.trim());
        }
        d.put("opciones", new ArrayList<>(opciones));
        return d;
    }
}
