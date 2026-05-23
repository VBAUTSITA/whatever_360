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
@Table(name = "PLAN_MAP_SECT_CRIT_4GWEEK")
public class Marker {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;
	private String name;
	private String sector_name;
	private String sector_address;
	private String enodob_name;
	private String enodob_address;
	//tag starlink
	private String es_starlink;
	
	private String description;
	private String altura;
	private String txrx700;
	private String txrx1900;
	private String txrx2600;
	private String txrxb38;
	private String txrx850; //new
	private String th_user_dl;
	private String prb_dl;
	private String dl_scheduling_rate;
	private String prioridad_sector;
	private String high_load_cellhr;
	private String low_th_dl_cellhr;
	private String total_high_load;
	private String total_low_th;
	private String total_cellhr;
	private String mac_volume_num_v2;
	private String meta_trafico_v2;
	private String ta_0_234_mts;
	private String ta_234_546_mts;
	private String ta_546_1014_mts;
	private String ta_1014_1950_mts;
	private String ta_1950_3510_mts;
	private String ta_3510_6630_mts;
	private String ta_6630_14430_mts;
	private String ta_mayor_14430_mts;
	private String dl_prb_util_700;
	private String dl_prb_util_1900;
	private String dl_prb_util_2600;
	private String dl_prb_util_2600_b38;
	private String dl_prb_util_850;
	private String thr_user_dl_700;
	private String thr_user_dl_1900;
	private String thr_user_dl_2600;
	private String thr_user_dl_2600_b38;
	private String thr_user_dl_850;
	private String usuarios_ifis;
	private String usuarios_tdds;
	private String electrical_tilt_700;
	private String electrical_tilt_1900;
	private String electrical_tilt_2600;
	private String electrica_tilt_b38;
	private String electrical_tilt_850;
	private String mechanical_tilt;
	private String antena;
	private String tipo_antena;
	private String jefatura_radio;
	private String region;
	private String sub_region;
	private String departamento;
	private String provincia;
	private String distrito;
	private Double latitude;
	private Double longitude;
	private String anio;
	private String semana;
	private String horizontal_beam;
	private String radio;
	private String region_filter;
	
	//5G BASIC DATA
	
	private String tiene_5g;
	private String prb_dl_5g;
	
	// Campos nuevos provenientes de marker_v2
	private Long req;
	private String proyecto;
	private String sub_proyecto;
	private String estado_req;
	private String f_aprobacion_req;
	private String gerencia_solicitante;
	private String nombre_requerimiento;

	private Long ot;
	private String estado_ot;
	private String tipo_trabajo;
	private String estado_req_trabajo;
	private String estado_trabajo;
	private Long req_trabajo;
	private String tipo_trabajo_hist;
	
	//ultimos campos
	private String tipo;
	private String estado_constuccion_ot;

	
	//ultimos campos V2
	private String alta_existe;
	private Long  alta_req;
	private Long  alta_ot;
	private String  alta_estado_ot;

	private String alta_proyecto;
	private String alta_nombre_req;
	private String alta_f_aprobacion_req;
	
	///NUEVOS CAMPOS
	/// 
	/// 
	private Long poblacion;
	private String trafico_terminales_5g;
	private String tipo_fo;
	private String req_4g;
	private String ro_nat;
	private String req_5g;
	private String posible5g;
	private String trafico;
	private String forecast;
	//private String habilitado;
	private Long ranking;
	private String site_name;
	
	//NUEVOS PARTE 2
	
	private String b2_b7;
	private String trafico_sector;
	private String infraestructura;
	private String con_capex;
	
	
	
	
	
