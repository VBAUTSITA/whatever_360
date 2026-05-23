package com.tracker.web.pap.report01;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "VW_PLANI_SEGUIM_PP_DASH_02")
public class ReportePP02 {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "proyecto")
    private String proyecto;

    @Column(name = "sub_proyecto")
    private String subProyecto;

    @Column(name = "jefatura_rf")
    private String jefaturaRf;

    @Column(name = "sub_region")
    private String subRegion;

    @Column(name = "contratista")
    private String contratista;

    @Column(name = "region")
    private String region;

    @Column(name = "sector")
    private String sector;

    @Column(name = "ot_pap")
    private String otPap;

    @Column(name = "req")
    private String req;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "semana")            // <<--- NUEVO
    private Integer semana;
    
    @Column(name = "estatus_general")
    private String estatusGeneral;
    
    @Column(name = "mes")
    private String mes;

    // Getters y setters aquí...
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public String getSubProyecto() {
		return subProyecto;
	}

	public void setSubProyecto(String subProyecto) {
		this.subProyecto = subProyecto;
	}

	public String getJefaturaRf() {
		return jefaturaRf;
	}

	public void setJefaturaRf(String jefaturaRf) {
		this.jefaturaRf = jefaturaRf;
	}

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public String getContratista() {
		return contratista;
	}

	public void setContratista(String contratista) {
		this.contratista = contratista;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getOtPap() {
		return otPap;
	}

	public void setOtPap(String otPap) {
		this.otPap = otPap;
	}

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public String getEstatusGeneral() {
		return estatusGeneral;
	}

	public void setEstatusGeneral(String estatusGeneral) {
		this.estatusGeneral = estatusGeneral;
	}

	public Integer getSemana() {
		return semana;
	}

	public void setSemana(Integer semana) {
		this.semana = semana;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

    
    
    
}

