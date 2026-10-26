package com.tracker.web.auditoriared;

import java.util.List;

public class SolicitudAuditoria {
    private String sitio;
    private Double latitud;
    private Double longitud;
    private Double radioKm;
    /** Opcional: si falta se usa el de configuracion (10 km). Define tambien el margen. */
    private Double radioReusoKm;
    private List<String> tecnologias;
    /** True = ignorar la cache y recalcular. */
    private boolean refrescar;

    public String getSitio() { return sitio; }
    public void setSitio(String sitio) { this.sitio = sitio; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Double getRadioKm() { return radioKm; }
    public void setRadioKm(Double radioKm) { this.radioKm = radioKm; }
    public Double getRadioReusoKm() { return radioReusoKm; }
    public void setRadioReusoKm(Double radioReusoKm) { this.radioReusoKm = radioReusoKm; }
    public List<String> getTecnologias() { return tecnologias; }
    public void setTecnologias(List<String> tecnologias) { this.tecnologias = tecnologias; }
    public boolean isRefrescar() { return refrescar; }
    public void setRefrescar(boolean refrescar) { this.refrescar = refrescar; }
}
