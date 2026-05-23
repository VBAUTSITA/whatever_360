package com.tracker.web.seguimiento.model;

import java.util.Date;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

public class Seguimiento {

	private	Integer site_id;
	private	Integer site_id_hist;
	private	String code_site;
	private	String site_address;
	private	String cellname;
	private	String sector;
	private	String band;
	private	String tecnologia;
	private	Float longitud;
	private	Float latitud;
	private	String tilt_electric_field;
	private	String azimuth;
	private	String height;
	private	String tilt_mecanic_field;
	private String tipo_antena;	
	private String potencia;	
	private String modo_transmision_celda;
	private	String logic_changes;

	@DateTimeFormat(pattern="dd/MMM/YYYY HH:mm:ss a")
	private Date date_change;
	private String usuario;	
	private String fecha;	
	private String estado_cambio;
	private	String cambio_coment;
	
	private String result_time;
	private String result_date;
	/*kpi 2G*/
	@Nullable
	private String ACC_CONGESTION_CS_GSM;
	@Nullable
	private String ACC_PS_GSM;
	@Nullable
	private String ACC_CONGESTION_PS_GSM;
	@Nullable
	private String ACC_RADIO_PS_GSM;
	@Nullable
	private String RET_CS_GSM;
	@Nullable
	private String RET_PS_GSM;
	@Nullable
	private String max_SCDCH_GSM;
	@Nullable
	private String min_SCDCH_GSM;
	@Nullable
	private String SCDCH_DROP_GSM;
	@Nullable
	private String SCDCH_BLOCK_GSM;
	@Nullable
	private String TCH_DENIED_GSM;
	@Nullable
	private String TRAFICO_DATOS_DL_GSM;
	@Nullable
	private String TRAFICO_DATOS_UL_GSM;
	@Nullable
	private String TRAFICO_VOZ_GSM;
	@Nullable
	private String TRAFICO_ERL_GSM;
	@Nullable
	private String TA_MEN_0_55_KM;
	@Nullable
	private String TA_0_55_TA_1_10_KM;
	@Nullable
	private String TA_1_10_TA_1_65_KM;
	@Nullable
	private String TA_1_65_TA_2_20_KM;
	@Nullable
	private String AGRUP_TA_2_20_TA_4_40_KM;
	@Nullable
	private String AGRUP_TA_4_40_TA_5_50_KM;
	@Nullable
	private String AGRUP_TA_5_50_TA_9_90_KM;
	@Nullable
	private String AGRUP_TA_9_90_TA_20_35_KM;
	@Nullable
	private String AGRUP_TA_20_35_TA_34_65_KM;
	@Nullable
	private String TA_MAY_34_65_KM;
	/*KPI 2G MAX AND MIN */
	@Nullable
	private String max_ACC_CONGESTION_CS_GSM;
	@Nullable
	private String min_ACC_CONGESTION_CS_GSM;
	@Nullable
	private String max_ACC_PS_GSM;
	@Nullable
	private String min_ACC_PS_GSM;
	@Nullable
	private String max_ACC_CONG_RAD_PS_GSM;
	@Nullable
	private String min_ACC_CONG_RAD_PS_GSM;
	@Nullable
	private String max_RET_GSM;
	@Nullable
	private String min_RET_GSM;
	@Nullable
	private String max_RET_CS_GSM; 
	@Nullable
	private String min_RET_CS_GSM;
	@Nullable
	private String max_RET_PS_GSM; 
	@Nullable
	private String min_RET_PS_GSM;
	@Nullable
	private String max_SCDCH_DROP_GSM; 
	@Nullable
	private String min_SCDCH_DROP_GSM;
	@Nullable
	private String max_SCDCH_BLOCK_GSM; 
	@Nullable
	private String min_SCDCH_BLOCK_GSM;
	@Nullable
	private String max_TCH_DENIED_GSM; 
	@Nullable
	private String min_TCH_DENIED_GSM;
	@Nullable
	private String max_TRAFICO_DATOS_GSM;
	@Nullable
	private String min_TRAFICO_DATOS_GSM;
	@Nullable
	private String max_TRAFICO_VOZ_GSM;
	@Nullable
	private String min_TRAFICO_VOZ_GSM;
	@Nullable
	private String max_TRAFICO_ERL_GSM; 
	@Nullable
	private String min_TRAFICO_ERL_GSM;
	@Nullable
	private String MAX_TA_GMS_AGRUP;
	@Nullable
	private String MIN_TA_GMS_AGRUP;
	/*kpi 3G*/
	@Nullable
	private String acc_cs;
	@Nullable
	private String acc_ps;	
	@Nullable
	private String ret_cs;	
	@Nullable
	private String ret_ps;	
	@Nullable
	private String throuput_dl;
	@Nullable
	private String dch_users;
	@Nullable
	private String trafico_voz_erlang;
	@Nullable
	private String trafico_datos_dl;
	@Nullable
	private String trafico_datos_ul;
	@Nullable
	private String throuput_ul;
	/*KPI 3G MAX AND MIN */
	@Nullable
	private String max_acc;
	@Nullable
	private String min_acc;
	@Nullable
	private String max_ret;
	@Nullable
	private String min_ret;
	@Nullable
	private String max_throuput;	
	@Nullable
	private String min_throuput;	
	@Nullable
	private String max_dch_users;	
	@Nullable
	private String min_dch_users;	
	@Nullable
	private String max_trafico_voz_erlang;	
	@Nullable
	private String min_trafico_voz_erlang;
	@Nullable
	private String max_trafico_datos;
	@Nullable
	private String min_trafico_datos;
	@Nullable
	private String max_vs_tp_ue;
	@Nullable
	private String min_vs_tp_ue;
	//propagation delay 3g
	@Nullable
	private String vs_tp_ue_0;
	@Nullable
	private String vs_tp_ue_1;
	@Nullable
	private String vs_tp_ue_2;
	@Nullable
	private String vs_tp_ue_3;
	@Nullable
	private String vs_tp_ue_4;
	@Nullable
	private String vs_tp_ue_5;
	@Nullable
	private String vs_tp_ue_6_9;
	@Nullable
	private String vs_tp_ue_10_15;
	@Nullable
	private String vs_tp_ue_16_25;
	@Nullable
	private String vs_tp_ue_26_35;
	@Nullable
	private String vs_tp_ue_36_55;
	@Nullable
	private String vs_tp_ue_more55;
	
