package com.tracker.web.recursos.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Una celda de la red YA desplegada, tal como la lee planner.js.
 *
 * IMPORTANTE: el acceso a propiedades en planner.js es sensible a
 * mayusculas/minusculas (fila.Cellname, fila.Carrier, fila.Phycellid,
 * fila.Freqband, fila.Txrxmode, fila.Latitud, fila.Longitud, fila.Azimuth,
 * y las 8 columnas TA en minusculas con sufijo "_porc"). Cambiar cualquier
 * @JsonProperty aqui sin revisar planner.js produce un Number(undefined) =
 * NaN silencioso, no un error.
 */
public class CeldaExistenteDTO {

	@JsonProperty("Cellname")
	private String cellname;

	@JsonProperty("Carrier")
	private String carrier;

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

	// Las 8 columnas TA: fracciones 0..1 que deben sumar ~1.0 (ya normalizadas
	// por RedExistenteService.normalizarTA antes de llegar aqui).
	@JsonProperty("ta_0_234_mts_porc")
	private Double ta0234;

	@JsonProperty("ta_234_546_mts_porc")
	private Double ta234546;

	@JsonProperty("ta_546_1014_mts_porc")
	private Double ta5461014;

	@JsonProperty("ta_1014_1950_mts_porc")
	private Double ta10141950;

	@JsonProperty("ta_1950_3510_mts_porc")
	private Double ta19503510;

	@JsonProperty("ta_3510_6630_mts_porc")
	private Double ta35106630;

	@JsonProperty("ta_6630_14430_mts_porc")
	private Double ta663014430;

	@JsonProperty("ta_mayor_a_14430_mts_porc")
	private Double taMayorA14430;

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

	public Double getTa0234() {
		return ta0234;
	}

	public void setTa0234(Double ta0234) {
		this.ta0234 = ta0234;
	}

	public Double getTa234546() {
		return ta234546;
	}

	public void setTa234546(Double ta234546) {
		this.ta234546 = ta234546;
	}

	public Double getTa5461014() {
		return ta5461014;
	}

	public void setTa5461014(Double ta5461014) {
		this.ta5461014 = ta5461014;
	}

	public Double getTa10141950() {
		return ta10141950;
	}

	public void setTa10141950(Double ta10141950) {
		this.ta10141950 = ta10141950;
	}

	public Double getTa19503510() {
		return ta19503510;
	}

	public void setTa19503510(Double ta19503510) {
		this.ta19503510 = ta19503510;
	}

	public Double getTa35106630() {
		return ta35106630;
	}

	public void setTa35106630(Double ta35106630) {
		this.ta35106630 = ta35106630;
	}

	public Double getTa663014430() {
		return ta663014430;
	}

	public void setTa663014430(Double ta663014430) {
		this.ta663014430 = ta663014430;
	}

	public Double getTaMayorA14430() {
		return taMayorA14430;
	}

	public void setTaMayorA14430(Double taMayorA14430) {
		this.taMayorA14430 = taMayorA14430;
	}
}
