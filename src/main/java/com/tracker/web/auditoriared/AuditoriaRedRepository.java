package com.tracker.web.auditoriared;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

/**
 * Lee la red existente con las mismas tablas, joins y columnas que
 * RedExistenteServiceImpl / RedExistenteServiceImpl5G de produccion (recursos_v5).
 */
@Repository
public class AuditoriaRedRepository {

    // Orden posicional comun a 4G y 5G: 1 nombre, 2 carrier, 3 pci, 4 banda, 5 txrx,
    // 6 latitud, 7 longitud, 8 azimuth, 9..16 TA.
    static final String SQL_4G =
            "SELECT r.cellname, r.carrier, r.phycellid, to_char(r.freqband) freq_band, r.txrxmode, "
            + "       r.latitud, r.longitud, r.azimuth, "
            + "       m.ta_0_234_mts, m.ta_234_546_mts, m.ta_546_1014_mts, m.ta_1014_1950_mts, "
            + "       m.ta_1950_3510_mts, m.ta_3510_6630_mts, m.ta_6630_14430_mts, m.ta_mayor_14430_mts "
            + "FROM tm_conf_actual_celda_4g r "
            + "LEFT JOIN PLAN_MAP_SECT_CRIT_4GWEEK m ON r.sector_name = m.sector_name "
            + "WHERE " + coordenadaSql("r.latitud") + " BETWEEN ? AND ? "
            + "  AND " + coordenadaSql("r.longitud") + " BETWEEN ? AND ?";

    static final String SQL_5G =
            "SELECT a.cellname, a.carrier, a.physicalcellid, a.frequencyband freq_band, a.txrxmode, "
            + "       a.latitud, a.longitud, a.azimuth, "
            + "       b.ta_0_117_mts_porc, b.ta_117_507_mts_porc, "
            + "       b.ta_507_975_mts_porc, b.ta_975_1775_mts_porc, "
            + "       b.ta_1775_3315_mts_porc, b.ta_3315_7215_mts_porc, "
            + "       b.ta_7215_15015_mts_porc, b.ta_mayor_a_15015_mts_porc "
            + "FROM tm_conf_actual_celda_5g a "
            + "LEFT JOIN vw_plani_recursos_5g_ta_diario b ON UPPER(a.cellname) = UPPER(b.cellname) "
            + "WHERE " + coordenadaSql("a.latitud") + " BETWEEN ? AND ? "
            + "  AND " + coordenadaSql("a.longitud") + " BETWEEN ? AND ?";

    // INSTR busca el texto literal: % y _ del usuario no actuan como comodines.
    static final String SQL_SITIOS_4G =
            "SELECT * FROM (SELECT MIN(r.cellname) nombre, r.latitud, r.longitud "
            + "FROM tm_conf_actual_celda_4g r "
            + "WHERE INSTR(UPPER(r.cellname), UPPER(?)) > 0 "
            + "  AND r.latitud IS NOT NULL AND r.longitud IS NOT NULL "
            + "GROUP BY r.latitud, r.longitud ORDER BY MIN(r.cellname)) WHERE ROWNUM <= ?";

    static final String SQL_SITIOS_5G =
            "SELECT * FROM (SELECT MIN(a.cellname) nombre, a.latitud, a.longitud "
            + "FROM tm_conf_actual_celda_5g a "
            + "WHERE INSTR(UPPER(a.cellname), UPPER(?)) > 0 "
            + "  AND a.latitud IS NOT NULL AND a.longitud IS NOT NULL "
            + "GROUP BY a.latitud, a.longitud ORDER BY MIN(a.cellname)) WHERE ROWNUM <= ?";

    private final JdbcTemplate jdbc;
    @Value("${auditoriared.max-filas-leidas:100000}")
    private int maxFilasLeidas = 100000;

    /** CASE short-circuits conversion: a dirty VARCHAR2 coordinate cannot abort the entire query. */
    private static String coordenadaSql(String columna) {
        String texto = "REPLACE(TRIM(CAST(" + columna + " AS VARCHAR2(4000))), ',', '.')";
        return "(CASE WHEN REGEXP_LIKE(" + texto + ", '^[+-]?[0-9]{1,3}([.][0-9]{1,15})?$') "
                + "THEN TO_NUMBER(" + texto + ", '999999999999999D999999999999999', "
                + "'NLS_NUMERIC_CHARACTERS=''.,''') END)";
    }

    public AuditoriaRedRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /** Resultado crudo de leer la caja de una tecnologia. */
    public static final class Lectura {
        private final List<CeldaAuditoriaDTO> celdas;
        private final int filasLeidas;
        private final int descartadasSinDatos;

        public Lectura(List<CeldaAuditoriaDTO> celdas, int filasLeidas, int descartadasSinDatos) {
            this.celdas = celdas;
            this.filasLeidas = filasLeidas;
            this.descartadasSinDatos = descartadasSinDatos;
        }

        public List<CeldaAuditoriaDTO> getCeldas() { return celdas; }
        public int getFilasLeidas() { return filasLeidas; }
        public int getDescartadasSinDatos() { return descartadasSinDatos; }
    }

