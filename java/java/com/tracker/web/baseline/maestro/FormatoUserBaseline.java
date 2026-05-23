package com.tracker.web.baseline.maestro;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Table(name = "PLANI_BS_PARAMETROS_4G_USUARIOS", uniqueConstraints = {@UniqueConstraint(columnNames = "orden")})
public class FormatoUserBaseline {

@Id
private Long id;
private String orden;
private String levelBs;
private String claseMo;
private String parametroId;
private String parameterName;
private String subParametro;
private String condicional;
private String bandAll;
private String band210MHz;
private String band215MHz;
private String band2815MHz;
private String band710MHz;
private String band715MHz;
private String band720MHz;
private String scriptBs;
private Date fechaActualizada;

//Getters and setters

public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getOrden() {
	return orden;
}
public void setOrden(String orden) {
	this.orden = orden;
}
public String getLevelBs() {
	return levelBs;
}
public void setLevelBs(String levelBs) {
	this.levelBs = levelBs;
}
public String getClaseMo() {
	return claseMo;
}
public void setClaseMo(String claseMo) {
	this.claseMo = claseMo;
}
public String getParametroId() {
	return parametroId;
}
public void setParametroId(String parametroId) {
	this.parametroId = parametroId;
}
public String getParameterName() {
	return parameterName;
}
public void setParameterName(String parameterName) {
	this.parameterName = parameterName;
}
public String getSubParametro() {
	return subParametro;
}
public void setSubParametro(String subParametro) {
	this.subParametro = subParametro;
}
public String getCondicional() {
	return condicional;
}
public void setCondicional(String condicional) {
	this.condicional = condicional;
}
public String getBandAll() {
	return bandAll;
}
public void setBandAll(String bandAll) {
	this.bandAll = bandAll;
}
public String getBand210MHz() {
	return band210MHz;
}
public void setBand210MHz(String band210mHz) {
	band210MHz = band210mHz;
}
public String getBand215MHz() {
	return band215MHz;
}
public void setBand215MHz(String band215mHz) {
	band215MHz = band215mHz;
}
public String getBand2815MHz() {
	return band2815MHz;
}
public void setBand2815MHz(String band2815mHz) {
	band2815MHz = band2815mHz;
}
public String getBand710MHz() {
	return band710MHz;
}
public void setBand710MHz(String band710mHz) {
	band710MHz = band710mHz;
}
public String getBand715MHz() {
	return band715MHz;
}
public void setBand715MHz(String band715mHz) {
	band715MHz = band715mHz;
}
public String getBand720MHz() {
	return band720MHz;
}
public void setBand720MHz(String band720mHz) {
	band720MHz = band720mHz;
}
public String getScriptBs() {
	return scriptBs;
}
public void setScriptBs(String scriptBs) {
	this.scriptBs = scriptBs;
}
public Date getFechaActualizada() {
	return fechaActualizada;
}
public void setFechaActualizada(Date fechaActualizada) {
	this.fechaActualizada = fechaActualizada;
}



}