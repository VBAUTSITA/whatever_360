package com.tracker.web.sectores.sitesnuevos.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tracker.web.sectores.sitesnuevos.dto.ResumenMatriz;
import com.tracker.web.sectores.sitesnuevos.service.SectoresSitesService;

import java.util.*;


@Controller
@RequestMapping("/sectores-sites-nuevos")
public class SectoresSitesController {
private final SectoresSitesService service;


public SectoresSitesController(SectoresSitesService service) {
this.service = service;
}


@GetMapping("/matriz")
public String matriz(Model model) {
    // Datos base
    List<ResumenMatriz> resumen = service.resumenMatriz();
    List<String> tipos = service.tiposSitio();
    Map<String, Integer> sectoresRealRaw  = service.sectoresRealesPorPrioridad();     // # Sectores (oficial)
    Map<String, Integer> sectoresAsignRaw = service.sectoresAsignadosPorPrioridad();  // NUEVO: sectores asignados

    // Fila fija (canónica) y orden
    List<String> prioridades = Arrays.asList(
        "1-Critical","2-Severe","3-High","4-Moderate",
        "5-Low","6-Very Low","7-Not Loaded","8-Cobertura"
    );

    // Normalizador y matcher (para reconciliar etiquetas diferentes, ej. "1 - Critical")
    java.util.function.Function<String,String> norm = new java.util.function.Function<String, String>() {
        @Override public String apply(String s) {
            if (s == null) return "";
            return s.trim().toLowerCase().replaceAll("\\s*-\\s*", "-");
        }
    };
    java.util.function.BiFunction<List<String>,String,String> matchKey =
        new java.util.function.BiFunction<List<String>, String, String>() {
            @Override public String apply(List<String> options, String raw) {
                String n = norm.apply(raw);
                for (String opt : options) {
                    if (norm.apply(opt).equals(n)) return opt;
                }
                return null;
            }
        };

    // Limpieza de tipos (evita problemas por espacios)
    for (int i = 0; i < tipos.size(); i++) {
        String t = tipos.get(i);
        tipos.set(i, t == null ? "" : t.trim());
    }

    // Prefill de la matriz p×t con 0
    Map<String, Map<String, Integer>> matriz = new LinkedHashMap<String, Map<String, Integer>>();
    for (String p : prioridades) {
        Map<String, Integer> fila = new LinkedHashMap<String, Integer>();
        for (String t : tipos) fila.put(t, Integer.valueOf(0));
        matriz.put(p, fila);
    }

    // Volcar datos reales a claves canónicas
    for (ResumenMatriz r : resumen) {
        String pKey = matchKey.apply(prioridades, r.getPrioridadSector());
        if (pKey == null) continue; // prioridad no listada
        String rTipo = (r.getTipoSitio() == null ? "" : r.getTipoSitio().trim());
        String tKey = null;
        for (String t : tipos) { if (t.equalsIgnoreCase(rTipo)) { tKey = t; break; } }
        if (tKey == null) continue; // tipo no está en cabecera → ignora

        Map<String, Integer> fila = matriz.get(pKey);
        Integer prev = fila.get(tKey);
        fila.put(tKey, (prev == null ? r.getCantidadSites() : prev + r.getCantidadSites()));
    }

    // Canonicalizar mapas de #Sectores y Asignados para que las claves coincidan con 'prioridades'
    Map<String,Integer> sectoresReal = new LinkedHashMap<String,Integer>();
    Map<String,Integer> sectoresAsign = new LinkedHashMap<String,Integer>();
    for (String p : prioridades) {
        Integer total = Integer.valueOf(0);
        if (sectoresRealRaw != null) {
            for (Map.Entry<String,Integer> e : sectoresRealRaw.entrySet()) {
                if (norm.apply(e.getKey()).equals(norm.apply(p))) { total = e.getValue(); break; }
            }
        }
        sectoresReal.put(p, total);

        Integer asig = Integer.valueOf(0);
        if (sectoresAsignRaw != null) {
            for (Map.Entry<String,Integer> e : sectoresAsignRaw.entrySet()) {
                if (norm.apply(e.getKey()).equals(norm.apply(p))) { asig = e.getValue(); break; }
            }
        }
        sectoresAsign.put(p, asig);
    }

    // NUEVO: calcular “Sectores No asignados” = total - asignados (nunca < 0)
    Map<String,Integer> sectoresNoAsign = new LinkedHashMap<String,Integer>();
    for (String p : prioridades) {
        int total = sectoresReal.containsKey(p)  ? sectoresReal.get(p)  : 0;
        int asig  = sectoresAsign.containsKey(p) ? sectoresAsign.get(p) : 0;
        int noAsig = total - asig;
        if (noAsig < 0) noAsig = 0;
        sectoresNoAsign.put(p, Integer.valueOf(noAsig));
    }

    // (Opcional) ordenar tipos para cabecera
    java.util.Collections.sort(tipos);

    // Modelo
    model.addAttribute("prioridades", prioridades);
    model.addAttribute("tipos", tipos);
    model.addAttribute("sectoresReal", sectoresReal);
    model.addAttribute("sectoresNoAsignados", sectoresNoAsign); // <-- NUEVO
    model.addAttribute("sectoresAsignados", sectoresAsign); // <-- NUEVO
    model.addAttribute("matriz", matriz);

    return "sectores_sites/matriz";
}





@GetMapping("/detalle")
public String detalle(@RequestParam("prioridad") String prioridad,
@RequestParam("tipoSitio") String tipoSitio,
Model model) {
model.addAttribute("prioridad", prioridad);
model.addAttribute("tipoSitio", tipoSitio);
model.addAttribute("detalle", service.detalle(prioridad, tipoSitio));
return "sectores_sites/detalle";
}


@GetMapping("/sectores")
public String detalleSectores(@RequestParam("prioridad") String prioridad, Model model) {
model.addAttribute("prioridad", prioridad);
model.addAttribute("sectores", service.detalleSectoresReales(prioridad));
return "sectores_sites/sectores";
}


@GetMapping("/no-asignados")
public String detalleNoAsignados(@RequestParam("prioridad") String prioridad, Model model) {
    model.addAttribute("prioridad", prioridad);
    model.addAttribute("sectores", service.detalleSectoresNoAsignados(prioridad));
    return "sectores_sites/sectores"; // reutilizamos la misma vista de lista simple
}

@GetMapping("/asignados")
public String detalleAsignados(@RequestParam("prioridad") String prioridad, org.springframework.ui.Model model) {
    model.addAttribute("prioridad", prioridad);
    model.addAttribute("sectores", service.detalleSectoresAsignados(prioridad));
    return "sectores_sites/sectores"; // reutiliza la misma vista de lista
}


}