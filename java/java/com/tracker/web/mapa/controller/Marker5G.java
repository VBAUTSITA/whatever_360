package com.tracker.web.mapa.controller;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Entity
//@Table(name = "vw_smart_opti_mapa_saved_test")
//@Table(name = "opti_mapa_saved_test")
@Table(name = "PLAN_5GWEEK")
public class Marker5G {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)


	private Long id;

	private String sector_name;

	private String description; //AZIMTH
	
	private Double latitude;
	private Double longitude;

	private String horizontal_beam;
	private String radio;
	
	//5G BASIC DATA
	//
	 
	private String acc_real_pct;
	private String prb_dl_pct;
	private String prb_ul_pct;
	private String avg_cqi;
	//drop ret
	//traffic dl + ul
	private String uplink_interference;
	private String user_rrc_avg;
	
	private String rete_real_pct;
	
	//TH_USER_DL_MBPS, TH_USER_UL_MBPS
	private String th_user_dl_mbps;
	private String th_user_ul_mbps;
	
	
	private String ancho_banda;
	private String trafico_dl_gb;
	private String trafico_ul_gb;
	
	private String prb_dl_n78_pct; 
	private String prb_dl_n7_pct;
	
	
	
	
	// Getters y setters

	public String getSector_name() {
		return sector_name;
	}
	public void setSector_name(String sector_name) {
		this.sector_name = sector_name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public String getHorizontal_beam() {
		return horizontal_beam;
	}
	public void setHorizontal_beam(String horizontal_beam) {
		this.horizontal_beam = horizontal_beam;
	}
	public String getRadio() {
		return radio;
	}
	public void setRadio(String radio) {
		this.radio = radio;
	}
	public String getAcc_real_pct() {
		return acc_real_pct;
	}
	public void setAcc_real_pct(String acc_real_pct) {
		this.acc_real_pct = acc_real_pct;
	}
	public String getPrb_ul_pct() {
		return prb_ul_pct;
	}
	public void setPrb_ul_pct(String prb_ul_pct) {
		this.prb_ul_pct = prb_ul_pct;
	}
	public String getPrb_dl_pct() {
		return prb_dl_pct;
	}
	public void setPrb_dl_pct(String prb_dl_pct) {
		this.prb_dl_pct = prb_dl_pct;
	}
	public String getAvg_cqi() {
		return avg_cqi;
	}
	public void setAvg_cqi(String avg_cqi) {
		this.avg_cqi = avg_cqi;
	}
	public String getUplink_interference() {
		return uplink_interference;
	}
	public void setUplink_interference(String uplink_interference) {
		this.uplink_interference = uplink_interference;
	}
	public String getUser_rrc_avg() {
		return user_rrc_avg;
	}
	public void setUser_rrc_avg(String user_rrc_avg) {
		this.user_rrc_avg = user_rrc_avg;
	}
	public String getRete_real_pct() {
		return rete_real_pct;
	}
	public void setRete_real_pct(String rete_real_pct) {
		this.rete_real_pct = rete_real_pct;
	}
	public String getTh_user_dl_mbps() {
		return th_user_dl_mbps;
	}
	public void setTh_user_dl_mbps(String th_user_dl_mbps) {
		this.th_user_dl_mbps = th_user_dl_mbps;
	}
	public String getTh_user_ul_mbps() {
		return th_user_ul_mbps;
	}
	public void setTh_user_ul_mbps(String th_user_ul_mbps) {
		this.th_user_ul_mbps = th_user_ul_mbps;
	}
	public String getAncho_banda() {
		return ancho_banda;
	}
	public void setAncho_banda(String ancho_banda) {
		this.ancho_banda = ancho_banda;
	}
	public String getTrafico_ul_gb() {
		return trafico_ul_gb;
	}
	public void setTrafico_ul_gb(String trafico_ul_gb) {
		this.trafico_ul_gb = trafico_ul_gb;
	}
	public String getTrafico_dl_gb() {
		return trafico_dl_gb;
	}
	public void setTrafico_dl_gb(String trafico_dl_gb) {
		this.trafico_dl_gb = trafico_dl_gb;
	}
	public String getPrb_dl_n78_pct() {
		return prb_dl_n78_pct;
	}
	public void setPrb_dl_n78_pct(String prb_dl_n78_pct) {
		this.prb_dl_n78_pct = prb_dl_n78_pct;
	}
	public String getPrb_dl_n7_pct() {
		return prb_dl_n7_pct;
	}
	public void setPrb_dl_n7_pct(String prb_dl_n7_pct) {
		this.prb_dl_n7_pct = prb_dl_n7_pct;
	}

	

	
	

}