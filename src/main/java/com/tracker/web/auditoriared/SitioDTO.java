package com.tracker.web.auditoriared;

/** Resultado de la busqueda por nombre de celda: una entrada por emplazamiento. */
public final class SitioDTO {
    private final String nombre;
    private final double latitud;
    private final double longitud;
    private final String tecnologia;

    public SitioDTO(String nombre, double latitud, double longitud, String tecnologia) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tecnologia = tecnologia;
    }

    public String getNombre() { return nombre; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getTecnologia() { return tecnologia; }
}
