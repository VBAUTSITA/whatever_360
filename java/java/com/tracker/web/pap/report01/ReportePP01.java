package com.tracker.web.pap.report01;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
//@Table(name = "tm_conf_actual_celda_4g")
@Table(name = "vw_PLANI_SEGUIM_PP_DASH_01")
public class ReportePP01 {
	
	    @Id
	    @Column(name = "ot", nullable = false)
	    private Long ot;	    
	    
	    @Column(name = "tipo_seguimiento", nullable = false, length = 100)
	    private String tipoSeguimiento;	    

	    @Column(name = "req_alta", nullable = false)
	    private Long reqAlta;

	    @Column(name = "codigosite", nullable = false, length = 100)
	    private String codigosite;

	    @Column(name = "nombresite", nullable = false, length = 100)
	    private String nombresite;
	    
	    @Column(name = "estado_sitio", nullable = false, length = 100)
	    private String estadoSitio;

	    @Column(name = "tipo_estacion", nullable = false, length = 100)
	    private String tipoEstacion;

	    @Column(name = "etiqueta", nullable = false, length = 100)
	    private String etiqueta;

	    @Column(name = "proyecto", nullable = false, length = 100)
	    private String proyecto;

	    @Column(name = "sub_proyecto", nullable = false, length = 100)
	    private String subProyecto;
	    
	    @Column(name = "region", nullable = false, length = 100)
	    private String region;

	    @Column(name = "subregion", nullable = false, length = 100)
	    private String subregion;

	    @Column(name = "contrata", nullable = false, length = 100)
	    private String contrata;

	    @Column(name = "status_ot", nullable = false, length = 100)
	    private String statusOt;

	    @Column(name = "estado_total", nullable = false, length = 100)
	    private String estadoTotal;
	    
	    @Column(name = "fase_proyecto", nullable = false, length = 100)
	    private String faseProyecto;

	    @Column(name = "etapa_evolucion", nullable = false, length = 100)
	    private String etapaEvolucion;

	    @Column(name = "subproy_const", nullable = false, length = 100)
	    private String subproyConst;

	    @Column(name = "forecast_etapa", nullable = false)
	    @Temporal(TemporalType.DATE)
	    private Date forecastEtapa;

	    @Column(name = "forecast_proyecto", nullable = false)
	    @Temporal(TemporalType.DATE)
	    private Date forecastProyecto;

	    @Column(name = "cambios_forecast_proyecto", nullable = false)
	    private Long cambiosForecastProyecto;
	    
	    @Column(name = "latitud", nullable = false)
	    private Double latitud;

	    @Column(name = "longitud", nullable = false)
	    private Double longitud;

	    @Column(name = "fecha_activacion", nullable = false)
	    @Temporal(TemporalType.DATE)
	    private Date fechaActivacion;

	    @Column(name = "fecha_on_air", nullable = false)
	    @Temporal(TemporalType.DATE)
	    private Date fechaOnAir;

	    @Column(name = "seguimiento_historico", nullable = false, length = 100)
	    private String seguimientoHistorico;
	    
	    @Column(name = "departamento", nullable = false, length = 100)
	    private String departamento;

	    @Column(name = "provincia", nullable = false, length = 100)
	    private String provincia;
	    
	    @Column(name = "distrito", nullable = false, length = 100)
	    private String distrito;

	    @Column(name = "fecha_carga", nullable = false)
	    @Temporal(TemporalType.DATE)
	    private Date fechaCarga;
	    
	    @Column(name = "week_fecha_activacion", nullable = false)
	    private Long weekFechaActivacion;

	    // Getters y setters...
	    
	    
		public Long getOt() {
			return ot;
		}

		public void setOt(Long ot) {
			this.ot = ot;
		}

		public String getTipoSeguimiento() {
			return tipoSeguimiento;
		}

		public void setTipoSeguimiento(String tipoSeguimiento) {
			this.tipoSeguimiento = tipoSeguimiento;
		}

