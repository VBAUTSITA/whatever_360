package com.tracker.web.baseline5g.generador.controller;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vw_plani_bs_gen_sites5G")
public class Baselinesites5g{
	
    @Id    
	private String mbts_name;

	public String getMbts_name() {
		return mbts_name;
	}

	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
    
    
}