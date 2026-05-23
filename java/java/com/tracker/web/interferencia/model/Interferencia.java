package com.tracker.web.interferencia.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

public class Interferencia {
	
	private	Integer inter_id;
	private	Integer inter_id_hist;
	private	String nro_caso;
	private	String site_code;
	private	String name_site;
	private	String banda_afectada; 
	private	String ruta_archivo;
	private	String sub_region;
	private	String departamento; 
	private String cluster_name;
	private	String distrito;
	private	String celdas_afectadas; 
	private	String responsable_caza; 
	private	String crq_number;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private	Date date_crq_envio;
	private	String week_crq_envio; 
	private	String priorid_celdas; 
	private	String priorid_nivel_interfer; 
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private	Date fecha_recepcion_report_oym; 
	private	String week_recepcion_report_oym;
	private	String tipo_fuente;
	private	String area_notificacion; 
	private	String date_notification;
	private	String visit_oym;
	private	String estatus_area; 
	private	String subestatus; 
	private	String responsable_area;
	private	String carta_denuncia;
	private	String cargo_mtc;
	private	String fecha_solucion_mtc; 
	private	String fecha_de_cese;
	private	String detalles_interferencia; 
	   
	@DateTimeFormat(pattern="dd/MMM/YYYY HH:mm:ss a")
	private	Date fecha_historial;
	private	String usuario_interferencia;
	private	String fecha_interferencia;
	private	String fecha_correctivo_hfc;
	
	private	String fecha_rpta_mtc;
	
	@DateTimeFormat(pattern="dd/MMM/YYYY HH:mm:ss a")
	private	Date fecha_registro_site;
	private String direccion_interferencia;
	private String nivel_interferencia;
	private String coordenadas_interferencia;
	
	
	private	String fecha_registro_site_date;	
	
