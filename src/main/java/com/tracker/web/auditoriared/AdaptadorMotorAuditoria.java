package com.tracker.web.auditoriared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Llama a PCIPlanner.auditarRed de los motores JS de produccion. Ninguna regla de PCI/RSI vive en Java. */
@Component
public class AdaptadorMotorAuditoria implements MotorAuditoria {

    /** Propio del modulo: solo usa funciones exportadas por el motor (PoolCarrier, DEFAULTS). */
    static final String RUTA_GEOMETRIA = "scripts/auditoriared/geometria_sectores.js";

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Script> scripts = new LinkedHashMap<>();
    private final Script scriptGeometria = Script.cargar(RUTA_GEOMETRIA);
    private final Map<String, ScriptEngine> motores = new HashMap<>();

    public AdaptadorMotorAuditoria(
            @Value("${auditoriared.motor-4g:scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js}") String ruta4g,
            @Value("${auditoriared.motor-5g:scripts/auditoriared/old_planner_5g.js}") String ruta5g) {
        scripts.put("4G", Script.cargar(ruta4g));
        scripts.put("5G", Script.cargar(ruta5g));
    }

    @Override
    public String version(String tecnologia) {
        Script s = scripts.get(tecnologia);
        return s == null ? "" : s.version();
    }

    @Override
    public synchronized ResultadoMotor auditar(String tecnologia, List<CeldaAuditoriaDTO> celdas,
            int radioReusoMetros, int tope) {
        if (celdas.isEmpty()) {
            return new ResultadoMotor(Collections.<ConflictoMotor>emptyList(), false,
                    Collections.<String>emptyList());
        }
        try {
            ScriptEngine engine = motor(tecnologia);
            // Los datos viajan como texto en una variable y se parsean dentro del motor:
            // nunca se concatenan al codigo fuente (evita inyeccion y el problema de U+2028).
            engine.put("__auditoriaFilas", mapper.writeValueAsString(porGrupo(tecnologia, celdas)));
            Object bruto = engine.eval("JSON.stringify(PCIPlanner.auditarRed(JSON.parse(__auditoriaFilas), "
                    + "{radioReuso: " + radioReusoMetros + ", cap: " + tope + "}))");
            if (!(bruto instanceof String)) {
                throw new IllegalStateException("auditarRed no devolvio JSON");
            }
            return leer(tecnologia, (String) bruto);
        } catch (ScriptException | IOException e) {
            throw new IllegalStateException("Fallo auditarRed del motor " + version(tecnologia), e);
        }
    }

