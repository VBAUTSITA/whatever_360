package com.tracker.web.baseline5g.controller;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vw_plani_baseline5g_dash03")
public class Baseline5gGraph03{
	
    @Id        
    private String estado;
    private int cantidad;
        

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
    

    
 // Getters y setters para todos los campos

    
    
}