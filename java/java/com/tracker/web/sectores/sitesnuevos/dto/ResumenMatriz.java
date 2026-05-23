package com.tracker.web.sectores.sitesnuevos.dto;


public class ResumenMatriz {
private String prioridadSector; // PRIORIDAD_SECTOR
private String tipoSitio; // TIPO_SITIO
private int cantidadSites; // COUNT(DISTINCT MBTS_NAME)


public String getPrioridadSector() { return prioridadSector; }
public void setPrioridadSector(String prioridadSector) { this.prioridadSector = prioridadSector; }
public String getTipoSitio() { return tipoSitio; }
public void setTipoSitio(String tipoSitio) { this.tipoSitio = tipoSitio; }
public int getCantidadSites() { return cantidadSites; }
public void setCantidadSites(int cantidadSites) { this.cantidadSites = cantidadSites; }
}