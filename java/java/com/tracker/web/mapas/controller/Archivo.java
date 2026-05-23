package com.tracker.web.mapas.controller;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "opti_mapascapas_saved")
//@Table(name = "opti_mapascapas_saved_test")
public class Archivo {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre_archivo_capa;
	private String archivo_capa;
	private String descripcion_capa;
	private String usuario_archivo;	
	//@DateTimeFormat(pattern="dd/MMM/YYYY HH:mm:ss a")
	//private Date fecha_carga;
	private String fecha_carga;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre_archivo_capa() {
		return nombre_archivo_capa;
	}
	public void setNombre_archivo_capa(String nombre_archivo_capa) {
		this.nombre_archivo_capa = nombre_archivo_capa;
	}
	public String getArchivo_capa() {
		return archivo_capa;
	}
	public void setArchivo_capa(String archivo_capa) {
		this.archivo_capa = archivo_capa;
	}
	public String getDescripcion_capa() {
		return descripcion_capa;
	}
	public void setDescripcion_capa(String descripcion_capa) {
		this.descripcion_capa = descripcion_capa;
	}
	public String getUsuario_archivo() {
		return usuario_archivo;
	}
	public void setUsuario_archivo(String usuario_archivo) {
		this.usuario_archivo = usuario_archivo;
	}
	public String getFecha_carga() {
		return fecha_carga;
	}
	public void setFecha_carga(String fecha_carga) {
		this.fecha_carga = fecha_carga;
	}
	

	
// Getters y setters
	
	
	
}

