package com.tracker.web.recursos.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Una celda 5G de la red YA desplegada, tal como la lee planner_5g.js.
 *
 * ============================ MULTI-TECNOLOGIA ============================
 * [CRITICO] planner_5g.js lee los MISMOS nombres de propiedad que el 4G:
 * fila.Cellname, fila.Carrier, fila.Phycellid, fila.Freqband, fila.Txrxmode,
 * fila.Latitud, fila.Longitud, fila.Azimuth (verificado contra planner_5g.js:
 * claves_carrier = ['Carrier','Freqband'] y PoolCarrier._push usa f.Phycellid).
 * NO usar "Physicalcellid" ni "Frequencyband" como @JsonProperty aunque asi
 * se llamen las columnas en tm_conf_actual_celda_5g: el motor JS haria
 * Number(undefined) = NaN en silencio y la segmentacion por carrier/banda
 * quedaria vacia sin lanzar ningun error.
 *
 * Lo UNICO distinto al DTO 4G son los 8 bins de Timing Advance: los cortes
 * 5G NR son 117/507/975/1775/3315/7215/15015 metros (BINS_TA en
 * planner_5g.js), distintos a los 234/546/.../14430 del 4G. Por eso este DTO
 * es una clase separada y no se reutiliza CeldaExistenteDTO: cada motor JS
 * recibe exactamente los nombres de TA que sabe leer.
 * ==========================================================================
 */
public class CeldaExistenteDTO5G {

	@JsonProperty("Cellname")
	private String cellname;

	@JsonProperty("Carrier")
	private String carrier;

	// [CRITICO] "Phycellid" (no "Physicalcellid"): es el nombre que planner_5g.js lee.
	@JsonProperty("Phycellid")
	private Integer phycellid;

	// [CRITICO] "Freqband" (no "Frequencyband"): clave de segmentacion del paso 2.
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

	// Las 8 columnas TA 5G NR: fracciones 0..1 que deben sumar ~1.0 (ya
	// normalizadas por RedExistenteServiceImpl5G.normalizarTA antes de llegar
	// aqui). Nombres EXACTOS de BINS_TA/COLS_TA_ALL en planner_5g.js.
	@JsonProperty("ta_0_117_mts_porc")
	private Double ta0117;

	@JsonProperty("ta_117_507_mts_porc")
	private Double ta117507;

	@JsonProperty("ta_507_975_mts_porc")
	private Double ta507975;

	@JsonProperty("ta_975_1775_mts_porc")
	private Double ta9751775;

	@JsonProperty("ta_1775_3315_mts_porc")
	private Double ta17753315;

	@JsonProperty("ta_3315_7215_mts_porc")
	private Double ta33157215;

	@JsonProperty("ta_7215_15015_mts_porc")
	private Double ta721515015;

	@JsonProperty("ta_mayor_a_15015_mts_porc")
	private Double taMayorA15015;

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

	public Double getTa0117() {
		return ta0117;
	}

	public void setTa0117(Double ta0117) {
		this.ta0117 = ta0117;
	}

	public Double getTa117507() {
		return ta117507;
	}

	public void setTa117507(Double ta117507) {
		this.ta117507 = ta117507;
	}

	public Double getTa507975() {
		return ta507975;
	}

	public void setTa507975(Double ta507975) {
		this.ta507975 = ta507975;
	}

	public Double getTa9751775() {
		return ta9751775;
	}

	public void setTa9751775(Double ta9751775) {
		this.ta9751775 = ta9751775;
	}

	public Double getTa17753315() {
		return ta17753315;
	}

	public void setTa17753315(Double ta17753315) {
		this.ta17753315 = ta17753315;
	}

	public Double getTa33157215() {
		return ta33157215;
	}

	public void setTa33157215(Double ta33157215) {
		this.ta33157215 = ta33157215;
	}

	public Double getTa721515015() {
		return ta721515015;
	}

	public void setTa721515015(Double ta721515015) {
		this.ta721515015 = ta721515015;
	}

	public Double getTaMayorA15015() {
		return taMayorA15015;
	}

	public void setTaMayorA15015(Double taMayorA15015) {
		this.taMayorA15015 = taMayorA15015;
	}
}
