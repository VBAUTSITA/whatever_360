package com.tracker.web.libreria.controller;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "opti_libreria_saved")
//@Table(name = "opti_libreria_saved_v2")
//@Table(name = "opti_libreria_saved_test")
public class Archivado {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_libreria;
	
	private String nombre_archivo_libreria;
	private String archivo_libreria;
	private String descripcion_archivo_libreria;
	private String usuario_libreria;
	//@DateTimeFormat(pattern="dd/MMM/YYYY HH:mm:ss a")
	//private Date fecha_carga;
	private String fecha_carga;
	
	//private String categoria_libreria;
	private String categoria_libreria;
	
	public Long getId_libreria() {
		return id_libreria;
	}
	public void setId_libreria(Long id_libreria) {
		this.id_libreria = id_libreria;
	}
	public String getNombre_archivo_libreria() {
		return nombre_archivo_libreria;
	}
	public void setNombre_archivo_libreria(String nombre_archivo_libreria) {
		this.nombre_archivo_libreria = nombre_archivo_libreria;
	}
	public String getArchivo_libreria() {
		return archivo_libreria;
	}
	public void setArchivo_libreria(String archivo_libreria) {
		this.archivo_libreria = archivo_libreria;
	}
	public String getDescripcion_archivo_libreria() {
		return descripcion_archivo_libreria;
	}
	public void setDescripcion_archivo_libreria(String descripcion_archivo_libreria) {
		this.descripcion_archivo_libreria = descripcion_archivo_libreria;
	}
	public String getUsuario_libreria() {
		return usuario_libreria;
	}
	public void setUsuario_libreria(String usuario_libreria) {
		this.usuario_libreria = usuario_libreria;
	}
	public String getFecha_carga() {
		return fecha_carga;
	}
	public void setFecha_carga(String fecha_carga) {
		this.fecha_carga = fecha_carga;
	}
	
	/*
	public String getcategoria_libreria() {
		return categoria_libreria;
	}
	public void setcategoria_libreria(String categoria_libreria) {
		this.categoria_libreria = categoria_libreria;
	}
	*/
	
	public String getCategoria_libreria() {
		return categoria_libreria;
	}
	public void setCategoria_libreria(String categoria_libreria) {
		this.categoria_libreria = categoria_libreria;
	}
	

	
// Getters y setters
	
	
	
}