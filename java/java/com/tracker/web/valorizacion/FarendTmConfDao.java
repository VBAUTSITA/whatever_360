package com.tracker.web.valorizacion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Nearest-FAREND lookup against the LIVE network inventory
 * (tm_conf_actual_celda_4g) — the same source and the same query pattern
 * the recursos module uses in RedExistenteServiceImpl:
 *
 *  - native SQL through the EntityManager (no JPA entity: the table is
 *    read-only reference data with ~90k rows nationwide),
 *  - a SQL bounding-box pre-filter so we never pull the national network
 *    just to find one neighbour,
 *  - lat/lon in that table are VARCHAR2, so the SQL casts with TO_NUMBER
 *    (Oracle) and the Java side re-parses defensively — a dirty row must
 *    be skipped, not crash the valorization (see the recursos module's
 *    notes on this exact table).
 *
 * The site label printed in the Excel ("LI0029_LOS_FLAMENCOS") is taken
 * from SECTOR_NAME. If the real convention needs trimming (e.g. dropping a
 * trailing sector suffix), adjust {@link #normalizarSite(String)} only.
 */
@Repository
public class FarendTmConfDao {

    /**
     * Search radii: first pass close (a farend is normally within a few
     * km), second pass wide before giving up. Same box math as the
     * recursos module: 1 degree of latitude ~ 111.32 km, and longitude
     * degrees shrink by cos(lat) away from the equator.
     */
    private static final double RADIO_CERCANO_KM = 30.0;
    private static final double RADIO_AMPLIO_KM = 150.0;
    private static final double KM_POR_GRADO_LAT = 111.32;

    /**
     * TO_NUMBER without a format mask: the thin ojdbc driver defaults to
     * '.' as the decimal separator, matching how these VARCHAR2 columns
     * already store data (e.g. "-77.0428") — same reasoning documented in
     * RedExistenteServiceImpl. Rows whose coords don't parse are excluded
     * by the BETWEEN itself on Oracle only if TO_NUMBER succeeds; anything
     * exotic is re-checked in Java anyway.
     */
    private static final String SQL_CANDIDATOS =
            "SELECT r.sector_name, r.latitud, r.longitud "
            + "FROM tm_conf_actual_celda_4g r "
            + "WHERE TO_NUMBER(r.latitud) BETWEEN :minLat AND :maxLat "
            + "  AND TO_NUMBER(r.longitud) BETWEEN :minLon AND :maxLon";

    /** Nearest site found: code + its coordinates (the BBU side). */
    public static class FarendCercano {
        private final String codigo;
        private final double latitud;
        private final double longitud;
        private final double distanciaKm;

        FarendCercano(String codigo, double latitud, double longitud, double distanciaKm) {
            this.codigo = codigo;
            this.latitud = latitud;
            this.longitud = longitud;
            this.distanciaKm = distanciaKm;
        }

        public String getCodigo() { return codigo; }
        public double getLatitud() { return latitud; }
        public double getLongitud() { return longitud; }
        public double getDistanciaKm() { return distanciaKm; }
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Nearest site to the PAP coordinates, or null when nothing is found
     * within {@link #RADIO_AMPLIO_KM} (or the coords are missing).
     */
    @Transactional(readOnly = true)
    public FarendCercano buscarMasCercano(Double lat, Double lon) {
        if (lat == null || lon == null) {
            return null;
        }
        FarendCercano cercano = buscarEnRadio(lat, lon, RADIO_CERCANO_KM);
        if (cercano == null) {
            cercano = buscarEnRadio(lat, lon, RADIO_AMPLIO_KM);
        }
        return cercano;
    }

    @SuppressWarnings("unchecked")
    private FarendCercano buscarEnRadio(double lat, double lon, double radioKm) {

        double margenLat = radioKm / KM_POR_GRADO_LAT;
        // cos(lat) correction so the box covers the same real km east-west;
        // the 0.05 floor is only defensive near the poles (irrelevant in Peru).
        double margenLon = radioKm
                / (KM_POR_GRADO_LAT * Math.max(0.05, Math.cos(Math.toRadians(lat))));

        Query q = entityManager.createNativeQuery(SQL_CANDIDATOS);
        q.setParameter("minLat", lat - margenLat);
        q.setParameter("maxLat", lat + margenLat);
        q.setParameter("minLon", lon - margenLon);
        q.setParameter("maxLon", lon + margenLon);

        List<Object[]> filas = q.getResultList();

        String mejorCodigo = null;
        double mejorLat = 0, mejorLon = 0;
        double mejorDist = Double.MAX_VALUE;

        for (Object[] fila : filas) {
            // Defensive re-parse: VARCHAR2 columns, dirty data skips the row.
            Double sLat = parseDouble(fila[1]);
            Double sLon = parseDouble(fila[2]);
            if (fila[0] == null || sLat == null || sLon == null) {
                continue;
            }
            double d = ValorizacionService.haversineKm(lat, lon, sLat.doubleValue(), sLon.doubleValue());
            if (d < mejorDist) {
                mejorDist = d;
                mejorCodigo = normalizarSite(String.valueOf(fila[0]));
                mejorLat = sLat.doubleValue();
                mejorLon = sLon.doubleValue();
            }
        }

        return (mejorCodigo == null) ? null
                : new FarendCercano(mejorCodigo, mejorLat, mejorLon, mejorDist);
    }

    /**
     * SECTOR_NAME -> site label printed in the Excel. Passthrough today;
     * if the real data carries a sector suffix (e.g. "..._1A"), strip it
     * here and nowhere else.
     */
    private static String normalizarSite(String sectorName) {
        return sectorName.trim();
    }

    private static Double parseDouble(Object raw) {
        if (raw == null) {
            return null;
        }
        try {
            return Double.parseDouble(String.valueOf(raw).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
