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
@Table(name = "PLAN_MAP_CAPA_SITES_WEEK")
public class MarkerSites {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;
	private String site_name_adress;
	private String site_name;
	private String site_address;
	private Double stored_x_location;
	private Double stored_y_location;
	private String tecnologias;
	private String altura_mts;
	private String tx;
	private String equipo;
	private String gsm;
	private String umts;
	private String lte;
	private String nr;	
	private String nombdep;
	private String nombprov;
	private String nombdist;
	private String codigo_dis;
	private String sub_region;
	private String jefe;
	
	// Getters y setters	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSite_name_adress() {
		return site_name_adress;
	}
	public void setSite_name_adress(String site_name_adress) {
		this.site_name_adress = site_name_adress;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}
	public String getSite_address() {
		return site_address;
	}
	public void setSite_address(String site_address) {
		this.site_address = site_address;
	}
	public Double getStored_x_location() {
		return stored_x_location;
	}
	public void setStored_x_location(Double stored_x_location) {
		this.stored_x_location = stored_x_location;
	}
	public Double getStored_y_location() {
		return stored_y_location;
	}
	public void setStored_y_location(Double stored_y_location) {
		this.stored_y_location = stored_y_location;
	}
	public String getTecnologias() {
		return tecnologias;
	}
	public void setTecnologias(String tecnologias) {
		this.tecnologias = tecnologias;
	}
	public String getAltura_mts() {
		return altura_mts;
	}
	public void setAltura_mts(String altura_mts) {
		this.altura_mts = altura_mts;
	}
	public String getTx() {
		return tx;
	}
	public void setTx(String tx) {
		this.tx = tx;
	}
	public String getEquipo() {
		return equipo;
	}
	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}
	public String getGsm() {
		return gsm;
	}
	public void setGsm(String gsm) {
		this.gsm = gsm;
	}
	public String getUmts() {
		return umts;
	}
	public void setUmts(String umts) {
		this.umts = umts;
	}
	public String getLte() {
		return lte;
	}
	public void setLte(String lte) {
		this.lte = lte;
	}
	public String getNr() {
		return nr;
	}
	public void setNr(String nr) {
		this.nr = nr;
	}
	public String getNombdep() {
		return nombdep;
	}
	public void setNombdep(String nombdep) {
		this.nombdep = nombdep;
	}
	public String getNombprov() {
		return nombprov;
	}
	public void setNombprov(String nombprov) {
		this.nombprov = nombprov;
	}
	public String getNombdist() {
		return nombdist;
	}
	public void setNombdist(String nombdist) {
		this.nombdist = nombdist;
	}
	public String getCodigo_dis() {
		return codigo_dis;
	}
	public void setCodigo_dis(String codigo_dis) {
		this.codigo_dis = codigo_dis;
	}
	public String getSub_region() {
		return sub_region;
	}
	public void setSub_region(String sub_region) {
		this.sub_region = sub_region;
	}
	public String getJefe() {
		return jefe;
	}
	public void setJefe(String jefe) {
		this.jefe = jefe;
	}
		
}