package com.tracker.web.interferencia.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class InterferenciaRowMapper implements RowMapper<Interferencia> {

	@Override
	public Interferencia mapRow(ResultSet rs, int rowNum) throws SQLException {

		Interferencia interferencia = new Interferencia();
		
		interferencia.setInter_id_hist(rs.getInt("inter_id_hist"));
		interferencia.setInter_id(rs.getInt("inter_id"));	
		interferencia.setNro_caso(rs.getString("nro_caso"));
		interferencia.setSite_code(rs.getString("site_code"));
		interferencia.setName_site(rs.getString("name_site"));
		interferencia.setBanda_afectada(rs.getString("banda_afectada"));
		interferencia.setRuta_archivo(rs.getString("ruta_archivo"));
		interferencia.setSub_region(rs.getString("sub_region"));
		interferencia.setDepartamento(rs.getString("departamento"));
		interferencia.setCluster_name(rs.getString("cluster_name"));
		interferencia.setDistrito(rs.getString("distrito"));
		interferencia.setCeldas_afectadas(rs.getString("celdas_afectadas"));
		interferencia.setResponsable_caza(rs.getString("responsable_caza"));
		interferencia.setCrq_number(rs.getString("crq_number"));
		interferencia.setDate_crq_envio(rs.getDate("date_crq_envio"));
		interferencia.setWeek_crq_envio(rs.getString("week_crq_envio"));
		interferencia.setPriorid_celdas(rs.getString("priorid_celdas"));
		interferencia.setPriorid_nivel_interfer(rs.getString("priorid_nivel_interfer"));
		interferencia.setFecha_recepcion_report_oym(rs.getDate("fecha_recepcion_report_oym"));
		interferencia.setWeek_recepcion_report_oym(rs.getString("week_recepcion_report_oym"));
		interferencia.setTipo_fuente(rs.getString("tipo_fuente"));
		interferencia.setArea_notificacion(rs.getString("area_notificacion"));
		interferencia.setDate_notification(rs.getString("date_notification"));
		interferencia.setVisit_oym(rs.getString("visit_oym"));
		interferencia.setEstatus_area(rs.getString("estatus_area"));
		interferencia.setSubestatus(rs.getString("subestatus"));
		interferencia.setResponsable_area(rs.getString("responsable_area"));
		interferencia.setCarta_denuncia(rs.getString("carta_denuncia"));
		interferencia.setCargo_mtc(rs.getString("cargo_mtc"));
		interferencia.setFecha_solucion_mtc(rs.getString("fecha_solucion_mtc"));
		interferencia.setFecha_de_cese(rs.getString("fecha_de_cese"));
		interferencia.setDetalles_interferencia(rs.getString("detalles_interferencia"));
		interferencia.setFecha_historial(rs.getDate("fecha_historial"));
		interferencia.setUsuario_interferencia(rs.getString("usuario_interferencia"));
		interferencia.setFecha_interferencia(rs.getString("fecha_interferencia"));
		interferencia.setFecha_correctivo_hfc(rs.getString("fecha_correctivo_hfc"));
		interferencia.setFecha_rpta_mtc(rs.getString("fecha_rpta_mtc"));
		interferencia.setFecha_registro_site(rs.getDate("fecha_registro_site"));
		interferencia.setFecha_registro_site_date(rs.getString("fecha_registro_site_date"));
		interferencia.setDireccion_interferencia(rs.getString("direccion_interferencia"));
		interferencia.setNivel_interferencia(rs.getString("nivel_interferencia"));
		interferencia.setCoordenadas_interferencia(rs.getString("coordenadas_interferencia"));
		
		return interferencia;
	}

}
