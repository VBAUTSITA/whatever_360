package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Celdas con un mismo CLUSTER_NAME de la red. El area visible son esas celdas; el margen son las
 * celdas de cualquier cluster a menos de margenKm de alguna de ellas, para no perder los pares del borde.
 */
public final class ClusterRedAlcance implements Alcance {

    private static final double RADIO_TIERRA_KM = 6371.0088;

    private final String cluster;
    private final double margenKm;
    private final List<double[]> puntos;
    private final double latitud;
    private final double longitud;
    private final double radioKm;
    private final double minLat, maxLat, minLon, maxLon;

    public ClusterRedAlcance(String cluster, List<double[]> puntos, double margenKm) {
        if (cluster == null || cluster.trim().isEmpty() || cluster.trim().length() > 100) {
            throw new IllegalArgumentException("Indique un cluster de red valido");
        }
        if (puntos == null || puntos.isEmpty()) {
            throw new IllegalArgumentException("El cluster " + cluster.trim() + " no tiene celdas con coordenadas");
        }
        if (!Double.isFinite(margenKm) || margenKm < 0) throw new IllegalArgumentException("Margen del cluster invalido");
        this.cluster = cluster.trim();
        this.margenKm = margenKm;
        this.puntos = new ArrayList<>();
        double x = 0, y = 0, z = 0, aLat = 90, bLat = -90, aLon = 180, bLon = -180;
        for (double[] p : puntos) {
            if (p == null || p.length != 2 || !Double.isFinite(p[0]) || !Double.isFinite(p[1])
                    || Math.abs(p[0]) > 90 || Math.abs(p[1]) > 180) {
                throw new IllegalArgumentException("Coordenadas invalidas en el cluster " + this.cluster);
            }
            this.puntos.add(new double[] {p[0], p[1]});
            double f = Math.toRadians(p[0]), l = Math.toRadians(p[1]);
            x += Math.cos(f) * Math.cos(l); y += Math.cos(f) * Math.sin(l); z += Math.sin(f);
            aLat = Math.min(aLat, p[0]); bLat = Math.max(bLat, p[0]); aLon = Math.min(aLon, p[1]); bLon = Math.max(bLon, p[1]);
        }
        if (Math.sqrt(x * x + y * y + z * z) < 1e-9) {
            throw new IllegalArgumentException("Las celdas del cluster " + this.cluster + " no definen un centro");
        }
        // Centro esferico y radio que cubre todas las celdas: solo para encuadrar el mapa.
        this.latitud = Math.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y)));
        this.longitud = Math.toDegrees(Math.atan2(y, x));
        double r = 0;
        for (double[] p : this.puntos) r = Math.max(r, ClusterAlcance.distanciaKm(latitud, longitud, p[0], p[1]));
        this.radioKm = r;
        double angular = margenKm / RADIO_TIERRA_KM, deltaLat = Math.toDegrees(angular) + 1e-9;
        this.minLat = Math.max(-90, aLat - deltaLat);
        this.maxLat = Math.min(90, bLat + deltaLat);
        double latExtrema = Math.max(Math.abs(minLat), Math.abs(maxLat));
        // Misma esfera que contieneConMargen; cerca de un polo o cruzando el antimeridiano se leen todas las longitudes.
        if (latExtrema >= 89.9 || bLon - aLon > 180) {
            this.minLon = -180; this.maxLon = 180;
        } else {
            double deltaLon = Math.toDegrees(Math.asin(Math.min(1, Math.sin(angular) / Math.cos(Math.toRadians(latExtrema))))) + 1e-9;
            this.minLon = aLon - deltaLon < -180 ? -180 : aLon - deltaLon;
            this.maxLon = bLon + deltaLon > 180 ? 180 : bLon + deltaLon;
        }
    }

    @Override public String getTipo() { return "CLUSTER_RED"; }

    @Override public String getDescripcion() {
        return String.format(Locale.ROOT, "Cluster %s (%d ubicaciones de celda)", cluster, puntos.size());
    }

    public String getCluster() { return cluster; }
    public int getUbicaciones() { return puntos.size(); }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    /** Radio desde el centro que cubre todas las celdas del cluster (encuadre del mapa). */
    public double getRadioKm() { return radioKm; }
    @Override public double getMargenKm() { return margenKm; }

    @Override public boolean contiene(double lat, double lon) { return distanciaMinimaKm(lat, lon) <= 1e-9; }

    @Override public boolean contieneConMargen(double lat, double lon) {
        if (lat < minLat || lat > maxLat || lon < minLon || lon > maxLon) return false;
        return distanciaMinimaKm(lat, lon) <= margenKm + 1e-9;
    }

    @Override public boolean incluye(CeldaAuditoriaDTO celda) {
        return celda.getCluster() != null && celda.getCluster().trim().equalsIgnoreCase(cluster);
    }

    @Override public CajaGeografica cajaConMargen() { return new CajaGeografica(minLat, maxLat, minLon, maxLon); }

    /** Las ubicaciones forman parte de la clave: si la red cambia, no se reutiliza un calculo anterior. */
    @Override public String clave() {
        double suma = 0;
        for (double[] p : puntos) suma += p[0] * 31 + p[1];
        return "CLUSTER_RED|" + cluster.toUpperCase(Locale.ROOT) + "|" + Double.toHexString(margenKm)
                + "|" + puntos.size() + "|" + Double.toHexString(suma);
    }

    private double distanciaMinimaKm(double lat, double lon) {
        double minima = Double.POSITIVE_INFINITY;
        for (double[] p : puntos) {
            minima = Math.min(minima, ClusterAlcance.distanciaKm(p[0], p[1], lat, lon));
            if (minima == 0) break;
        }
        return minima;
    }
}
