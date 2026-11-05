package com.tracker.web.auditoriared;

/** Un CLUSTER_NAME de la red con sus celdas distintas por tecnologia (selector del paso 1). */
public final class ClusterRedDTO {
    private final String nombre;
    private int celdas4G;
    private int celdas5G;

    public ClusterRedDTO(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }
    public int getCeldas4G() { return celdas4G; }
    public int getCeldas5G() { return celdas5G; }

    void sumar(String tecnologia, int celdas) {
        if ("4G".equals(tecnologia)) celdas4G += celdas; else celdas5G += celdas;
    }
}