		public Long getReqAlta() {
			return reqAlta;
		}

		public void setReqAlta(Long reqAlta) {
			this.reqAlta = reqAlta;
		}

		public String getCodigosite() {
			return codigosite;
		}

		public void setCodigosite(String codigosite) {
			this.codigosite = codigosite;
		}

		public String getNombresite() {
			return nombresite;
		}

		public void setNombresite(String nombresite) {
			this.nombresite = nombresite;
		}

		public String getEstadoSitio() {
			return estadoSitio;
		}

		public void setEstadoSitio(String estadoSitio) {
			this.estadoSitio = estadoSitio;
		}

		public String getTipoEstacion() {
			return tipoEstacion;
		}

		public void setTipoEstacion(String tipoEstacion) {
			this.tipoEstacion = tipoEstacion;
		}

		public String getEtiqueta() {
			return etiqueta;
		}

		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}

		public String getProyecto() {
			return proyecto;
		}

		public void setProyecto(String proyecto) {
			this.proyecto = proyecto;
		}

		public String getSubProyecto() {
			return subProyecto;
		}

		public void setSubProyecto(String subProyecto) {
			this.subProyecto = subProyecto;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getSubregion() {
			return subregion;
		}

		public void setSubregion(String subregion) {
			this.subregion = subregion;
		}

		public String getContrata() {
			return contrata;
		}

		public void setContrata(String contrata) {
			this.contrata = contrata;
		}

		public String getStatusOt() {
			return statusOt;
		}

		public void setStatusOt(String statusOt) {
			this.statusOt = statusOt;
		}

		public String getEstadoTotal() {
			return estadoTotal;
		}

		public void setEstadoTotal(String estadoTotal) {
			this.estadoTotal = estadoTotal;
		}

		public String getFaseProyecto() {
			return faseProyecto;
		}

		public void setFaseProyecto(String faseProyecto) {
			this.faseProyecto = faseProyecto;
		}

		public String getEtapaEvolucion() {
			return etapaEvolucion;
		}

		public void setEtapaEvolucion(String etapaEvolucion) {
			this.etapaEvolucion = etapaEvolucion;
		}

		public String getSubproyConst() {
			return subproyConst;
		}

		public void setSubproyConst(String subproyConst) {
			this.subproyConst = subproyConst;
		}

		public Date getForecastEtapa() {
			return forecastEtapa;
		}

		public void setForecastEtapa(Date forecastEtapa) {
			this.forecastEtapa = forecastEtapa;
		}

		public Date getForecastProyecto() {
			return forecastProyecto;
		}

		public void setForecastProyecto(Date forecastProyecto) {
			this.forecastProyecto = forecastProyecto;
		}

		public Long getCambiosForecastProyecto() {
			return cambiosForecastProyecto;
		}

		public void setCambiosForecastProyecto(Long cambiosForecastProyecto) {
			this.cambiosForecastProyecto = cambiosForecastProyecto;
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

		public Date getFechaActivacion() {
			return fechaActivacion;
		}

		public void setFechaActivacion(Date fechaActivacion) {
			this.fechaActivacion = fechaActivacion;
		}

		public Date getFechaOnAir() {
			return fechaOnAir;
		}

		public void setFechaOnAir(Date fechaOnAir) {
			this.fechaOnAir = fechaOnAir;
		}

		public String getSeguimientoHistorico() {
			return seguimientoHistorico;
		}

		public void setSeguimientoHistorico(String seguimientoHistorico) {
			this.seguimientoHistorico = seguimientoHistorico;
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

		public Date getFechaCarga() {
			return fechaCarga;
		}

		public void setFechaCarga(Date fechaCarga) {
			this.fechaCarga = fechaCarga;
		}

		public Long getWeekFechaActivacion() {
			return weekFechaActivacion;
		}

		public void setWeekFechaActivacion(Long weekFechaActivacion) {
			this.weekFechaActivacion = weekFechaActivacion;
		}

	  
    
}