	// Getters y setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSector_name() {
		return sector_name;
	}
	public void setSector_name(String sector_name) {
		this.sector_name = sector_name;
	}
	public String getSector_address() {
		return sector_address;
	}
	public void setSector_address(String sector_address) {
		this.sector_address = sector_address;
	}
	public String getEnodob_name() {
		return enodob_name;
	}
	public void setEnodob_name(String enodob_name) {
		this.enodob_name = enodob_name;
	}
	public String getEnodob_address() {
		return enodob_address;
	}
	public void setEnodob_address(String enodob_address) {
		this.enodob_address = enodob_address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAltura() {
		return altura;
	}
	public void setAltura(String altura) {
		this.altura = altura;
	}
	public String getTxrx700() {
		return txrx700;
	}
	public void setTxrx700(String txrx700) {
		this.txrx700 = txrx700;
	}
	public String getTxrx1900() {
		return txrx1900;
	}
	public void setTxrx1900(String txrx1900) {
		this.txrx1900 = txrx1900;
	}
	public String getTxrx2600() {
		return txrx2600;
	}
	public void setTxrx2600(String txrx2600) {
		this.txrx2600 = txrx2600;
	}
	public String getTxrxb38() {
		return txrxb38;
	}
	public void setTxrxb38(String txrxb38) {
		this.txrxb38 = txrxb38;
	}
	public String getTh_user_dl() {
		return th_user_dl;
	}
	public void setTh_user_dl(String th_user_dl) {
		this.th_user_dl = th_user_dl;
	}
	public String getPrb_dl() {
		return prb_dl;
	}
	public void setPrb_dl(String prb_dl) {
		this.prb_dl = prb_dl;
	}
	public String getDl_scheduling_rate() {
		return dl_scheduling_rate;
	}
	public void setDl_scheduling_rate(String dl_scheduling_rate) {
		this.dl_scheduling_rate = dl_scheduling_rate;
	}
	public String getPrioridad_sector() {
		return prioridad_sector;
	}
	public void setPrioridad_sector(String prioridad_sector) {
		this.prioridad_sector = prioridad_sector;
	}
	public String getHigh_load_cellhr() {
		return high_load_cellhr;
	}
	public void setHigh_load_cellhr(String high_load_cellhr) {
		this.high_load_cellhr = high_load_cellhr;
	}
	public String getLow_th_dl_cellhr() {
		return low_th_dl_cellhr;
	}
	public void setLow_th_dl_cellhr(String low_th_dl_cellhr) {
		this.low_th_dl_cellhr = low_th_dl_cellhr;
	}
	public String getTotal_high_load() {
		return total_high_load;
	}
	public void setTotal_high_load(String total_high_load) {
		this.total_high_load = total_high_load;
	}
	public String getTotal_low_th() {
		return total_low_th;
	}
	public void setTotal_low_th(String total_low_th) {
		this.total_low_th = total_low_th;
	}
	public String getTotal_cellhr() {
		return total_cellhr;
	}
	public void setTotal_cellhr(String total_cellhr) {
		this.total_cellhr = total_cellhr;
	}
	public String getMac_volume_num_v2() {
		return mac_volume_num_v2;
	}
	public void setMac_volume_num_v2(String mac_volume_num_v2) {
		this.mac_volume_num_v2 = mac_volume_num_v2;
	}
	public String getMeta_trafico_v2() {
		return meta_trafico_v2;
	}
	public void setMeta_trafico_v2(String meta_trafico_v2) {
		this.meta_trafico_v2 = meta_trafico_v2;
	}
	public String getTa_0_234_mts() {
		return ta_0_234_mts;
	}
	public void setTa_0_234_mts(String ta_0_234_mts) {
		this.ta_0_234_mts = ta_0_234_mts;
	}
	public String getTa_234_546_mts() {
		return ta_234_546_mts;
	}
	public void setTa_234_546_mts(String ta_234_546_mts) {
		this.ta_234_546_mts = ta_234_546_mts;
	}
	public String getTa_546_1014_mts() {
		return ta_546_1014_mts;
	}
	public void setTa_546_1014_mts(String ta_546_1014_mts) {
		this.ta_546_1014_mts = ta_546_1014_mts;
	}
	public String getTa_1014_1950_mts() {
		return ta_1014_1950_mts;
	}
	public void setTa_1014_1950_mts(String ta_1014_1950_mts) {
		this.ta_1014_1950_mts = ta_1014_1950_mts;
	}
	public String getTa_1950_3510_mts() {
		return ta_1950_3510_mts;
	}
	public void setTa_1950_3510_mts(String ta_1950_3510_mts) {
		this.ta_1950_3510_mts = ta_1950_3510_mts;
	}
	public String getTa_3510_6630_mts() {
		return ta_3510_6630_mts;
	}
	public void setTa_3510_6630_mts(String ta_3510_6630_mts) {
		this.ta_3510_6630_mts = ta_3510_6630_mts;
	}
	public String getTa_6630_14430_mts() {
		return ta_6630_14430_mts;
	}
	public void setTa_6630_14430_mts(String ta_6630_14430_mts) {
		this.ta_6630_14430_mts = ta_6630_14430_mts;
	}
	public String getTa_mayor_14430_mts() {
		return ta_mayor_14430_mts;
	}
	public void setTa_mayor_14430_mts(String ta_mayor_14430_mts) {
		this.ta_mayor_14430_mts = ta_mayor_14430_mts;
	}
	public String getDl_prb_util_700() {
		return dl_prb_util_700;
	}
	public void setDl_prb_util_700(String dl_prb_util_700) {
		this.dl_prb_util_700 = dl_prb_util_700;
	}
	public String getDl_prb_util_1900() {
		return dl_prb_util_1900;
	}
	public void setDl_prb_util_1900(String dl_prb_util_1900) {
		this.dl_prb_util_1900 = dl_prb_util_1900;
	}
	public String getDl_prb_util_2600() {
		return dl_prb_util_2600;
	}
	public void setDl_prb_util_2600(String dl_prb_util_2600) {
		this.dl_prb_util_2600 = dl_prb_util_2600;
	}
	public String getDl_prb_util_2600_b38() {
		return dl_prb_util_2600_b38;
	}
	public void setDl_prb_util_2600_b38(String dl_prb_util_2600_b38) {
		this.dl_prb_util_2600_b38 = dl_prb_util_2600_b38;
	}
	public String getThr_user_dl_700() {
		return thr_user_dl_700;
	}
	public void setThr_user_dl_700(String thr_user_dl_700) {
		this.thr_user_dl_700 = thr_user_dl_700;
	}
	public String getThr_user_dl_1900() {
		return thr_user_dl_1900;
	}
	public void setThr_user_dl_1900(String thr_user_dl_1900) {
		this.thr_user_dl_1900 = thr_user_dl_1900;
	}
	public String getThr_user_dl_2600() {
		return thr_user_dl_2600;
	}
	public void setThr_user_dl_2600(String thr_user_dl_2600) {
		this.thr_user_dl_2600 = thr_user_dl_2600;
	}
	public String getThr_user_dl_2600_b38() {
		return thr_user_dl_2600_b38;
	}
	public void setThr_user_dl_2600_b38(String thr_user_dl_2600_b38) {
		this.thr_user_dl_2600_b38 = thr_user_dl_2600_b38;
	}
	public String getUsuarios_ifis() {
		return usuarios_ifis;
	}
	public void setUsuarios_ifis(String usuarios_ifis) {
		this.usuarios_ifis = usuarios_ifis;
	}
	public String getUsuarios_tdds() {
		return usuarios_tdds;
	}
	public void setUsuarios_tdds(String usuarios_tdds) {
		this.usuarios_tdds = usuarios_tdds;
	}
	public String getElectrical_tilt_700() {
		return electrical_tilt_700;
	}
	public void setElectrical_tilt_700(String electrical_tilt_700) {
		this.electrical_tilt_700 = electrical_tilt_700;
	}
	public String getElectrical_tilt_1900() {
		return electrical_tilt_1900;
	}
	public void setElectrical_tilt_1900(String electrical_tilt_1900) {
		this.electrical_tilt_1900 = electrical_tilt_1900;
	}
	public String getElectrical_tilt_2600() {
		return electrical_tilt_2600;
	}
	public void setElectrical_tilt_2600(String electrical_tilt_2600) {
		this.electrical_tilt_2600 = electrical_tilt_2600;
	}
	public String getElectrica_tilt_b38() {
		return electrica_tilt_b38;
	}
	public void setElectrica_tilt_b38(String electrica_tilt_b38) {
		this.electrica_tilt_b38 = electrica_tilt_b38;
	}
	public String getMechanical_tilt() {
		return mechanical_tilt;
	}
	public void setMechanical_tilt(String mechanical_tilt) {
		this.mechanical_tilt = mechanical_tilt;
	}
	public String getAntena() {
		return antena;
	}
	public void setAntena(String antena) {
		this.antena = antena;
	}
	public String getTipo_antena() {
		return tipo_antena;
	}
	public void setTipo_antena(String tipo_antena) {
		this.tipo_antena = tipo_antena;
	}
	public String getJefatura_radio() {
		return jefatura_radio;
	}
	public void setJefatura_radio(String jefatura_radio) {
		this.jefatura_radio = jefatura_radio;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getSub_region() {
		return sub_region;
	}
	public void setSub_region(String sub_region) {
		this.sub_region = sub_region;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
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
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getSemana() {
		return semana;
	}
	public void setSemana(String semana) {
		this.semana = semana;
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
	public String getRegion_filter() {
		return region_filter;
	}
	public void setRegion_filter(String region_filter) {
		this.region_filter = region_filter;
	}
	public String getTxrx850() {
		return txrx850;
	}
	public void setTxrx850(String txrx850) {
		this.txrx850 = txrx850;
	}
	public String getDl_prb_util_850() {
		return dl_prb_util_850;
	}
	public void setDl_prb_util_850(String dl_prb_util_850) {
		this.dl_prb_util_850 = dl_prb_util_850;
	}
	public String getThr_user_dl_850() {
		return thr_user_dl_850;
	}
	public void setThr_user_dl_850(String thr_user_dl_850) {
		this.thr_user_dl_850 = thr_user_dl_850;
	}
	public String getElectrical_tilt_850() {
		return electrical_tilt_850;
	}
	public void setElectrical_tilt_850(String electrical_tilt_850) {
		this.electrical_tilt_850 = electrical_tilt_850;
	}

	
	
	public Long getReq() {
	    return req;
	}

	public void setReq(Long req) {
	    this.req = req;
	}

	public String getProyecto() {
	    return proyecto;
	}

	public void setProyecto(String proyecto) {
	    this.proyecto = proyecto;
	}

	public String getSub_proyecto() {
	    return sub_proyecto;
	}

	public void setSub_proyecto(String sub_proyecto) {
	    this.sub_proyecto = sub_proyecto;
	}

	public String getEstado_req() {
	    return estado_req;
	}

	public void setEstado_req(String estado_req) {
	    this.estado_req = estado_req;
	}

	public String getF_aprobacion_req() {
	    return f_aprobacion_req;
	}

	public void setF_aprobacion_req(String f_aprobacion_req) {
	    this.f_aprobacion_req = f_aprobacion_req;
	}

	public String getGerencia_solicitante() {
	    return gerencia_solicitante;
	}

	public void setGerencia_solicitante(String gerencia_solicitante) {
	    this.gerencia_solicitante = gerencia_solicitante;
	}

	public String getNombre_requerimiento() {
	    return nombre_requerimiento;
	}

	public void setNombre_requerimiento(String nombre_requerimiento) {
	    this.nombre_requerimiento = nombre_requerimiento;
	}

	public Long getOt() {
	    return ot;
	}

	public void setOt(Long ot) {
	    this.ot = ot;
	}

	public String getEstado_ot() {
	    return estado_ot;
	}

	public void setEstado_ot(String estado_ot) {
	    this.estado_ot = estado_ot;
	}

	public String getTipo_trabajo() {
	    return tipo_trabajo;
	}

	public void setTipo_trabajo(String tipo_trabajo) {
	    this.tipo_trabajo = tipo_trabajo;
	}

	public String getEstado_req_trabajo() {
	    return estado_req_trabajo;
	}

	public void setEstado_req_trabajo(String estado_req_trabajo) {
	    this.estado_req_trabajo = estado_req_trabajo;
	}

	public String getEstado_trabajo() {
	    return estado_trabajo;
	}

	public void setEstado_trabajo(String estado_trabajo) {
	    this.estado_trabajo = estado_trabajo;
	}

	public Long getReq_trabajo() {
	    return req_trabajo;
	}

	public void setReq_trabajo(Long req_trabajo) {
	    this.req_trabajo = req_trabajo;
	}

	public String getTipo_trabajo_hist() {
	    return tipo_trabajo_hist;
	}

	public void setTipo_trabajo_hist(String tipo_trabajo_hist) {
	    this.tipo_trabajo_hist = tipo_trabajo_hist;
	}
	public String getTiene_5g() {
		return tiene_5g;
	}
	public void setTiene_5g(String tiene_5g) {
		this.tiene_5g = tiene_5g;
	}
	public String getPrb_dl_5g() {
		return prb_dl_5g;
	}
	public void setPrb_dl_5g(String prb_dl_5g) {
		this.prb_dl_5g = prb_dl_5g;
	}
	public String getEs_starlink() {
		return es_starlink;
	}
	public void setEs_starlink(String es_starlink) {
		this.es_starlink = es_starlink;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getEstado_constuccion_ot() {
		return estado_constuccion_ot;
	}
	public void setEstado_constuccion_ot(String estado_constuccion_ot) {
		this.estado_constuccion_ot = estado_constuccion_ot;
	}
	public String getAlta_existe() {
		return alta_existe;
	}
	public void setAlta_existe(String alta_existe) {
		this.alta_existe = alta_existe;
	}
	public Long getAlta_ot() {
		return alta_ot;
	}
	public void setAlta_ot(Long alta_ot) {
		this.alta_ot = alta_ot;
	}
	public Long getAlta_req() {
		return alta_req;
	}
	public void setAlta_req(Long alta_req) {
		this.alta_req = alta_req;
	}
	public String getAlta_estado_ot() {
		return alta_estado_ot;
	}
	public void setAlta_estado_ot(String alta_estado_ot) {
		this.alta_estado_ot = alta_estado_ot;
	}
	public String getAlta_proyecto() {
		return alta_proyecto;
	}
	public void setAlta_proyecto(String alta_proyecto) {
		this.alta_proyecto = alta_proyecto;
	}
	public String getAlta_nombre_req() {
		return alta_nombre_req;
	}
	public void setAlta_nombre_req(String alta_nombre_req) {
		this.alta_nombre_req = alta_nombre_req;
	}
	public String getAlta_f_aprobacion_req() {
		return alta_f_aprobacion_req;
	}
	public void setAlta_f_aprobacion_req(String alta_f_aprobacion_req) {
		this.alta_f_aprobacion_req = alta_f_aprobacion_req;
	}
	
	//NUEVOS CAMPOS
	
	public Long getPoblacion() {
	    return poblacion;
	}
	public void setPoblacion(Long poblacion) {
	    this.poblacion = poblacion;
	}

	public String getTrafico_terminales_5g() {
	    return trafico_terminales_5g;
	}
	public void setTrafico_terminales_5g(String trafico_terminales_5g) {
	    this.trafico_terminales_5g = trafico_terminales_5g;
	}

	public String getTipo_fo() {
	    return tipo_fo;
	}
	public void setTipo_fo(String tipo_fo) {
	    this.tipo_fo = tipo_fo;
	}

	public String getReq_4g() {
	    return req_4g;
	}
	public void setReq_4g(String req_4g) {
	    this.req_4g = req_4g;
	}

	public String getRo_nat() {
	    return ro_nat;
	}
	public void setRo_nat(String ro_nat) {
	    this.ro_nat = ro_nat;
	}

	public String getReq_5g() {
	    return req_5g;
	}
	public void setReq_5g(String req_5g) {
	    this.req_5g = req_5g;
	}

	public String getPosible5g() {
	    return posible5g;
	}
	public void setPosible5g(String posible5g) {
	    this.posible5g = posible5g;
	}

	public String getTrafico() {
	    return trafico;
	}
	public void setTrafico(String trafico) {
	    this.trafico = trafico;
	}

	public String getForecast() {
	    return forecast;
	}
	public void setForecast(String forecast) {
	    this.forecast = forecast;
	}



	public Long getRanking() {
	    return ranking;
	}
	public void setRanking(Long ranking) {
	    this.ranking = ranking;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSitename(String site_name) {
		this.site_name = site_name;
	}
	public String getTrafico_sector() {
		return trafico_sector;
	}
	public void setTrafico_sector(String trafico_sector) {
		this.trafico_sector = trafico_sector;
	}
	public String getB2_b7() {
		return b2_b7;
	}
	public void setB2_b7(String b2_b7) {
		this.b2_b7 = b2_b7;
	}
	public String getInfraestructura() {
		return infraestructura;
	}
	public void setInfraestructura(String infraestructura) {
		this.infraestructura = infraestructura;
	}
	public String getCon_capex() {
		return con_capex;
	}
	public void setCon_capex(String con_capex) {
		this.con_capex = con_capex;
	}


}