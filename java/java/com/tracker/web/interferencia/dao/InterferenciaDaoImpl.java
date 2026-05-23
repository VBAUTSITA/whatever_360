package com.tracker.web.interferencia.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.web.interferencia.model.Interferencia;
import com.tracker.web.interferencia.model.InterferenciaRowMapper;

@Transactional
@Repository
public class InterferenciaDaoImpl implements InterferenciaDao {

	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Interferencia> getAllInterferencias() {

		String query = "select * from (SELECT to_char(a.fecha_registro_site, 'YYYY/MM/DD HH24:MI:SS') fecha_registro_site_date, \r\n" + 
				"to_char(a.fecha_historial, 'YYYY/MM/DD HH24:MI:SS') fecha_interferencia,a.* \r\n" + 
				"FROM opti_interfreq_saved a order by a.fecha_historial desc)";

		RowMapper<Interferencia> rowMapper = new InterferenciaRowMapper();

		List<Interferencia> list = jdbcTemplate.query(query, rowMapper);

		return list;
	}
	
	@Override
	public List<Interferencia> getAllInterferenciaHist() {

		String query = "select * from (SELECT to_char(a.fecha_registro_site, 'YYYY/MM/DD HH24:MI:SS') fecha_registro_site_date, \r\n" + 
				"to_char(a.fecha_historial, 'YYYY/MM/DD HH24:MI:SS') fecha_interferencia,a.* \r\n" + 
				"FROM opti_interfreq_saved_hist a order by a.fecha_historial desc)";
		
		RowMapper<Interferencia> rowMapper = new InterferenciaRowMapper();

		List<Interferencia> list = jdbcTemplate.query(query, rowMapper);

		return list;
		
	}
	

	@Override
	public Interferencia findInterferenciaById(int id) {

		String query = "SELECT * FROM opti_interfreq_saved WHERE inter_id = ? ";

		RowMapper<Interferencia> rowMapper = new BeanPropertyRowMapper<Interferencia>(Interferencia.class);

		Interferencia interferencia = jdbcTemplate.queryForObject(query, rowMapper, id);

		return interferencia;
	}
	
	@Override
	public List<Interferencia> getAllLegalInterferencias() {

		String query = "select * from (SELECT to_char(a.fecha_registro_site, 'YYYY/MM/DD HH24:MI:SS') fecha_registro_site_date, \r\n" + 
				"to_char(a.fecha_historial, 'YYYY/MM/DD HH24:MI:SS') fecha_interferencia,a.* \r\n" + 
				"FROM opti_interfreq_saved a order by a.fecha_historial desc)";

		RowMapper<Interferencia> rowMapper = new InterferenciaRowMapper();

		List<Interferencia> list = jdbcTemplate.query(query, rowMapper);

		return list;
	}
	
	/*historico por id del la tabla principal*/
	@Override
	public List<Interferencia> findInterferenciaHistById(int id) {

		String query = "select * from (SELECT to_char(a.fecha_registro_site, 'YYYY/MM/DD HH24:MI:SS') fecha_registro_site_date, \r\n" + 
				"to_char(a.fecha_historial, 'YYYY/MM/DD HH24:MI:SS') fecha_interferencia,a.* \r\n" + 
				"FROM opti_interfreq_saved_hist a WHERE a.inter_id = ? order by a.fecha_historial desc)";

		RowMapper<Interferencia> rowMapper = new BeanPropertyRowMapper<Interferencia>(Interferencia.class);

		List<Interferencia> list = jdbcTemplate.query(query, rowMapper, id);

		return list;
	}
	/**/
	
	/*historico por id de la tabla historica*/
	@Override
	public Interferencia findInterferenciaHistByIdSolo(int id) {

		String query = "select * from (select * FROM opti_interfreq_saved_hist WHERE inter_id_hist = ? order by fecha_historial desc)";

		RowMapper<Interferencia> rowMapper = new BeanPropertyRowMapper<Interferencia>(Interferencia.class);

		Interferencia interferencia = jdbcTemplate.queryForObject(query, rowMapper, id);

		return interferencia;

	}
	/**/

	
	