	public String getFecha_registro_site_date() {
		return fecha_registro_site_date;
	}
	public void setFecha_registro_site_date(String fecha_registro_site_date) {
		this.fecha_registro_site_date = fecha_registro_site_date;
	}
	public String getFecha_correctivo_hfc() {
		return fecha_correctivo_hfc;
	}
	public void setFecha_correctivo_hfc(String fecha_correctivo_hfc) {
		this.fecha_correctivo_hfc = fecha_correctivo_hfc;
	}
	public String getSubestatus() {
		return subestatus;
	}
	public void setSubestatus(String subestatus) {
		this.subestatus = subestatus;
	}
	public String getNro_caso() {
		return nro_caso;
	}
	public void setNro_caso(String nro_caso) {
		this.nro_caso = nro_caso;
	}
	public String getCluster_name() {
		return cluster_name;
	}
	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}
	public Integer getInter_id() {
		return inter_id;
	}
	public void setInter_id(Integer inter_id) {
		this.inter_id = inter_id;
	}
	public Integer getInter_id_hist() {
		return inter_id_hist;
	}
	public void setInter_id_hist(Integer inter_id_hist) {
		this.inter_id_hist = inter_id_hist;
	}
	public String getSite_code() {
		return site_code;
	}
	public void setSite_code(String site_code) {
		this.site_code = site_code;
	}
	public String getName_site() {
		return name_site;
	}
	public void setName_site(String name_site) {
		this.name_site = name_site;
	}
	public String getBanda_afectada() {
		return banda_afectada;
	}
	public void setBanda_afectada(String banda_afectada) {
		this.banda_afectada = banda_afectada;
	}
	public String getRuta_archivo() {
		return ruta_archivo;
	}
	public void setRuta_archivo(String ruta_archivo) {
		this.ruta_archivo = ruta_archivo;
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
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}
	public String getCeldas_afectadas() {
		return celdas_afectadas;
	}
	public void setCeldas_afectadas(String celdas_afectadas) {
		this.celdas_afectadas = celdas_afectadas;
	}
	public String getResponsable_caza() {
		return responsable_caza;
	}
	public void setResponsable_caza(String responsable_caza) {
		this.responsable_caza = responsable_caza;
	}
	public String getCrq_number() {
		return crq_number;
	}
	public void setCrq_number(String crq_number) {
		this.crq_number = crq_number;
	}
	public Date getDate_crq_envio() {
		return date_crq_envio;
	}
	public void setDate_crq_envio(Date date_crq_envio) {
		this.date_crq_envio = date_crq_envio;
	}
	public String getWeek_crq_envio() {
		return week_crq_envio;
	}
	public void setWeek_crq_envio(String week_crq_envio) {
		this.week_crq_envio = week_crq_envio;
	}
	public String getPriorid_celdas() {
		return priorid_celdas;
	}
	public void setPriorid_celdas(String priorid_celdas) {
		this.priorid_celdas = priorid_celdas;
	}
	public String getPriorid_nivel_interfer() {
		return priorid_nivel_interfer;
	}
	public void setPriorid_nivel_interfer(String priorid_nivel_interfer) {
		this.priorid_nivel_interfer = priorid_nivel_interfer;
	}
	public Date getFecha_recepcion_report_oym() {
		return fecha_recepcion_report_oym;
	}
	public void setFecha_recepcion_report_oym(Date fecha_recepcion_report_oym) {
		this.fecha_recepcion_report_oym = fecha_recepcion_report_oym;
	}
	public String getWeek_recepcion_report_oym() {
		return week_recepcion_report_oym;
	}
	public void setWeek_recepcion_report_oym(String week_recepcion_report_oym) {
		this.week_recepcion_report_oym = week_recepcion_report_oym;
	}
	public String getTipo_fuente() {
		return tipo_fuente;
	}
	public void setTipo_fuente(String tipo_fuente) {
		this.tipo_fuente = tipo_fuente;
	}
	public String getArea_notificacion() {
		return area_notificacion;
	}
	public void setArea_notificacion(String area_notificacion) {
		this.area_notificacion = area_notificacion;
	}
	public String getDate_notification() {
		return date_notification;
	}
	public void setDate_notification(String date_notification) {
		this.date_notification = date_notification;
	}
	public String getVisit_oym() {
		return visit_oym;
	}
	public void setVisit_oym(String visit_oym) {
		this.visit_oym = visit_oym;
	}
	public String getEstatus_area() {
		return estatus_area;
	}
	public void setEstatus_area(String estatus_area) {
		this.estatus_area = estatus_area;
	}
	public String getResponsable_area() {
		return responsable_area;
	}
	public void setResponsable_area(String responsable_area) {
		this.responsable_area = responsable_area;
	}
	public String getCarta_denuncia() {
		return carta_denuncia;
	}
	public void setCarta_denuncia(String carta_denuncia) {
		this.carta_denuncia = carta_denuncia;
	}
	public String getCargo_mtc() {
		return cargo_mtc;
	}
	public void setCargo_mtc(String cargo_mtc) {
		this.cargo_mtc = cargo_mtc;
	}
	public String getFecha_solucion_mtc() {
		return fecha_solucion_mtc;
	}
	public void setFecha_solucion_mtc(String fecha_solucion_mtc) {
		this.fecha_solucion_mtc = fecha_solucion_mtc;
	}
	public String getFecha_de_cese() {
		return fecha_de_cese;
	}
	public void setFecha_de_cese(String fecha_de_cese) {
		this.fecha_de_cese = fecha_de_cese;
	}
	public String getDetalles_interferencia() {
		return detalles_interferencia;
	}
	public void setDetalles_interferencia(String detalles_interferencia) {
		this.detalles_interferencia = detalles_interferencia;
	}
	public Date getFecha_historial() {
		return fecha_historial;
	}
	public void setFecha_historial(Date fecha_historial) {
		this.fecha_historial = fecha_historial;
	}
	public String getUsuario_interferencia() {
		return usuario_interferencia;
	}
	public void setUsuario_interferencia(String usuario_interferencia) {
		this.usuario_interferencia = usuario_interferencia;
	}
	public String getFecha_interferencia() {
		return fecha_interferencia;
	}
	public void setFecha_interferencia(String fecha_interferencia) {
		this.fecha_interferencia = fecha_interferencia;
	}
	public String getFecha_rpta_mtc() {
		return fecha_rpta_mtc;
	}
	public void setFecha_rpta_mtc(String fecha_rpta_mtc) {
		this.fecha_rpta_mtc = fecha_rpta_mtc;
	}
	public Date getFecha_registro_site() {
		return fecha_registro_site;
	}
	public void setFecha_registro_site(Date fecha_registro_site) {
		this.fecha_registro_site = fecha_registro_site;
	}
	public String getDireccion_interferencia() {
		return direccion_interferencia;
	}
	public void setDireccion_interferencia(String direccion_interferencia) {
		this.direccion_interferencia = direccion_interferencia;
	}
	public String getNivel_interferencia() {
		return nivel_interferencia;
	}
	public void setNivel_interferencia(String nivel_interferencia) {
		this.nivel_interferencia = nivel_interferencia;
	}
	public String getCoordenadas_interferencia() {
		return coordenadas_interferencia;
	}
	public void setCoordenadas_interferencia(String coordenadas_interferencia) {
		this.coordenadas_interferencia = coordenadas_interferencia;
	}
	




}
