package com.tracker.web.recursos.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Una celda NUEVA a planificar (viene de un Recursos sin pciPsc/rsi aun).
 *
 * Mismo aviso que CeldaExistenteDTO: los nombres @JsonProperty deben coincidir
 * EXACTO (mayusculas/minusculas) con lo que planner.js lee de "fila".
 * "indoor" se manda como boolean JSON real (true/false): String(true) =>
 * "true", que SI esta en la lista de valores indoor que acepta planner.js;
 * String(false) => "false", que no esta, por lo que cae correctamente a
 * outdoor. No hace falta mapear a "S"/"N" aqui.
 */
public class CeldaPlanDTO {

	@JsonProperty("Cellname")
	private String cellname;

	@JsonProperty("Carrier")
	private String carrier;

	// ========================== MULTI-TECNOLOGIA ==========================
	// "4G" o "5G". OJO: ningun planner_*.js lee esta propiedad — la SELECCION
	// del motor (planner_4g.js vs planner_5g.js) y de la fuente de red
	// existente ocurre ANTES, en Java (PciRsiAsignacionServiceImpl separa el
	// lote por tecnologia y llama a PlannerEngine.planificarLote4g/5g).
	// Se serializa hacia el JS solo como traza informativa/depuracion: los
	// motores ignoran propiedades desconocidas sin efecto alguno.
	// ======================================================================
	@JsonProperty("Tecnologia")
	private String tecnologia;

	@JsonProperty("Phycellid")
	private Integer phycellid;

	@JsonProperty("Freqband")
	private String freqband;

	@JsonProperty("Txrxmode")
	private String txrxmode;

	@JsonProperty("Latitud")
	private Double latitud;

	@JsonProperty("Longitud")
	private Double longitud;

	@JsonProperty("Azimuth")
	private Double azimuth;

	@JsonProperty("Indoor")
	private Boolean indoor;

	public String getCellname() {
		return cellname;
	}

	public void setCellname(String cellname) {
		this.cellname = cellname;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getTecnologia() {
		return tecnologia;
	}

	public void setTecnologia(String tecnologia) {
		this.tecnologia = tecnologia;
	}

	public Integer getPhycellid() {
		return phycellid;
	}

	public void setPhycellid(Integer phycellid) {
		this.phycellid = phycellid;
	}

	public String getFreqband() {
		return freqband;
	}

	public void setFreqband(String freqband) {
		this.freqband = freqband;
	}

	public String getTxrxmode() {
		return txrxmode;
	}

	public void setTxrxmode(String txrxmode) {
		this.txrxmode = txrxmode;
	}

	public Double getLatitud() {
		return latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	public Double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(Double azimuth) {
		this.azimuth = azimuth;
	}

	public Boolean getIndoor() {
		return indoor;
	}

	public void setIndoor(Boolean indoor) {
		this.indoor = indoor;
	}
}