	@Override
	public void addInterferencia(Interferencia interferencia) {

		String query1 = "INSERT INTO opti_interfreq_saved \r\n" + 
				"(inter_id, nro_caso, site_code, name_site, banda_afectada, ruta_archivo, sub_region, departamento, cluster_name, distrito, \r\n" + 
				"celdas_afectadas, responsable_caza, crq_number, date_crq_envio, priorid_celdas, priorid_nivel_interfer, \r\n" + 
				"fecha_recepcion_report_oym, tipo_fuente, area_notificacion, date_notification,\r\n" + 
				"visit_oym, estatus_area, subestatus, responsable_area, carta_denuncia, cargo_mtc, fecha_solucion_mtc, fecha_de_cese,\r\n" + 
				"detalles_interferencia, fecha_historial, usuario_interferencia, fecha_correctivo_hfc, fecha_rpta_mtc, direccion_interferencia, nivel_interferencia, coordenadas_interferencia, fecha_registro_site)\r\n" + 
				"VALUES (OPTI_interfreq_SEQ.NEXTVAL,concat(to_char(sysdate, 'YYYY'),OPTI_interfreq_nrocaso_SEQ.NEXTVAL),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,sysdate)";

		jdbcTemplate.update(query1, interferencia.getSite_code(), interferencia.getName_site(),	interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), 
				interferencia.getDate_crq_envio(), interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(), interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), interferencia.getFecha_correctivo_hfc(),
				interferencia.getFecha_rpta_mtc(), interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia()  );
		
		String query1update = "UPDATE opti_interfreq_saved SET site_code = ? , name_site = ? , banda_afectada = ? , ruta_archivo = ? , \r\n" + 
				"sub_region = ? , departamento = ? , cluster_name = ? , distrito = ? , celdas_afectadas = ? , responsable_caza = ? , \r\n" + 
				"crq_number = ? , date_crq_envio = ? , \r\n" + 
				"week_crq_envio = case\r\n" + 
				"                        when date_crq_envio is null then week_crq_envio \r\n" + 
				"                        when date_crq_envio is not null then \r\n" + 
				"                             to_char( (date_crq_envio) ,'IW')\r\n" + 
				"                        end,\r\n" + 
				"priorid_celdas = ? , priorid_nivel_interfer = ? , fecha_recepcion_report_oym = ? , \r\n" + 
				"week_recepcion_report_oym = case\r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is null then week_recepcion_report_oym \r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is not null then \r\n" + 
				"                             to_char( (FECHA_RECEPCION_REPORT_OYM) ,'IW')\r\n" + 
				"                        end,               \r\n" + 
				"tipo_fuente = ? , area_notificacion = ? , date_notification = ? , visit_oym = ? , estatus_area = ? , subestatus = ? , \r\n" + 
				"responsable_area = ? , carta_denuncia = ? , cargo_mtc = ? , fecha_solucion_mtc = ? , fecha_de_cese = ? ,\r\n" + 
				"detalles_interferencia = ? , fecha_historial = sysdate , usuario_interferencia = ? , fecha_correctivo_hfc = ?, fecha_rpta_mtc = ?, \r\n" + 
				"direccion_interferencia = ? , nivel_interferencia = ? , coordenadas_interferencia = ?  WHERE inter_id=(select max(inter_id) from opti_interfreq_saved)";			
								
		jdbcTemplate.update(query1update, interferencia.getSite_code(), interferencia.getName_site(),	interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), 
				interferencia.getDate_crq_envio(),  interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(), interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), interferencia.getFecha_correctivo_hfc(), 
				interferencia.getFecha_rpta_mtc(), interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia()  );
		
		String query2 = "INSERT INTO opti_interfreq_saved_hist \r\n" + 
				"(inter_id_hist, inter_id, nro_caso, site_code, name_site, banda_afectada, ruta_archivo, sub_region, departamento, cluster_name, distrito, \r\n" + 
				"celdas_afectadas, responsable_caza, crq_number, date_crq_envio, priorid_celdas, priorid_nivel_interfer, \r\n" + 
				"fecha_recepcion_report_oym, tipo_fuente, area_notificacion, date_notification,\r\n" + 
				"visit_oym, estatus_area, subestatus, responsable_area, carta_denuncia, cargo_mtc, fecha_solucion_mtc, fecha_de_cese,\r\n" + 
				"detalles_interferencia, fecha_historial, usuario_interferencia, fecha_correctivo_hfc, fecha_rpta_mtc, direccion_interferencia, nivel_interferencia, coordenadas_interferencia, fecha_registro_site  )\r\n" + 
				"VALUES (OPTI_interfreq_hist_SEQ.NEXTVAL,(select max(inter_id) from opti_interfreq_saved),(select max(nro_caso) from opti_interfreq_saved),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,sysdate)";


		jdbcTemplate.update(query2, interferencia.getSite_code(), interferencia.getName_site(),	interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), 
				interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), interferencia.getDate_crq_envio(),  
				interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(), interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), interferencia.getFecha_correctivo_hfc(),
				interferencia.getFecha_rpta_mtc(),interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia() );
		
		String query2update = "UPDATE opti_interfreq_saved_hist SET site_code = ? , name_site = ? , banda_afectada = ? , ruta_archivo = ? , \r\n" + 
				"sub_region = ? , departamento = ? , cluster_name = ? , distrito = ? , celdas_afectadas = ? , responsable_caza = ? , \r\n" + 
				"crq_number = ? , date_crq_envio = ? , \r\n" + 
				"week_crq_envio = case\r\n" + 
				"                        when date_crq_envio is null then week_crq_envio \r\n" + 
				"                        when date_crq_envio is not null then \r\n" + 
				"                             to_char( (date_crq_envio) ,'IW')\r\n" + 
				"                        end,\r\n" + 
				"priorid_celdas = ? , priorid_nivel_interfer = ? , fecha_recepcion_report_oym = ? , \r\n" + 
				"week_recepcion_report_oym = case\r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is null then week_recepcion_report_oym \r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is not null then \r\n" + 
				"                             to_char( (FECHA_RECEPCION_REPORT_OYM) ,'IW')\r\n" + 
				"                        end,                 \r\n" + 
				"tipo_fuente = ? , area_notificacion = ? , date_notification = ? , visit_oym = ? , estatus_area = ? ,  subestatus = ? ,\r\n" + 
				"responsable_area = ? , carta_denuncia = ? , cargo_mtc = ? , fecha_solucion_mtc = ? , fecha_de_cese = ? ,\r\n" + 
				"detalles_interferencia = ? , fecha_historial = sysdate , usuario_interferencia = ? , fecha_correctivo_hfc = ?, fecha_rpta_mtc = ?, \r\n" + 
				"direccion_interferencia = ? , nivel_interferencia = ? , coordenadas_interferencia = ? WHERE inter_id_hist=(select max(inter_id_hist) from opti_interfreq_saved_hist)";			
								
		jdbcTemplate.update(query2update, interferencia.getSite_code(), interferencia.getName_site(),	interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), 
				interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), interferencia.getDate_crq_envio(),  
				interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(),  interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), interferencia.getFecha_correctivo_hfc(), 
				interferencia.getFecha_rpta_mtc(),interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia() );

		
	}
	
	@Override
	public void updateInterferencia(Interferencia interferencia) {
		
		String query1 = "UPDATE opti_interfreq_saved SET nro_caso = ? , site_code = ? , name_site = ? , banda_afectada = ? , ruta_archivo = ? , \r\n" + 
				"sub_region = ? , departamento = ? , cluster_name = ? , distrito = ? , celdas_afectadas = ? , responsable_caza = ? , \r\n" + 
				"crq_number = ? , date_crq_envio = ? , \r\n" + 				
				"priorid_celdas = ? , priorid_nivel_interfer = ? , fecha_recepcion_report_oym = ? , \r\n" + 
				"tipo_fuente = ? , area_notificacion = ? , date_notification = ? , visit_oym = ? , estatus_area = ? , subestatus = ? , \r\n" + 
				"responsable_area = ? , carta_denuncia = ? , cargo_mtc = ? , fecha_solucion_mtc = ? , fecha_de_cese = ? ,\r\n" + 
				"detalles_interferencia = ? , fecha_historial = sysdate , usuario_interferencia = ? , fecha_correctivo_hfc = ? , fecha_rpta_mtc = ? , direccion_interferencia = ? , nivel_interferencia = ? , coordenadas_interferencia = ?, fecha_registro_site = ? \r\n" + 
				"WHERE inter_id=? ";			
								
		jdbcTemplate.update(query1, interferencia.getNro_caso(), interferencia.getSite_code(), interferencia.getName_site(), interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), 
				interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), interferencia.getDate_crq_envio(),  
				interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(), interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), 
				interferencia.getFecha_correctivo_hfc(), interferencia.getFecha_rpta_mtc(), interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia(), interferencia.getFecha_registro_site(), interferencia.getInter_id());

		String query11 = "UPDATE opti_interfreq_saved SET nro_caso = ? , site_code = ? , name_site = ? , banda_afectada = ? , ruta_archivo = ? , \r\n" + 
				"sub_region = ? , departamento = ? , cluster_name = ? , distrito = ? , celdas_afectadas = ? , responsable_caza = ? , \r\n" + 
				"crq_number = ? , date_crq_envio = ? , \r\n" + 
				"week_crq_envio = case\r\n" + 
				"                        when date_crq_envio is null then week_crq_envio \r\n" + 
				"                        when date_crq_envio is not null then \r\n" + 
				"                             to_char( (date_crq_envio) ,'IW')\r\n" + 
				"                        end,\r\n" + 
				"priorid_celdas = ? , priorid_nivel_interfer = ? , fecha_recepcion_report_oym = ? , \r\n" + 
				"week_recepcion_report_oym = case\r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is null then week_recepcion_report_oym \r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is not null then \r\n" + 
				"                             to_char( (FECHA_RECEPCION_REPORT_OYM) ,'IW')\r\n" + 
				"                        end, \r\n" + 
				"tipo_fuente = ? , area_notificacion = ? , date_notification = ? , visit_oym = ? , estatus_area = ? , subestatus = ? , \r\n" + 
				"responsable_area = ? , carta_denuncia = ? , cargo_mtc = ? , fecha_solucion_mtc = ? , fecha_de_cese = ? ,\r\n" + 
				"detalles_interferencia = ? , fecha_historial = sysdate , usuario_interferencia = ? , fecha_correctivo_hfc = ? , fecha_rpta_mtc = ? , direccion_interferencia = ? , nivel_interferencia = ? , coordenadas_interferencia = ?, fecha_registro_site = ? \r\n" + 
				"WHERE inter_id=? ";			
								
		jdbcTemplate.update(query11, interferencia.getNro_caso(), interferencia.getSite_code(), interferencia.getName_site(), interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), 
				interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), interferencia.getDate_crq_envio(),  
				interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(), interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), 
				interferencia.getFecha_correctivo_hfc(), interferencia.getFecha_rpta_mtc(), interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia(), interferencia.getFecha_registro_site(), interferencia.getInter_id());

		
		String query2 = "INSERT INTO opti_interfreq_saved_hist \r\n" + 
				"(inter_id_hist, inter_id, nro_caso, site_code, name_site, banda_afectada, ruta_archivo, sub_region, departamento, cluster_name, distrito, \r\n" + 
				"celdas_afectadas, responsable_caza, crq_number, date_crq_envio, priorid_celdas, priorid_nivel_interfer, \r\n" + 
				"fecha_recepcion_report_oym, tipo_fuente, area_notificacion, date_notification,\r\n" + 
				"visit_oym, estatus_area,  subestatus, responsable_area, carta_denuncia, cargo_mtc, fecha_solucion_mtc, fecha_de_cese,\r\n" + 
				"detalles_interferencia, fecha_historial, usuario_interferencia, fecha_correctivo_hfc, fecha_rpta_mtc, direccion_interferencia, nivel_interferencia, coordenadas_interferencia, fecha_registro_site)\r\n" + 
				"VALUES (OPTI_interfreq_hist_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?)";


		jdbcTemplate.update(query2, interferencia.getInter_id(), interferencia.getNro_caso(), interferencia.getSite_code(), interferencia.getName_site(),	
				interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), 
				interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), interferencia.getDate_crq_envio(),  
				interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(), interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), 
				interferencia.getFecha_correctivo_hfc(), interferencia.getFecha_rpta_mtc(), interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia(), interferencia.getFecha_registro_site());

		String query22 = "UPDATE opti_interfreq_saved_hist SET site_code = ? , name_site = ? , banda_afectada = ? , ruta_archivo = ? , \r\n" + 
				"sub_region = ? , departamento = ? , cluster_name = ? , distrito = ? , celdas_afectadas = ? , responsable_caza = ? , \r\n" + 
				"crq_number = ? , date_crq_envio = ? , \r\n" + 
				"week_crq_envio = case\r\n" + 
				"                        when date_crq_envio is null then week_crq_envio \r\n" + 
				"                        when date_crq_envio is not null then \r\n" + 
				"                             to_char( (date_crq_envio) ,'IW')\r\n" + 
				"                        end,\r\n" + 
				"priorid_celdas = ? , priorid_nivel_interfer = ? , fecha_recepcion_report_oym = ? , \r\n" + 
				"week_recepcion_report_oym = case\r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is null then week_recepcion_report_oym \r\n" + 
				"                        when FECHA_RECEPCION_REPORT_OYM is not null then \r\n" + 
				"                             to_char( (FECHA_RECEPCION_REPORT_OYM) ,'IW')\r\n" + 
				"                        end,                 \r\n" + 
				"tipo_fuente = ? , area_notificacion = ? , date_notification = ? , visit_oym = ? , estatus_area = ? ,  subestatus = ? ,\r\n" + 
				"responsable_area = ? , carta_denuncia = ? , cargo_mtc = ? , fecha_solucion_mtc = ? , fecha_de_cese = ? ,\r\n" + 
				"detalles_interferencia = ? , fecha_historial = sysdate , usuario_interferencia = ? , fecha_correctivo_hfc = ?, fecha_rpta_mtc = ? ,  direccion_interferencia = ? , nivel_interferencia = ? , coordenadas_interferencia = ? \r\n" + 
				"WHERE inter_id_hist=(select max(inter_id_hist) from opti_interfreq_saved_hist)";			
								
		jdbcTemplate.update(query22, interferencia.getSite_code(), interferencia.getName_site(),	interferencia.getBanda_afectada(), 
				interferencia.getRuta_archivo(), interferencia.getSub_region(), interferencia.getDepartamento(), interferencia.getCluster_name(), 
				interferencia.getDistrito(), 
				interferencia.getCeldas_afectadas(), interferencia.getResponsable_caza(),  interferencia.getCrq_number(), interferencia.getDate_crq_envio(),  
				interferencia.getPriorid_celdas(), interferencia.getPriorid_nivel_interfer(),  
				interferencia.getFecha_recepcion_report_oym(),  interferencia.getTipo_fuente(), 
				interferencia.getArea_notificacion(), interferencia.getDate_notification(), interferencia.getVisit_oym(),  interferencia.getEstatus_area(),
				interferencia.getSubestatus(), interferencia.getResponsable_area(), interferencia.getCarta_denuncia(), interferencia.getCargo_mtc(), interferencia.getFecha_solucion_mtc(),
				interferencia.getFecha_de_cese(), interferencia.getDetalles_interferencia(), interferencia.getUsuario_interferencia(), interferencia.getFecha_correctivo_hfc(), 
				interferencia.getFecha_rpta_mtc(), interferencia.getDireccion_interferencia(), interferencia.getNivel_interferencia(), interferencia.getCoordenadas_interferencia() );
		
		
	}

	@Override
	public void deleteInterferencia(int id) {

		String query = "DELETE FROM opti_interfreq_saved WHERE inter_id = ?";

		jdbcTemplate.update(query, id);
	}

}
