package com.tracker.web.recursos.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "RECURSOS_FORMULARIO")
public class Recursos {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECURSOS_SEQ")
	@SequenceGenerator(name = "RECURSOS_SEQ", sequenceName = "RECURSOS_SEQ", allocationSize = 1)	
	private Long id;
	//faltaba el campo semana
	private String semana;
	private String siteName;
	private String cellName;
	private Double longitud;
	private Double latitud;
	private String tecnologia;
	private String proyecto;
	private Integer pciPsc;
	private Integer rsi;
	private Integer mod;
	private String usuarioIngreso;
	private String jefatura;
	
	private String fechaIngreso;          // ANTES ERA DATE TYPE LO CMABIE set once, not updateable
	private String fechaActualizacion;    // auto-updated
	private String fechaAsignacion;       // auto-updated

	// Campos para el motor de auto-asignacion PCI/RSI (planner.js via Nashorn)
	private String carrier;
	private Double azimuth;
	private String txrxmode;
	private String freqband;
	private String indoor;        // "S" / "N"
	private String auditoriaPci;  // traza del motor: por que se eligio el PCI/PSS
	/*
	@PrePersist
	@PreUpdate
	public void prePersist() {
	this.fechaActualizacion = new Date();
	this.fechaAsignacion = new Date();
	}*/
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	public Double getLatitud() {
		return latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public String getTecnologia() {
		return tecnologia;
	}

	public void setTecnologia(String tecnologia) {
		this.tecnologia = tecnologia;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public Integer getPciPsc() {
		return pciPsc;
	}

	public void setPciPsc(Integer pciPsc) {
		this.pciPsc = pciPsc;
	}

	public Integer getRsi() {
		return rsi;
	}

	public void setRsi(Integer rsi) {
		this.rsi = rsi;
	}

	public Integer getMod() {
		return mod;
	}

	public void setMod(Integer mod) {
		this.mod = mod;
	}

	public String getUsuarioIngreso() {
		return usuarioIngreso;
	}

	public void setUsuarioIngreso(String usuarioIngreso) {
		this.usuarioIngreso = usuarioIngreso;
	}

	public String getJefatura() {
		return jefatura;
	}

	public void setJefatura(String jefatura) {
		this.jefatura = jefatura;
	}

	public String getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(String fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public String getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(String fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public String getFechaAsignacion() {
		return fechaAsignacion;
	}

	public void setFechaAsignacion(String fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}



	public String getSemana() {
		return semana;
	}



	public void setSemana(String semana) {
		this.semana = semana;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(Double azimuth) {
		this.azimuth = azimuth;
	}

	public String getTxrxmode() {
		return txrxmode;
	}

	public void setTxrxmode(String txrxmode) {
		this.txrxmode = txrxmode;
	}

	public String getFreqband() {
		return freqband;
	}

	public void setFreqband(String freqband) {
		this.freqband = freqband;
	}

	public String getIndoor() {
		return indoor;
	}

	public void setIndoor(String indoor) {
		this.indoor = indoor;
	}

	public String getAuditoriaPci() {
		return auditoriaPci;
	}

	public void setAuditoriaPci(String auditoriaPci) {
		this.auditoriaPci = auditoriaPci;
	}

}