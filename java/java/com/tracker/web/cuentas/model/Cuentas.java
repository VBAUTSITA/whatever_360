package com.tracker.web.cuentas.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

public class Cuentas {
	private	Integer id_usuario;
	private	String codigo_usuario;
	private	String nombre_usuario;
	private	String apellidos_usuario;
	private	String correo_usuario;
	private	String clave_usuario;
	private	Date horario_registrado;
	private String clave_usuario_bcry;
	
	
	public Integer getId_usuario() {
		return id_usuario;
	}
	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}
	public String getCodigo_usuario() {
		return codigo_usuario;
	}
	public void setCodigo_usuario(String codigo_usuario) {
		this.codigo_usuario = codigo_usuario;
	}
	public String getNombre_usuario() {
		return nombre_usuario;
	}
	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}
	public String getApellidos_usuario() {
		return apellidos_usuario;
	}
	public void setApellidos_usuario(String apellidos_usuario) {
		this.apellidos_usuario = apellidos_usuario;
	}
	public String getCorreo_usuario() {
		return correo_usuario;
	}
	public void setCorreo_usuario(String correo_usuario) {
		this.correo_usuario = correo_usuario;
	}
	public String getClave_usuario() {
		return clave_usuario;
	}
	public void setClave_usuario(String clave_usuario) {
		this.clave_usuario = clave_usuario;
	}
	public Date getHorario_registrado() {
		return horario_registrado;
	}
	public void setHorario_registrado(Date horario_registrado) {
		this.horario_registrado = horario_registrado;
	}
	public String getClave_usuario_bcry() {
		return clave_usuario_bcry;
	}
	public void setClave_usuario_bcry(String clave_usuario_bcry) {
		this.clave_usuario_bcry = clave_usuario_bcry;
	}
	
	
	
} 