package com.tracker.web.auditoriared;

/**
 * Zona a auditar. Separa el area que ve el usuario (contiene) de la que recibe el
 * motor (contieneConMargen): sin el margen, los conflictos del borde desaparecen.
 */
public interface Alcance {

    /** Geometria para la vista: CIRCULO o CLUSTER_RED (celdas de un CLUSTER_NAME). */
    String getTipo();

    String getDescripcion();

    double getMargenKm();

    boolean contiene(double latitud, double longitud);

    boolean contieneConMargen(double latitud, double longitud);

    /** Celda del area visible. Por defecto geometrica; un cluster de red decide por su nombre. */
    default boolean incluye(CeldaAuditoriaDTO celda) {
        return contiene(celda.getLatitud(), celda.getLongitud());
    }

    /** Prefiltro de la consulta SQL; el filtro exacto es contieneConMargen. */
    CajaGeografica cajaConMargen();

    /** Misma clave = misma auditoria (se usa para la cache). */
    String clave();
}