    public Lectura leer(final String tecnologia, CajaGeografica caja) {
        String sql = "4G".equals(tecnologia) ? SQL_4G : "5G".equals(tecnologia) ? SQL_5G : null;
        if (sql == null) throw new IllegalArgumentException("Tecnologia no soportada: " + tecnologia);
        final List<CeldaAuditoriaDTO> celdas = new ArrayList<>();
        final int[] contadores = new int[2]; // [0] filas leidas, [1] descartadas
        jdbc.query(sql, new Object[] {caja.getMinLat(), caja.getMaxLat(), caja.getMinLon(), caja.getMaxLon()},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        contadores[0]++;
                        if (maxFilasLeidas < 1 || contadores[0] > maxFilasLeidas) {
                            throw new IllegalArgumentException("La lectura excede el limite de filas; reduzca el area auditada");
                        }
                        CeldaAuditoriaDTO celda = aCelda(tecnologia, rs);
                        if (celda == null) contadores[1]++;
                        else celdas.add(celda);
                    }
                });
        return new Lectura(celdas, contadores[0], contadores[1]);
    }

    public List<SitioDTO> buscarSitios(String texto, int limite) {
        Map<String, SitioDTO> porCoordenada = new LinkedHashMap<>();
        agregarSitios(SQL_SITIOS_4G, "4G", texto, limite, porCoordenada);
        agregarSitios(SQL_SITIOS_5G, "5G", texto, limite, porCoordenada);
        List<SitioDTO> sitios = new ArrayList<>(porCoordenada.values());
        return sitios.size() > limite ? new ArrayList<>(sitios.subList(0, limite)) : sitios;
    }

    private void agregarSitios(String sql, final String tecnologia, String texto, int limite,
            final Map<String, SitioDTO> destino) {
        jdbc.query(sql, new Object[] {texto, limite}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Double lat = numero(rs.getObject(2));
                Double lon = numero(rs.getObject(3));
                String nombre = rs.getString(1);
                if (lat == null || lon == null || Math.abs(lat) > 90 || Math.abs(lon) > 180
                        || nombre == null || nombre.trim().isEmpty()) return;
                String clave = String.format(Locale.ROOT, "%.5f|%.5f", lat, lon);
                if (!destino.containsKey(clave)) destino.put(clave, new SitioDTO(nombre, lat, lon, tecnologia));
            }
        });
    }

    /** Sin coordenadas, azimuth o PCI la celda no se puede auditar: se descarta en vez de inventar un 0. */
    static CeldaAuditoriaDTO aCelda(String tecnologia, ResultSet rs) throws SQLException {
        String nombre = rs.getString(1);
        Double lat = numero(rs.getObject(6));
        Double lon = numero(rs.getObject(7));
        Double azimuth = numero(rs.getObject(8));
        Double pci = numero(rs.getObject(3));
        if (nombre == null || nombre.trim().isEmpty() || lat == null || lon == null
                || azimuth == null || pci == null || Math.abs(lat) > 90 || Math.abs(lon) > 180
                || azimuth < 0 || azimuth > 360 || pci != Math.rint(pci)
                || pci < 0 || pci > ("5G".equals(tecnologia) ? 1007 : 503)) {
            return null;
        }
        double[] taCrudo = new double[8];
        for (int i = 0; i < 8; i++) {
            Double v = numero(rs.getObject(9 + i));
            taCrudo[i] = v == null ? 0.0 : v;
        }
        Object banda = rs.getObject(4);
        Object carrier = rs.getObject(2);
        Object txrx = rs.getObject(5);
        if (carrier == null || String.valueOf(carrier).trim().isEmpty()
                || banda == null || String.valueOf(banda).trim().isEmpty()) return null;
        return new CeldaAuditoriaDTO(nombre.trim(), tecnologia,
                carrier == null ? null : String.valueOf(carrier).trim(),
                banda == null ? null : String.valueOf(banda).trim(),
                lat, lon, azimuth, (int) Math.round(pci),
                txrx == null ? null : String.valueOf(txrx).trim(),
                normalizarTA(taCrudo));
    }

    /** Las columnas son VARCHAR2 y las de TA traen el % pegado ("39.82%"). */
    static Double numero(Object crudo) {
        if (crudo == null) return null;
        String texto = String.valueOf(crudo).trim();
        if (texto.endsWith("%")) texto = texto.substring(0, texto.length() - 1).trim();
        if (texto.indexOf(',') >= 0 && texto.indexOf('.') < 0) texto = texto.replace(',', '.');
        if (texto.isEmpty()) return null;
        try {
            double v = Double.parseDouble(texto);
            return Double.isFinite(v) ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * El motor espera 8 fracciones que suman ~1. Igual que produccion: si suman ~100 se dividen,
     * y si no cuadran se envian en cero para que el motor use su respaldo (promedio de vecinas).
     */
    static double[] normalizarTA(double[] crudo) {
        if (crudo == null || crudo.length != 8) return new double[8];
        double suma = 0;
        for (double v : crudo) {
            if (!Double.isFinite(v) || v < 0) return new double[8];
            suma += v;
        }
        double factor;
        if (suma >= 0.5 && suma <= 1.5) factor = 1.0;
        else if (suma >= 50 && suma <= 150) factor = 0.01;
        else return new double[8];
        double[] salida = new double[8];
        for (int i = 0; i < 8; i++) salida[i] = crudo[i] * factor;
        return salida;
    }
}
