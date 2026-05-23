package com.tracker.web.mapa.controller;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Entity
//@Table(name = "vw_smart_opti_mapa_saved_test")
//@Table(name = "opti_mapa_saved_test")
@Table(name = "PLANI_PLOT_CONSTRUCCION")
public class PlotConstruccion {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private String tipo_seguimiento;
	private String ot;
	private String codigosite;
	private String nombresite;
	private String estado_sitio;
	private String tipo_estacion;
	private String status_ot;
	private Double latitud;
	private Double longitud;
	
	// Getters y setters
	public Long getId() {
	    return id;
	}
	public void setId(Long id) {
	    this.id = id;
	}
	
	public String getTipo_seguimiento() {
	    return tipo_seguimiento;
	}
	public void setTipo_seguimiento(String tipo_seguimiento) {
	    this.tipo_seguimiento = tipo_seguimiento;
	}
	
	public String getOt() {
	    return ot;
	}
	public void setOt(String ot) {
	    this.ot = ot;
	}
	
	public String getCodigosite() {
	    return codigosite;
	}
	public void setCodigosite(String codigosite) {
	    this.codigosite = codigosite;
	}
	
	public String getNombresite() {
	    return nombresite;
	}
	public void setNombresite(String nombresite) {
	    this.nombresite = nombresite;
	}
	
	public String getEstado_sitio() {
	    return estado_sitio;
	}
	public void setEstado_sitio(String estado_sitio) {
	    this.estado_sitio = estado_sitio;
	}
	
	public String getTipo_estacion() {
	    return tipo_estacion;
	}
	public void setTipo_estacion(String tipo_estacion) {
	    this.tipo_estacion = tipo_estacion;
	}
	
	public String getStatus_ot() {
	    return status_ot;
	}
	public void setStatus_ot(String status_ot) {
	    this.status_ot = status_ot;
	}
	
	public Double getLatitud() {
	    return latitud;
	}
	public void setLatitud(Double latitud) {
	    this.latitud = latitud;
	}
	
	public Double getLongitud() {
	    return longitud;
	}
	public void setLongitud(Double longitud) {
	    this.longitud = longitud;
	}
		
		
}