	/**/
	/*kpi 4G*/
	@Nullable
	private String prb_dl;
	@Nullable
	private String prb_ul;
	@Nullable
	private String accessibility;
	@Nullable
	private String retainability;
	@Nullable
	private String beusers;
	@Nullable
	private String throuput_user_dl;
	@Nullable
	private String throuput_user_ul;
	@Nullable
	private String dl_traffic_vol_mb;
	@Nullable
	private String ul_traffic_vol_mb;  
	@Nullable
	private String l_ra_ta_ue_index0;
	@Nullable
	private String l_ra_ta_ue_index1;
	@Nullable
	private String l_ra_ta_ue_index2;
	@Nullable
	private String l_ra_ta_ue_index3;
	@Nullable
	private String l_ra_ta_ue_index4;
	@Nullable
	private String l_ra_ta_ue_index5;
	@Nullable
	private String l_ra_ta_ue_index6;
	@Nullable
	private String l_ra_ta_ue_index7;
	@Nullable
	private String l_ra_ta_ue_index8;
	@Nullable
	private String l_ra_ta_ue_index9;
	@Nullable
	private String l_ra_ta_ue_index10;
	@Nullable
	private String l_ra_ta_ue_index11;
	/**/
	/*kpi 4G MAX AND MIN*/
	@Nullable
	private String max_result_time;
	@Nullable
	private String min_result_time;
	@Nullable
	private String max_prb_lte;
	@Nullable
	private String min_prb_lte;
	@Nullable
	private String max_accessibility;
	@Nullable
	private String min_accessibility;
	@Nullable
	private String max_retainability;
	@Nullable
	private String min_retainability;
	@Nullable
	private String max_beusers;
	@Nullable
	private String min_beusers;
	@Nullable
	private String max_throuput_user_lte;
	@Nullable
	private String min_throuput_user_lte;
	@Nullable
	private String max_traffic_vol_mb_lte;
	@Nullable
	private String min_traffic_vol_mb_lte;
	@Nullable
	private String max_l_ra_ta_ue_index;
	@Nullable
	private String min_l_ra_ta_ue_index;	

	/**/
	
