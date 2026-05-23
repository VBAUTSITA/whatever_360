package com.tracker.web.baseline5g.list.parametros.controller;

import javax.persistence.*;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "vw_plani_baseline5g_details02")
public class Baseline5gSitesClasemo {
	
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
	private String clase;
	

	
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

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getMbts_name() {
		return mbts_name;
	}

	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
	
	
	

}


