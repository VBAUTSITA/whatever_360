package com.tracker.web.auditoriared;

import java.util.Locale;

/** Circulo de radioKm alrededor de un punto, mas el margen de reuso. */
public final class ClusterAlcance implements Alcance {

    private static final double RADIO_TIERRA_KM = 6371.0088;
    private static final double KM_POR_GRADO_LAT = 111.32;

    private final double latitud;
    private final double longitud;
    private final double radioKm;
    private final double margenKm;

    public ClusterAlcance(double latitud, double longitud, double radioKm, double margenKm) {
        if (!Double.isFinite(latitud) || !Double.isFinite(longitud)
                || latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180
                || !Double.isFinite(radioKm) || radioKm <= 0
                || !Double.isFinite(margenKm) || margenKm < 0) {
            throw new IllegalArgumentException("Centro, radio o margen del cluster invalidos");
        }
        this.latitud = latitud;
        this.longitud = longitud;
        this.radioKm = radioKm;
        this.margenKm = margenKm;
    }

    @Override public String getTipo() { return "CIRCULO"; }

    @Override public String getDescripcion() {
        return String.format(Locale.ROOT, "Radio %.1f km alrededor de %.6f, %.6f", radioKm, latitud, longitud);
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public double getRadioKm() { return radioKm; }
    @Override public double getMargenKm() { return margenKm; }

    @Override public boolean contiene(double lat, double lon) {
        return distanciaKm(latitud, longitud, lat, lon) <= radioKm + 1e-9;
    }

    @Override public boolean contieneConMargen(double lat, double lon) {
        return distanciaKm(latitud, longitud, lat, lon) <= radioKm + margenKm + 1e-9;
    }

    @Override public CajaGeografica cajaConMargen() {
        double radioTotal = radioKm + margenKm;
        double deltaLat = radioTotal / KM_POR_GRADO_LAT;
        // Se usa la latitud mas alejada del ecuador para que la caja no recorte el circulo.
        double extrema = Math.min(89.9, Math.abs(latitud) + deltaLat);
        double deltaLon = radioTotal / (KM_POR_GRADO_LAT
                * Math.max(0.05, Math.cos(Math.toRadians(extrema))));
        return new CajaGeografica(Math.max(-90, latitud - deltaLat),
                Math.min(90, latitud + deltaLat),
                Math.max(-180, longitud - deltaLon),
                Math.min(180, longitud + deltaLon));
    }

    @Override public String clave() {
        return String.format(Locale.ROOT, "CIRCULO|%.5f|%.5f|%.3f|%.3f", latitud, longitud, radioKm, margenKm);
    }

    public static double distanciaKm(double latA, double lonA, double latB, double lonB) {
        double dLat = Math.toRadians(latB - latA);
        double dLon = Math.toRadians(lonB - lonA);
        double s = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return RADIO_TIERRA_KM * 2 * Math.atan2(Math.sqrt(s), Math.sqrt(Math.max(0, 1 - s)));
    }
}