	public Integer getSite_id() {
		return site_id;
	}
	public void setSite_id(Integer site_id) {
		this.site_id = site_id;
	}
	public Integer getSite_id_hist() {
		return site_id_hist;
	}
	public void setSite_id_hist(Integer site_id_hist) {
		this.site_id_hist = site_id_hist;
	}
	public String getCode_site() {
		return code_site;
	}
	public void setCode_site(String code_site) {
		this.code_site = code_site;
	}
	public String getSite_address() {
		return site_address;
	}
	public void setSite_address(String site_address) {
		this.site_address = site_address;
	}
	public String getCellname() {
		return cellname;
	}
	public void setCellname(String cellname) {
		this.cellname = cellname;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getBand() {
		return band;
	}
	public void setBand(String band) {
		this.band = band;
	}
	public String getTecnologia() {
		return tecnologia;
	}
	public void setTecnologia(String tecnologia) {
		this.tecnologia = tecnologia;
	}
	public Float getLongitud() {
		return longitud;
	}
	public void setLongitud(Float longitud) {
		this.longitud = longitud;
	}
	public Float getLatitud() {
		return latitud;
	}
	public void setLatitud(Float latitud) {
		this.latitud = latitud;
	}
	public String getTilt_electric_field() {
		return tilt_electric_field;
	}
	public void setTilt_electric_field(String tilt_electric_field) {
		this.tilt_electric_field = tilt_electric_field;
	}
	public String getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(String azimuth) {
		this.azimuth = azimuth;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getTilt_mecanic_field() {
		return tilt_mecanic_field;
	}
	public void setTilt_mecanic_field(String tilt_mecanic_field) {
		this.tilt_mecanic_field = tilt_mecanic_field;
	}
	
	public String getTipo_antena() {
		return tipo_antena;
	}
	public void setTipo_antena(String tipo_antena) {
		this.tipo_antena = tipo_antena;
	}
	public String getPotencia() {
		return potencia;
	}
	public void setPotencia(String potencia) {
		this.potencia = potencia;
	}
	public String getModo_transmision_celda() {
		return modo_transmision_celda;
	}
	public void setModo_transmision_celda(String modo_transmision_celda) {
		this.modo_transmision_celda = modo_transmision_celda;
	}
	public String getLogic_changes() {
		return logic_changes;
	}
	public void setLogic_changes(String logic_changes) {
		this.logic_changes = logic_changes;
	}
	public Date getDate_change() {
		return date_change;
	}
	public void setDate_change(Date date_change) {
		this.date_change = date_change;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getEstado_cambio() {
		return estado_cambio;
	}
	public void setEstado_cambio(String estado_cambio) {
		this.estado_cambio = estado_cambio;
	}
	public String getCambio_coment() {
		return cambio_coment;
	}
	public void setCambio_coment(String cambio_coment) {
		this.cambio_coment = cambio_coment;
	}
	public String getResult_time() {
		return result_time;
	}
	public void setResult_time(String result_time) {
		this.result_time = result_time;
	}
	
	public String getResult_date() {
		return result_date;
	}
	public void setResult_date(String result_date) {
		this.result_date = result_date;
	}
	/*get and set KPI 2G*/
	public String getACC_CONGESTION_CS_GSM() {
		return ACC_CONGESTION_CS_GSM;
	}
	public void setACC_CONGESTION_CS_GSM(String aCC_CONGESTION_CS_GSM) {
		ACC_CONGESTION_CS_GSM = aCC_CONGESTION_CS_GSM;
	}
	public String getACC_PS_GSM() {
		return ACC_PS_GSM;
	}
	public void setACC_PS_GSM(String aCC_PS_GSM) {
		ACC_PS_GSM = aCC_PS_GSM;
	}
	public String getACC_CONGESTION_PS_GSM() {
		return ACC_CONGESTION_PS_GSM;
	}
	public void setACC_CONGESTION_PS_GSM(String aCC_CONGESTION_PS_GSM) {
		ACC_CONGESTION_PS_GSM = aCC_CONGESTION_PS_GSM;
	}
	public String getACC_RADIO_PS_GSM() {
		return ACC_RADIO_PS_GSM;
	}
	public void setACC_RADIO_PS_GSM(String aCC_RADIO_PS_GSM) {
		ACC_RADIO_PS_GSM = aCC_RADIO_PS_GSM;
	}
	public String getRET_CS_GSM() {
		return RET_CS_GSM;
	}
	public void setRET_CS_GSM(String rET_CS_GSM) {
		RET_CS_GSM = rET_CS_GSM;
	}
	public String getRET_PS_GSM() {
		return RET_PS_GSM;
	}
	public void setRET_PS_GSM(String rET_PS_GSM) {
		RET_PS_GSM = rET_PS_GSM;
	}
	public String getSCDCH_DROP_GSM() {
		return SCDCH_DROP_GSM;
	}
	public void setSCDCH_DROP_GSM(String sCDCH_DROP_GSM) {
		SCDCH_DROP_GSM = sCDCH_DROP_GSM;
	}
	public String getSCDCH_BLOCK_GSM() {
		return SCDCH_BLOCK_GSM;
	}
	public void setSCDCH_BLOCK_GSM(String sCDCH_BLOCK_GSM) {
		SCDCH_BLOCK_GSM = sCDCH_BLOCK_GSM;
	}
	public String getTCH_DENIED_GSM() {
		return TCH_DENIED_GSM;
	}
	public void setTCH_DENIED_GSM(String tCH_DENIED_GSM) {
		TCH_DENIED_GSM = tCH_DENIED_GSM;
	}
	public String getTRAFICO_DATOS_DL_GSM() {
		return TRAFICO_DATOS_DL_GSM;
	}
	public void setTRAFICO_DATOS_DL_GSM(String tRAFICO_DATOS_DL_GSM) {
		TRAFICO_DATOS_DL_GSM = tRAFICO_DATOS_DL_GSM;
	}
	public String getTRAFICO_DATOS_UL_GSM() {
		return TRAFICO_DATOS_UL_GSM;
	}
	public void setTRAFICO_DATOS_UL_GSM(String tRAFICO_DATOS_UL_GSM) {
		TRAFICO_DATOS_UL_GSM = tRAFICO_DATOS_UL_GSM;
	}
	public String getTRAFICO_VOZ_GSM() {
		return TRAFICO_VOZ_GSM;
	}
	public void setTRAFICO_VOZ_GSM(String tRAFICO_VOZ_GSM) {
		TRAFICO_VOZ_GSM = tRAFICO_VOZ_GSM;
	}
	public String getTRAFICO_ERL_GSM() {
		return TRAFICO_ERL_GSM;
	}
	public void setTRAFICO_ERL_GSM(String tRAFICO_ERL_GSM) {
		TRAFICO_ERL_GSM = tRAFICO_ERL_GSM;
	}
	public String getTA_MEN_0_55_KM() {
		return TA_MEN_0_55_KM;
	}
	public void setTA_MEN_0_55_KM(String tA_MEN_0_55_KM) {
		TA_MEN_0_55_KM = tA_MEN_0_55_KM;
	}
	public String getTA_0_55_TA_1_10_KM() {
		return TA_0_55_TA_1_10_KM;
	}
	public void setTA_0_55_TA_1_10_KM(String tA_0_55_TA_1_10_KM) {
		TA_0_55_TA_1_10_KM = tA_0_55_TA_1_10_KM;
	}
	public String getTA_1_10_TA_1_65_KM() {
		return TA_1_10_TA_1_65_KM;
	}
	public void setTA_1_10_TA_1_65_KM(String tA_1_10_TA_1_65_KM) {
		TA_1_10_TA_1_65_KM = tA_1_10_TA_1_65_KM;
	}
	public String getTA_1_65_TA_2_20_KM() {
		return TA_1_65_TA_2_20_KM;
	}
	public void setTA_1_65_TA_2_20_KM(String tA_1_65_TA_2_20_KM) {
		TA_1_65_TA_2_20_KM = tA_1_65_TA_2_20_KM;
	}
	public String getAGRUP_TA_2_20_TA_4_40_KM() {
		return AGRUP_TA_2_20_TA_4_40_KM;
	}
	public void setAGRUP_TA_2_20_TA_4_40_KM(String aGRUP_TA_2_20_TA_4_40_KM) {
		AGRUP_TA_2_20_TA_4_40_KM = aGRUP_TA_2_20_TA_4_40_KM;
	}
	public String getAGRUP_TA_4_40_TA_5_50_KM() {
		return AGRUP_TA_4_40_TA_5_50_KM;
	}
	public void setAGRUP_TA_4_40_TA_5_50_KM(String aGRUP_TA_4_40_TA_5_50_KM) {
		AGRUP_TA_4_40_TA_5_50_KM = aGRUP_TA_4_40_TA_5_50_KM;
	}
	public String getAGRUP_TA_5_50_TA_9_90_KM() {
		return AGRUP_TA_5_50_TA_9_90_KM;
	}
	public void setAGRUP_TA_5_50_TA_9_90_KM(String aGRUP_TA_5_50_TA_9_90_KM) {
		AGRUP_TA_5_50_TA_9_90_KM = aGRUP_TA_5_50_TA_9_90_KM;
	}
	public String getAGRUP_TA_9_90_TA_20_35_KM() {
		return AGRUP_TA_9_90_TA_20_35_KM;
	}
	public void setAGRUP_TA_9_90_TA_20_35_KM(String aGRUP_TA_9_90_TA_20_35_KM) {
		AGRUP_TA_9_90_TA_20_35_KM = aGRUP_TA_9_90_TA_20_35_KM;
	}
	public String getAGRUP_TA_20_35_TA_34_65_KM() {
		return AGRUP_TA_20_35_TA_34_65_KM;
	}
	public void setAGRUP_TA_20_35_TA_34_65_KM(String aGRUP_TA_20_35_TA_34_65_KM) {
		AGRUP_TA_20_35_TA_34_65_KM = aGRUP_TA_20_35_TA_34_65_KM;
	}
	public String getTA_MAY_34_65_KM() {
		return TA_MAY_34_65_KM;
	}
	public void setTA_MAY_34_65_KM(String tA_MAY_34_65_KM) {
		TA_MAY_34_65_KM = tA_MAY_34_65_KM;
	}
	public String getMax_ACC_CONGESTION_CS_GSM() {
		return max_ACC_CONGESTION_CS_GSM;
	}
	public void setMax_ACC_CONGESTION_CS_GSM(String max_ACC_CONGESTION_CS_GSM) {
		this.max_ACC_CONGESTION_CS_GSM = max_ACC_CONGESTION_CS_GSM;
	}
	public String getMin_ACC_CONGESTION_CS_GSM() {
		return min_ACC_CONGESTION_CS_GSM;
	}
	public void setMin_ACC_CONGESTION_CS_GSM(String min_ACC_CONGESTION_CS_GSM) {
		this.min_ACC_CONGESTION_CS_GSM = min_ACC_CONGESTION_CS_GSM;
	}
	public String getMax_ACC_PS_GSM() {
		return max_ACC_PS_GSM;
	}
	public void setMax_ACC_PS_GSM(String max_ACC_PS_GSM) {
		this.max_ACC_PS_GSM = max_ACC_PS_GSM;
	}
	public String getMin_ACC_PS_GSM() {
		return min_ACC_PS_GSM;
	}
	public void setMin_ACC_PS_GSM(String min_ACC_PS_GSM) {
		this.min_ACC_PS_GSM = min_ACC_PS_GSM;
	}
	public String getMax_ACC_CONG_RAD_PS_GSM() {
		return max_ACC_CONG_RAD_PS_GSM;
	}
	public void setMax_ACC_CONG_RAD_PS_GSM(String max_ACC_CONG_RAD_PS_GSM) {
		this.max_ACC_CONG_RAD_PS_GSM = max_ACC_CONG_RAD_PS_GSM;
	}
	public String getMin_ACC_CONG_RAD_PS_GSM() {
		return min_ACC_CONG_RAD_PS_GSM;
	}
	public void setMin_ACC_CONG_RAD_PS_GSM(String min_ACC_CONG_RAD_PS_GSM) {
		this.min_ACC_CONG_RAD_PS_GSM = min_ACC_CONG_RAD_PS_GSM;
	}	
	public String getMax_RET_GSM() {
		return max_RET_GSM;
	}
	public void setMax_RET_GSM(String max_RET_GSM) {
		this.max_RET_GSM = max_RET_GSM;
	}
	public String getMin_RET_GSM() {
		return min_RET_GSM;
	}
	public void setMin_RET_GSM(String min_RET_GSM) {
		this.min_RET_GSM = min_RET_GSM;
	}
	public String getMax_RET_CS_GSM() {
		return max_RET_CS_GSM;
	}
	public void setMax_RET_CS_GSM(String max_RET_CS_GSM) {
		this.max_RET_CS_GSM = max_RET_CS_GSM;
	}
	public String getMin_RET_CS_GSM() {
		return min_RET_CS_GSM;
	}
	public void setMin_RET_CS_GSM(String min_RET_CS_GSM) {
		this.min_RET_CS_GSM = min_RET_CS_GSM;
	}
	public String getMax_RET_PS_GSM() {
		return max_RET_PS_GSM;
	}
	public void setMax_RET_PS_GSM(String max_RET_PS_GSM) {
		this.max_RET_PS_GSM = max_RET_PS_GSM;
	}
	public String getMin_RET_PS_GSM() {
		return min_RET_PS_GSM;
	}
	public void setMin_RET_PS_GSM(String min_RET_PS_GSM) {
		this.min_RET_PS_GSM = min_RET_PS_GSM;
	}	
	public String getMax_SCDCH_GSM() {
		return max_SCDCH_GSM;
	}
	public void setMax_SCDCH_GSM(String max_SCDCH_GSM) {
		this.max_SCDCH_GSM = max_SCDCH_GSM;
	}
	public String getMin_SCDCH_GSM() {
		return min_SCDCH_GSM;
	}
	public void setMin_SCDCH_GSM(String min_SCDCH_GSM) {
		this.min_SCDCH_GSM = min_SCDCH_GSM;
	}
	public String getMax_SCDCH_DROP_GSM() {
		return max_SCDCH_DROP_GSM;
	}
	public void setMax_SCDCH_DROP_GSM(String max_SCDCH_DROP_GSM) {
		this.max_SCDCH_DROP_GSM = max_SCDCH_DROP_GSM;
	}
	public String getMin_SCDCH_DROP_GSM() {
		return min_SCDCH_DROP_GSM;
	}
	public void setMin_SCDCH_DROP_GSM(String min_SCDCH_DROP_GSM) {
		this.min_SCDCH_DROP_GSM = min_SCDCH_DROP_GSM;
	}
	public String getMax_SCDCH_BLOCK_GSM() {
		return max_SCDCH_BLOCK_GSM;
	}
	public void setMax_SCDCH_BLOCK_GSM(String max_SCDCH_BLOCK_GSM) {
		this.max_SCDCH_BLOCK_GSM = max_SCDCH_BLOCK_GSM;
	}
	public String getMin_SCDCH_BLOCK_GSM() {
		return min_SCDCH_BLOCK_GSM;
	}
	public void setMin_SCDCH_BLOCK_GSM(String min_SCDCH_BLOCK_GSM) {
		this.min_SCDCH_BLOCK_GSM = min_SCDCH_BLOCK_GSM;
	}
	public String getMax_TCH_DENIED_GSM() {
		return max_TCH_DENIED_GSM;
	}
	public void setMax_TCH_DENIED_GSM(String max_TCH_DENIED_GSM) {
		this.max_TCH_DENIED_GSM = max_TCH_DENIED_GSM;
	}
	public String getMin_TCH_DENIED_GSM() {
		return min_TCH_DENIED_GSM;
	}
	public void setMin_TCH_DENIED_GSM(String min_TCH_DENIED_GSM) {
		this.min_TCH_DENIED_GSM = min_TCH_DENIED_GSM;
	}
	public String getMax_TRAFICO_DATOS_GSM() {
		return max_TRAFICO_DATOS_GSM;
	}
	public void setMax_TRAFICO_DATOS_GSM(String max_TRAFICO_DATOS_GSM) {
		this.max_TRAFICO_DATOS_GSM = max_TRAFICO_DATOS_GSM;
	}
	public String getMin_TRAFICO_DATOS_GSM() {
		return min_TRAFICO_DATOS_GSM;
	}
	public void setMin_TRAFICO_DATOS_GSM(String min_TRAFICO_DATOS_GSM) {
		this.min_TRAFICO_DATOS_GSM = min_TRAFICO_DATOS_GSM;
	}
	public String getMax_TRAFICO_VOZ_GSM() {
		return max_TRAFICO_VOZ_GSM;
	}
	public void setMax_TRAFICO_VOZ_GSM(String max_TRAFICO_VOZ_GSM) {
		this.max_TRAFICO_VOZ_GSM = max_TRAFICO_VOZ_GSM;
	}
	public String getMin_TRAFICO_VOZ_GSM() {
		return min_TRAFICO_VOZ_GSM;
	}
	public void setMin_TRAFICO_VOZ_GSM(String min_TRAFICO_VOZ_GSM) {
		this.min_TRAFICO_VOZ_GSM = min_TRAFICO_VOZ_GSM;
	}
	public String getMax_TRAFICO_ERL_GSM() {
		return max_TRAFICO_ERL_GSM;
	}
	public void setMax_TRAFICO_ERL_GSM(String max_TRAFICO_ERL_GSM) {
		this.max_TRAFICO_ERL_GSM = max_TRAFICO_ERL_GSM;
	}
	public String getMin_TRAFICO_ERL_GSM() {
		return min_TRAFICO_ERL_GSM;
	}
	public void setMin_TRAFICO_ERL_GSM(String min_TRAFICO_ERL_GSM) {
		this.min_TRAFICO_ERL_GSM = min_TRAFICO_ERL_GSM;
	}
	public String getMAX_TA_GMS_AGRUP() {
		return MAX_TA_GMS_AGRUP;
	}
	public void setMAX_TA_GMS_AGRUP(String mAX_TA_GMS_AGRUP) {
		MAX_TA_GMS_AGRUP = mAX_TA_GMS_AGRUP;
	}
	public String getMIN_TA_GMS_AGRUP() {
		return MIN_TA_GMS_AGRUP;
	}
	public void setMIN_TA_GMS_AGRUP(String mIN_TA_GMS_AGRUP) {
		MIN_TA_GMS_AGRUP = mIN_TA_GMS_AGRUP;
	}
	/*get and set KPI 3G*/
	public String getAcc_cs() {
		return acc_cs;
	}	
	public void setAcc_cs(String acc_cs) {
		this.acc_cs = acc_cs;
	}
	public String getAcc_ps() {
		return acc_ps;
	}
	public void setAcc_ps(String acc_ps) {
		this.acc_ps = acc_ps;
	}
	public String getRet_cs() {
		return ret_cs;
	}
	public void setRet_cs(String ret_cs) {
		this.ret_cs = ret_cs;
	}
	public String getRet_ps() {
		return ret_ps;
	}
	public void setRet_ps(String ret_ps) {
		this.ret_ps = ret_ps;
	}
	public String getThrouput_dl() {
		return throuput_dl;
	}
	public void setThrouput_dl(String throuput_dl) {
		this.throuput_dl = throuput_dl;
	}
	public String getDch_users() {
		return dch_users;
	}
	public void setDch_users(String dch_users) {
		this.dch_users = dch_users;
	}
	public String getTrafico_voz_erlang() {
		return trafico_voz_erlang;
	}
	public void setTrafico_voz_erlang(String trafico_voz_erlang) {
		this.trafico_voz_erlang = trafico_voz_erlang;
	}
	public String getTrafico_datos_dl() {
		return trafico_datos_dl;
	}
	public void setTrafico_datos_dl(String trafico_datos_dl) {
		this.trafico_datos_dl = trafico_datos_dl;
	}
	public String getTrafico_datos_ul() {
		return trafico_datos_ul;
	}
	public void setTrafico_datos_ul(String trafico_datos_ul) {
		this.trafico_datos_ul = trafico_datos_ul;
	}
	public String getThrouput_ul() {
		return throuput_ul;
	}
	public void setThrouput_ul(String throuput_ul) {
		this.throuput_ul = throuput_ul;
	}
	public String getVs_tp_ue_0() {
		return vs_tp_ue_0;
	}
	public void setVs_tp_ue_0(String vs_tp_ue_0) {
		this.vs_tp_ue_0 = vs_tp_ue_0;
	}
	public String getVs_tp_ue_1() {
		return vs_tp_ue_1;
	}
	public void setVs_tp_ue_1(String vs_tp_ue_1) {
		this.vs_tp_ue_1 = vs_tp_ue_1;
	}
	public String getVs_tp_ue_2() {
		return vs_tp_ue_2;
	}
	public void setVs_tp_ue_2(String vs_tp_ue_2) {
		this.vs_tp_ue_2 = vs_tp_ue_2;
	}
	public String getVs_tp_ue_3() {
		return vs_tp_ue_3;
	}
	public void setVs_tp_ue_3(String vs_tp_ue_3) {
		this.vs_tp_ue_3 = vs_tp_ue_3;
	}
	public String getVs_tp_ue_4() {
		return vs_tp_ue_4;
	}
	public void setVs_tp_ue_4(String vs_tp_ue_4) {
		this.vs_tp_ue_4 = vs_tp_ue_4;
	}
	public String getVs_tp_ue_5() {
		return vs_tp_ue_5;
	}
	public void setVs_tp_ue_5(String vs_tp_ue_5) {
		this.vs_tp_ue_5 = vs_tp_ue_5;
	}
	public String getVs_tp_ue_6_9() {
		return vs_tp_ue_6_9;
	}
	public void setVs_tp_ue_6_9(String vs_tp_ue_6_9) {
		this.vs_tp_ue_6_9 = vs_tp_ue_6_9;
	}
	public String getVs_tp_ue_10_15() {
		return vs_tp_ue_10_15;
	}
	public void setVs_tp_ue_10_15(String vs_tp_ue_10_15) {
		this.vs_tp_ue_10_15 = vs_tp_ue_10_15;
	}
	public String getVs_tp_ue_16_25() {
		return vs_tp_ue_16_25;
	}
	public void setVs_tp_ue_16_25(String vs_tp_ue_16_25) {
		this.vs_tp_ue_16_25 = vs_tp_ue_16_25;
	}
	public String getVs_tp_ue_26_35() {
		return vs_tp_ue_26_35;
	}
	public void setVs_tp_ue_26_35(String vs_tp_ue_26_35) {
		this.vs_tp_ue_26_35 = vs_tp_ue_26_35;
	}
	public String getVs_tp_ue_36_55() {
		return vs_tp_ue_36_55;
	}
	public void setVs_tp_ue_36_55(String vs_tp_ue_36_55) {
		this.vs_tp_ue_36_55 = vs_tp_ue_36_55;
	}
	public String getVs_tp_ue_more55() {
		return vs_tp_ue_more55;
	}
	public void setVs_tp_ue_more55(String vs_tp_ue_more55) {
		this.vs_tp_ue_more55 = vs_tp_ue_more55;
	}
	
	/*get and set KPI 3G MAX AND MIN*/
	public String getMax_acc() {
		return max_acc;
	}
	public void setMax_acc(String max_acc) {
		this.max_acc = max_acc;
	}
	public String getMin_acc() {
		return min_acc;
	}
	public void setMin_acc(String min_acc) {
		this.min_acc = min_acc;
	}
	public String getMax_ret() {
		return max_ret;
	}
	public void setMax_ret(String max_ret) {
		this.max_ret = max_ret;
	}
	public String getMin_ret() {
		return min_ret;
	}
	public void setMin_ret(String min_ret) {
		this.min_ret = min_ret;
	}
	public String getMax_throuput() {
		return max_throuput;
	}
	public void setMax_throuput(String max_throuput) {
		this.max_throuput = max_throuput;
	}
	public String getMin_throuput() {
		return min_throuput;
	}
	public void setMin_throuput(String min_throuput) {
		this.min_throuput = min_throuput;
	}
	public String getMax_dch_users() {
		return max_dch_users;
	}
	public void setMax_dch_users(String max_dch_users) {
		this.max_dch_users = max_dch_users;
	}
	public String getMin_dch_users() {
		return min_dch_users;
	}
	public void setMin_dch_users(String min_dch_users) {
		this.min_dch_users = min_dch_users;
	}
	public String getMax_trafico_voz_erlang() {
		return max_trafico_voz_erlang;
	}
	public void setMax_trafico_voz_erlang(String max_trafico_voz_erlang) {
		this.max_trafico_voz_erlang = max_trafico_voz_erlang;
	}
	public String getMin_trafico_voz_erlang() {
		return min_trafico_voz_erlang;
	}
	public void setMin_trafico_voz_erlang(String min_trafico_voz_erlang) {
		this.min_trafico_voz_erlang = min_trafico_voz_erlang;
	}
	public String getMax_trafico_datos() {
		return max_trafico_datos;
	}
	public void setMax_trafico_datos(String max_trafico_datos) {
		this.max_trafico_datos = max_trafico_datos;
	}
	public String getMin_trafico_datos() {
		return min_trafico_datos;
	}
	public void setMin_trafico_datos(String min_trafico_datos) {
		this.min_trafico_datos = min_trafico_datos;
	}
	public String getMax_vs_tp_ue() {
		return max_vs_tp_ue;
	}
	public void setMax_vs_tp_ue(String max_vs_tp_ue) {
		this.max_vs_tp_ue = max_vs_tp_ue;
	}
	public String getMin_vs_tp_ue() {
		return min_vs_tp_ue;
	}
	public void setMin_vs_tp_ue(String min_vs_tp_ue) {
		this.min_vs_tp_ue = min_vs_tp_ue;
	}
	
	/**/
	/*get and set KPI 4G*/
	public String getPrb_dl() {
		return prb_dl;
	}	
	public void setPrb_dl(String prb_dl) {
		this.prb_dl = prb_dl;
	}	
	public String getPrb_ul() {
		return prb_ul;
	}
	public void setPrb_ul(String prb_ul) {
		this.prb_ul = prb_ul;
	}	
	public String getAccessibility() {
		return accessibility;
	}
	public void setAccessibility(String accessibility) {
		this.accessibility = accessibility;
	}	
	public String getRetainability() {
		return retainability;
	}
	public void setRetainability(String retainability) {
		this.retainability = retainability;
	}
	public String getBeusers() {
		return beusers;
	}
	public void setBeusers(String beusers) {
		this.beusers = beusers;
	}
	public String getThrouput_user_dl() {
		return throuput_user_dl;
	}
	public void setThrouput_user_dl(String throuput_user_dl) {
		this.throuput_user_dl = throuput_user_dl;
	}
	public String getThrouput_user_ul() {
		return throuput_user_ul;
	}
	public void setThrouput_user_ul(String throuput_user_ul) {
		this.throuput_user_ul = throuput_user_ul;
	}
	public String getDl_traffic_vol_mb() {
		return dl_traffic_vol_mb;
	}
	public void setDl_traffic_vol_mb(String dl_traffic_vol_mb) {
		this.dl_traffic_vol_mb = dl_traffic_vol_mb;
	}
	public String getUl_traffic_vol_mb() {
		return ul_traffic_vol_mb;
	}
	public void setUl_traffic_vol_mb(String ul_traffic_vol_mb) {
		this.ul_traffic_vol_mb = ul_traffic_vol_mb;
	}
	public String getL_ra_ta_ue_index0() {
		return l_ra_ta_ue_index0;
	}
	public void setL_ra_ta_ue_index0(String l_ra_ta_ue_index0) {
		this.l_ra_ta_ue_index0 = l_ra_ta_ue_index0;
	}
	public String getL_ra_ta_ue_index1() {
		return l_ra_ta_ue_index1;
	}
	public void setL_ra_ta_ue_index1(String l_ra_ta_ue_index1) {
		this.l_ra_ta_ue_index1 = l_ra_ta_ue_index1;
	}
	public String getL_ra_ta_ue_index2() {
		return l_ra_ta_ue_index2;
	}
	public void setL_ra_ta_ue_index2(String l_ra_ta_ue_index2) {
		this.l_ra_ta_ue_index2 = l_ra_ta_ue_index2;
	}
	public String getL_ra_ta_ue_index3() {
		return l_ra_ta_ue_index3;
	}
	public void setL_ra_ta_ue_index3(String l_ra_ta_ue_index3) {
		this.l_ra_ta_ue_index3 = l_ra_ta_ue_index3;
	}
	public String getL_ra_ta_ue_index4() {
		return l_ra_ta_ue_index4;
	}
	public void setL_ra_ta_ue_index4(String l_ra_ta_ue_index4) {
		this.l_ra_ta_ue_index4 = l_ra_ta_ue_index4;
	}
	public String getL_ra_ta_ue_index5() {
		return l_ra_ta_ue_index5;
	}
	public void setL_ra_ta_ue_index5(String l_ra_ta_ue_index5) {
		this.l_ra_ta_ue_index5 = l_ra_ta_ue_index5;
	}
	public String getL_ra_ta_ue_index6() {
		return l_ra_ta_ue_index6;
	}
	public void setL_ra_ta_ue_index6(String l_ra_ta_ue_index6) {
		this.l_ra_ta_ue_index6 = l_ra_ta_ue_index6;
	}
	public String getL_ra_ta_ue_index7() {
		return l_ra_ta_ue_index7;
	}
	public void setL_ra_ta_ue_index7(String l_ra_ta_ue_index7) {
		this.l_ra_ta_ue_index7 = l_ra_ta_ue_index7;
	}
	public String getL_ra_ta_ue_index8() {
		return l_ra_ta_ue_index8;
	}
	public void setL_ra_ta_ue_index8(String l_ra_ta_ue_index8) {
		this.l_ra_ta_ue_index8 = l_ra_ta_ue_index8;
	}
	public String getL_ra_ta_ue_index9() {
		return l_ra_ta_ue_index9;
	}
	public void setL_ra_ta_ue_index9(String l_ra_ta_ue_index9) {
		this.l_ra_ta_ue_index9 = l_ra_ta_ue_index9;
	}
	public String getL_ra_ta_ue_index10() {
		return l_ra_ta_ue_index10;
	}
	public void setL_ra_ta_ue_index10(String l_ra_ta_ue_index10) {
		this.l_ra_ta_ue_index10 = l_ra_ta_ue_index10;
	}
	public String getL_ra_ta_ue_index11() {
		return l_ra_ta_ue_index11;
	}
	public void setL_ra_ta_ue_index11(String l_ra_ta_ue_index11) {
		this.l_ra_ta_ue_index11 = l_ra_ta_ue_index11;
	}
	/**/	
	

	
	/*get and set KPI 4G MAX AND MIN*/
	public String getMax_result_time() {
		return max_result_time;
	}
	public void setMax_result_time(String max_result_time) {
		this.max_result_time = max_result_time;
	}
	public String getMin_result_time() {
		return min_result_time;
	}
	public void setMin_result_time(String min_result_time) {
		this.min_result_time = min_result_time;
	}		
	public String getMax_accessibility() {
		return max_accessibility;
	}
	public void setMax_accessibility(String max_accessibility) {
		this.max_accessibility = max_accessibility;
	}
	public String getMin_accessibility() {
		return min_accessibility;
	}
	public void setMin_accessibility(String min_accessibility) {
		this.min_accessibility = min_accessibility;
	}
	public String getMax_retainability() {
		return max_retainability;
	}
	public void setMax_retainability(String max_retainability) {
		this.max_retainability = max_retainability;
	}
	public String getMin_retainability() {
		return min_retainability;
	}
	public void setMin_retainability(String min_retainability) {
		this.min_retainability = min_retainability;
	}
	public String getMax_beusers() {
		return max_beusers;
	}
	public void setMax_beusers(String max_beusers) {
		this.max_beusers = max_beusers;
	}
	public String getMin_beusers() {
		return min_beusers;
	}
	public void setMin_beusers(String min_beusers) {
		this.min_beusers = min_beusers;
	}
	public String getMax_prb_lte() {
		return max_prb_lte;
	}
	public void setMax_prb_lte(String max_prb_lte) {
		this.max_prb_lte = max_prb_lte;
	}
	public String getMin_prb_lte() {
		return min_prb_lte;
	}
	public void setMin_prb_lte(String min_prb_lte) {
		this.min_prb_lte = min_prb_lte;
	}
	public String getMax_throuput_user_lte() {
		return max_throuput_user_lte;
	}
	public void setMax_throuput_user_lte(String max_throuput_user_lte) {
		this.max_throuput_user_lte = max_throuput_user_lte;
	}
	public String getMin_throuput_user_lte() {
		return min_throuput_user_lte;
	}
	public void setMin_throuput_user_lte(String min_throuput_user_lte) {
		this.min_throuput_user_lte = min_throuput_user_lte;
	}
	public String getMax_traffic_vol_mb_lte() {
		return max_traffic_vol_mb_lte;
	}
	public void setMax_traffic_vol_mb_lte(String max_traffic_vol_mb_lte) {
		this.max_traffic_vol_mb_lte = max_traffic_vol_mb_lte;
	}
	public String getMin_traffic_vol_mb_lte() {
		return min_traffic_vol_mb_lte;
	}
	public void setMin_traffic_vol_mb_lte(String min_traffic_vol_mb_lte) {
		this.min_traffic_vol_mb_lte = min_traffic_vol_mb_lte;
	}
	public String getMax_l_ra_ta_ue_index() {
		return max_l_ra_ta_ue_index;
	}
	public void setMax_l_ra_ta_ue_index(String max_l_ra_ta_ue_index) {
		this.max_l_ra_ta_ue_index = max_l_ra_ta_ue_index;
	}
	public String getMin_l_ra_ta_ue_index() {
		return min_l_ra_ta_ue_index;
	}
	public void setMin_l_ra_ta_ue_index(String min_l_ra_ta_ue_index) {
		this.min_l_ra_ta_ue_index = min_l_ra_ta_ue_index;
	}

	/**/



}
