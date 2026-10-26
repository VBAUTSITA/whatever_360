package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.tracker.web.auditoriared.AccesoJefaturaAuditoria;

/**
 * Celdas de UN sitio de /recursos/asignar, con el PCI que trae la pantalla y no el de la BD.
 * Lee las columnas con su propio SQL y no con RecursosRepository: asi no depende del modelo de
 * recursos, que cambia de nombres entre copias (freqband / freqBandText). La politica de acceso
 * SI es la de recursos: solo los registros de la jefatura del usuario, igual que en /asignar.
 */
@Component
public class LectorSitioPlanificado {

    static final int MAX_CELDAS = 30;
    static final String SQL = "SELECT ID, SITE_NAME, CELL_NAME, TECNOLOGIA, CARRIER, FREQ_BAND_TEXT, TXRX_MODE, "
            + "LATITUD, LONGITUD, AZIMUTH, JEFATURA FROM RECURSOS_FORMULARIO WHERE ID IN (";

    private final JdbcTemplate jdbc;
    private final AccesoJefaturaAuditoria jefaturas;

    public LectorSitioPlanificado(JdbcTemplate jdbc, AccesoJefaturaAuditoria jefaturas) {
        this.jdbc = jdbc;
        this.jefaturas = jefaturas;
    }

    /** Las celdas 3G (u otra tecnologia) se omiten con un aviso: auditarRed solo corre en 4G y 5G. */
    public List<CeldaAuditoriaDTO> leer(List<Long> ids, List<Integer> pcis, List<String> avisos) {
        if (ids == null || pcis == null || ids.isEmpty() || ids.size() != pcis.size()) {
            throw new IllegalArgumentException("Tiene que llegar un PCI por cada id");
        }
        if (ids.size() > MAX_CELDAS) throw new IllegalArgumentException("Maximo " + MAX_CELDAS + " celdas por sitio");
        if (new HashSet<>(ids).size() != ids.size()) throw new IllegalArgumentException("Hay ids repetidos");
        // Antes de leer: sin jefatura declarada (ni ROLE_ADMIN) lanza AccessDeniedException.
        String jefaturaUsuario = jefaturas.jefaturaActual();

        StringBuilder sql = new StringBuilder(SQL);
        for (int i = 0; i < ids.size(); i++) sql.append(i == 0 ? "?" : ",?");
        Map<Long, Map<String, Object>> porId = new HashMap<>();
        for (Map<String, Object> fila : jdbc.query(sql.append(')').toString(), ids.toArray(), new ColumnMapRowMapper())) {
            porId.put(((Number) fila.get("ID")).longValue(), fila);
        }

        String sitio = null;
        Set<String> nombres = new HashSet<>();
        List<CeldaAuditoriaDTO> celdas = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Map<String, Object> f = porId.get(ids.get(i));
            if (f == null) throw new IllegalArgumentException("No existe el registro " + ids.get(i));
            if (!jefaturas.coincide(texto(f, "JEFATURA"), jefaturaUsuario)) {
                throw new AccessDeniedException("El registro " + ids.get(i) + " es de otra jefatura");
            }
            String nombreSitio = texto(f, "SITE_NAME");
            if (sitio == null) sitio = nombreSitio;
            if (nombreSitio == null || !nombreSitio.equalsIgnoreCase(sitio)) {
                throw new IllegalArgumentException("Un sitio por vez: llegaron " + sitio + " y " + nombreSitio);
            }
            String nombre = texto(f, "CELL_NAME");
            if (nombre == null || !nombres.add(nombre.toUpperCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Celda sin nombre o repetida en el sitio " + sitio + ": " + nombre);
            }
            String tecnologia = tecnologia(texto(f, "TECNOLOGIA"));
            if (tecnologia == null) {
                avisos.add(nombre + ": la tecnologia '" + texto(f, "TECNOLOGIA") + "' todavia no se audita (solo 4G y 5G).");
                continue;
            }
            int pci = pcis.get(i);
            int maximo = "5G".equals(tecnologia) ? 1007 : 503;
            if (pci < 0 || pci > maximo) {
                throw new IllegalArgumentException(nombre + ": el PCI " + pci + " esta fuera de 0-" + maximo + " (" + tecnologia + ")");
            }
            Double lat = AuditoriaRedRepository.numero(f.get("LATITUD"));
            Double lon = AuditoriaRedRepository.numero(f.get("LONGITUD"));
            Double azimuth = AuditoriaRedRepository.numero(f.get("AZIMUTH"));
            if (lat == null || lon == null || azimuth == null) {
                throw new IllegalArgumentException(nombre + ": faltan latitud, longitud o azimuth");
            }
            // TA en cero: como en el paso 4 de la planificacion, el motor le da el radio promedio de sus 30 vecinas.
            CeldaAuditoriaDTO celda = new CeldaAuditoriaDTO(nombre, tecnologia, texto(f, "CARRIER"),
                    texto(f, "FREQ_BAND_TEXT"), lat, lon, azimuth, pci, texto(f, "TXRX_MODE"), new double[8]);
            celda.setPlanificada(true);
            celdas.add(celda);
        }
        if (celdas.isEmpty()) {
            throw new IllegalArgumentException("El sitio " + sitio + " no tiene celdas 4G/5G: la auditoria 3G todavia no existe");
        }
        return celdas;
    }

    /** Mismo criterio que normalizarTecnologia de PciRsiAsignacionServiceImpl ("LTE 4G", "5G NR"); lo ambiguo, no. */
    static String tecnologia(String cruda) {
        String t = cruda == null ? "" : cruda.trim().toUpperCase(Locale.ROOT);
        boolean es5g = t.contains("5G") || t.contains("NR");
        boolean es4g = t.contains("4G") || t.contains("LTE");
        return es5g == es4g ? null : es5g ? "5G" : "4G";
    }

    private static String texto(Map<String, Object> fila, String columna) {
        Object v = fila.get(columna);
        String s = v == null ? null : String.valueOf(v).trim();
        return s == null || s.isEmpty() ? null : s;
    }
}
