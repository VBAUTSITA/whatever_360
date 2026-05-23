package com.tracker.web.baseline.generador.controller;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vw_plani_baseline_export01")
public class Baselinedata {
	
		@Id
		private String id_mbts;
		private int orden;		
		private String mbts;
		private String clase;	
		private String parametro;
		private String subparametro;
		private String condicional;
		private String valor_baseline;
		private String valor_actual;
		private String script_bs;
		private String estado;
		
		// Getters y setters	    

		public String getId_mbts() {
			return id_mbts;
		}

		public void setId_mbts(String id_mbts) {
			this.id_mbts = id_mbts;
		}

		public int getOrden() {
			return orden;
		}

		public void setOrden(int orden) {
			this.orden = orden;
		}

		public String getMbts() {
			return mbts;
		}

		public void setMbts(String mbts) {
			this.mbts = mbts;
		}

		public String getClase() {
			return clase;
		}

		public void setClase(String clase) {
			this.clase = clase;
		}

		public String getParametro() {
			return parametro;
		}

		public void setParametro(String parametro) {
			this.parametro = parametro;
		}

		public String getSubparametro() {
			return subparametro;
		}

		public void setSubparametro(String subparametro) {
			this.subparametro = subparametro;
		}

		public String getCondicional() {
			return condicional;
		}

		public void setCondicional(String condicional) {
			this.condicional = condicional;
		}

		public String getValor_baseline() {
			return valor_baseline;
		}

		public void setValor_baseline(String valor_baseline) {
			this.valor_baseline = valor_baseline;
		}

		public String getValor_actual() {
			return valor_actual;
		}

		public void setValor_actual(String valor_actual) {
			this.valor_actual = valor_actual;
		}

		public String getScript_bs() {
			return script_bs;
		}

		public void setScript_bs(String script_bs) {
			this.script_bs = script_bs;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		} 
		
		/*@Override
	    public String toString() {
	        return String.format("mbts: %s, clase: %s, parametro: %s, valor_actual: %s",
	                mbts, clase, parametro, valor_actual);
	    } */
}