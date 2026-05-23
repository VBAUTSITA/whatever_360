package com.tracker.web.baseline5g4g.controller;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vw_plani_baseline5g4g_dash04")
public class Baseline5g4gGraph04{
	
    @Id        
    private int cantidad_mbts;
    private int cantidad_parametros;

    
 // Getters y setters para todos los campos

	public int getCantidad_mbts() {
		return cantidad_mbts;
	}
	public void setCantidad_mbts(int cantidad_mbts) {
		this.cantidad_mbts = cantidad_mbts;
	}
	public int getCantidad_parametros() {
		return cantidad_parametros;
	}
	public void setCantidad_parametros(int cantidad_parametros) {
		this.cantidad_parametros = cantidad_parametros;
	}
        
    
    
}