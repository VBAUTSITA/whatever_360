package com.tracker.web.recursos.planner.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Una fila de salida de Planificador.planificarLote (objeto "r" en
 * planner.js). Solo se mapean los campos que este modulo realmente consume;
 * @JsonIgnoreProperties(ignoreUnknown) descarta el resto (vecinas, wedge_plan,
 * candidatos_top, etc.) sin que Jackson falle por campos no modelados.
 *
 * IMPORTANTE: a diferencia de las DTO de entrada, estos nombres ya son
 * snake_case tal cual los escribe planner.js (pss_elegido, etc.) - no son
 * convertidos por ninguna libreria, son los literales del objeto JS.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoPlanificacionDTO {

	private String cellname;

	private Integer pci;

	private Integer rsi;

	@JsonProperty("pss_elegido")
	private Integer pssElegido;

	private String error;

	private List<String> advertencias;

	private List<String> auditoria;

	public String getCellname() {
		return cellname;
	}

	public void setCellname(String cellname) {
		this.cellname = cellname;
	}

	public Integer getPci() {
		return pci;
	}

	public void setPci(Integer pci) {
		this.pci = pci;
	}

	public Integer getRsi() {
		return rsi;
	}

	public void setRsi(Integer rsi) {
		this.rsi = rsi;
	}

	public Integer getPssElegido() {
		return pssElegido;
	}

	public void setPssElegido(Integer pssElegido) {
		this.pssElegido = pssElegido;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<String> getAdvertencias() {
		return advertencias;
	}

	public void setAdvertencias(List<String> advertencias) {
		this.advertencias = advertencias;
	}

	public List<String> getAuditoria() {
		return auditoria;
	}

	public void setAuditoria(List<String> auditoria) {
		this.auditoria = auditoria;
	}
}