    @Override
    public synchronized Map<String, GeometriaSector> geometria(String tecnologia,
            List<CeldaAuditoriaDTO> celdas, Collection<String> nombres) {
        Map<String, GeometriaSector> salida = new HashMap<>();
        if (celdas.isEmpty() || nombres.isEmpty()) return salida;
        try {
            ScriptEngine engine = motor(tecnologia);
            engine.put("__auditoriaFilas", mapper.writeValueAsString(porGrupo(tecnologia, celdas)));
            engine.put("__auditoriaNombres", mapper.writeValueAsString(nombres));
            Object bruto = engine.eval("JSON.stringify(AuditoriaGeometria.calcular("
                    + "JSON.parse(__auditoriaFilas), JSON.parse(__auditoriaNombres)))");
            if (!(bruto instanceof String)) {
                throw new IllegalStateException("La geometria de sectores no devolvio JSON");
            }
            Iterator<Map.Entry<String, JsonNode>> it = mapper.readTree((String) bruto).fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                JsonNode g = e.getValue();
                Double az = nullable(g, "az"), haz = nullable(g, "hbeam"), radio = nullable(g, "radio");
                if (az == null || haz == null || haz <= 0 || haz > 360 || radio == null || radio <= 0) {
                    throw new IllegalStateException("El motor devolvio geometria incompleta para " + e.getKey());
                }
                salida.put(e.getKey().trim().toUpperCase(Locale.ROOT), new GeometriaSector(
                        az, haz, radio, g.path("sinTa").asBoolean(false)));
            }
            return salida;
        } catch (ScriptException | IOException e) {
            throw new IllegalStateException("Fallo la geometria de sectores con el motor " + version(tecnologia), e);
        }
    }

    private Map<String, List<Map<String, Object>>> porGrupo(String tecnologia, List<CeldaAuditoriaDTO> celdas) {
        Map<String, List<Map<String, Object>>> porGrupo = new LinkedHashMap<>();
        for (CeldaAuditoriaDTO celda : celdas) {
            if (!tecnologia.equals(celda.getTecnologia())) {
                throw new IllegalArgumentException("Celda de otra tecnologia en el motor " + tecnologia);
            }
            String grupo = claveGrupo(celda);
            List<Map<String, Object>> filas = porGrupo.get(grupo);
            if (filas == null) {
                filas = new ArrayList<>();
                porGrupo.put(grupo, filas);
            }
            filas.add(celda.paraMotor());
        }
        return porGrupo;
    }

    /** Mismo criterio que la clave de pool del motor (claves_carrier = Carrier + Freqband). */
    static String claveGrupo(CeldaAuditoriaDTO celda) {
        String carrier = celda.getCarrier() == null ? "" : celda.getCarrier().trim().toUpperCase(Locale.ROOT);
        String banda = celda.getBanda() == null ? "" : celda.getBanda().trim().toUpperCase(Locale.ROOT);
        return carrier + "|" + banda;
    }

    private ResultadoMotor leer(String tecnologia, String json) throws IOException {
        JsonNode raiz = mapper.readTree(json);
        if (!raiz.path("conflicts").isArray()) {
            throw new IllegalStateException("auditarRed no devolvio la lista conflicts");
        }
        List<ConflictoMotor> conflictos = new ArrayList<>();
        for (JsonNode nodo : raiz.path("conflicts")) {
            if (!nodo.path("type").isTextual() || nodo.path("type").asText().trim().isEmpty()
                    || !nodo.path("aName").isTextual() || !nodo.path("bName").isTextual()
                    || !nodo.path("dist").isNumber() || !Double.isFinite(nodo.path("dist").asDouble())
                    || nodo.path("dist").asDouble() < 0) {
                throw new IllegalStateException("El motor devolvio un conflicto incompleto o invalido");
            }
            conflictos.add(new ConflictoMotor(
                    nodo.path("type").asText(), nodo.path("aName").asText(),
                    nodo.path("bName").asText(), nodo.path("dist").asDouble(),
                    nodo.path("val").asText(),
                    nodo.path("motivo").isNull() || nodo.path("motivo").isMissingNode()
                            ? null : nodo.path("motivo").asText()));
        }
        boolean capped = raiz.path("capped").asBoolean(false);
        List<String> tipos = new ArrayList<>();
        JsonNode cappedTipos = raiz.path("cappedTipos");
        if (cappedTipos.isArray()) {
            for (JsonNode tipo : cappedTipos) tipos.add(tecnologia + ":" + tipo.asText());
        } else if (capped) {
            // El motor 5G solo informa un tope global, no por tipo.
            tipos.add(tecnologia + ":GLOBAL");
        }
        return new ResultadoMotor(conflictos, capped, tipos);
    }

    @Override
    public synchronized List<InteraccionVecinaDTO> vecinos(String tecnologia, List<CeldaAuditoriaDTO> contexto,
            String objetivo, int limite) {
        if (limite < 1 || limite > 200 || objetivo == null || objetivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique una celda objetivo y entre 1 y 200 vecinas");
        }
        List<InteraccionVecinaDTO> salida = new ArrayList<>();
        try {
            ScriptEngine engine = motor(tecnologia);
            engine.put("__auditoriaFilas", mapper.writeValueAsString(porGrupo(tecnologia, contexto)));
            engine.put("__auditoriaObjetivo", objetivo);
            engine.put("__auditoriaLimite", limite);
            Object bruto = engine.eval("JSON.stringify(AuditoriaGeometria.vecinos("
                    + "JSON.parse(__auditoriaFilas), __auditoriaObjetivo, __auditoriaLimite))");
            if (!(bruto instanceof String)) throw new IllegalStateException("La vecindad no devolvio JSON");
            JsonNode raiz = mapper.readTree((String) bruto);
            if (!raiz.isArray()) throw new IllegalStateException("La vecindad no devolvio una lista");
            Map<String, CeldaAuditoriaDTO> porNombre = new HashMap<>();
            for (CeldaAuditoriaDTO c : contexto) porNombre.put(c.getNombre().trim().toUpperCase(Locale.ROOT), c);
            for (JsonNode n : raiz) {
                CeldaAuditoriaDTO c = porNombre.get(n.path("nombre").asText().trim().toUpperCase(Locale.ROOT));
                if (c == null) throw new IllegalStateException("La vecindad incluyo una celda desconocida");
                c = c.copia();
                Double radio = nullable(n, "radioM"), azimuth = nullable(n, "azimuthEfectivo"), haz = nullable(n, "hbeam");
                if (radio != null && radio > 0 && azimuth != null && haz != null && haz > 0) {
                    c.aplicarGeometria(new GeometriaSector(azimuth, haz, radio, n.path("sinTaVecina").asBoolean()));
                }
                salida.add(new InteraccionVecinaDTO(c, n.path("distanciaMetros").asDouble(),
                        nullable(n, "areaInterseccionM2"), nullable(n, "interseccionNormalizadaM"),
                        n.path("mismoPci").asBoolean(), n.path("mismoMod3").asBoolean(),
                        n.path("mismoRsi").asBoolean(), n.path("sinTaObjetivo").asBoolean(),
                        n.path("sinTaVecina").asBoolean()));
            }
            return salida;
        } catch (ScriptException | IOException e) {
            throw new IllegalStateException("No se pudo inspeccionar la vecindad con el motor " + version(tecnologia), e);
        }
    }

    private static Double nullable(JsonNode nodo, String campo) {
        return nodo.path(campo).isNumber() && Double.isFinite(nodo.path(campo).asDouble())
                ? nodo.path(campo).asDouble() : null;
    }

    private ScriptEngine motor(String tecnologia) throws ScriptException {
        ScriptEngine engine = motores.get(tecnologia);
        if (engine != null) return engine;
        Script script = scripts.get(tecnologia);
        if (script == null) throw new IllegalArgumentException("Tecnologia no soportada: " + tecnologia);
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        if (engine == null) {
            throw new IllegalStateException("El motor JS 'nashorn' no esta disponible en este JDK. "
                    + "La auditoria requiere Java 8 (Nashorn se retiro en JDK 15).");
        }
        engine.eval(script.fuente);
        engine.eval(scriptGeometria.fuente);
        motores.put(tecnologia, engine);
        return engine;
    }

    private static final class Script {
        final String ruta;
        final String fuente;
        final String sha256;

        private Script(String ruta, String fuente, String sha256) {
            this.ruta = ruta;
            this.fuente = fuente;
            this.sha256 = sha256;
        }

        String version() {
            String archivo = ruta.substring(ruta.lastIndexOf('/') + 1);
            return archivo + " (" + sha256.substring(0, 8) + ")";
        }

        static Script cargar(String ruta) {
            try (InputStream in = AdaptadorMotorAuditoria.class.getClassLoader().getResourceAsStream(ruta)) {
                if (in == null) throw new IllegalStateException("No se encontro el motor " + ruta);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                byte[] bloque = new byte[8192];
                int n;
                while ((n = in.read(bloque)) >= 0) bytes.write(bloque, 0, n);
                byte[] contenido = bytes.toByteArray();
                return new Script(ruta, new String(contenido, StandardCharsets.UTF_8), sha256(contenido));
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer el motor " + ruta, e);
            }
        }

        private static String sha256(byte[] contenido) {
            try {
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(contenido);
                StringBuilder sb = new StringBuilder();
                for (byte b : hash) sb.append(String.format("%02X", b));
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
