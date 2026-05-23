package com.tracker.web.baseline.generador.controller;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vw_plani_baseline_sites01")
public class Baselinesites{
	
    @Id    
	private String mbts_name;

	public String getMbts_name() {
		return mbts_name;
	}

	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
    
    
}