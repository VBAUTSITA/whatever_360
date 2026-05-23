package com.tracker.web.baseline5g4g.list.parametros.controller;

import javax.persistence.*;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "vw_plani_baseline5g4g_details01")
public class Baseline5g4gSitesParametros {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true)
	private String region;
	
	@Column(nullable = true)
	private String sub_region;
	
	@Column(nullable = true)
	private String provincia;
	
	@Column(nullable = true)
	private String distrito;
	
	@Column(nullable = true)
	private String mbts_name;
	
	@Column(nullable = true)
	private String parametros;
	
	
	// Getters y setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSub_region() {
		return sub_region;
	}

	public void setSub_region(String sub_region) {
		this.sub_region = sub_region;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getParametros() {
		return parametros;
	}

	public void setParametros(String parametros) {
		this.parametros = parametros;
	}

	public String getMbts_name() {
		return mbts_name;
	}

	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
	
	
	

}


