package com.tracker.web.seguimiento.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.web.seguimiento.model.Seguimiento;
import com.tracker.web.seguimiento.model.SeguimientoRowMapper;

@Transactional
@Repository
public class SeguimientoDaoImpl implements SeguimientoDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Seguimiento> getAllSeguimientos() {

		String query = "select * from (SELECT to_char(a.date_change, 'YYYY/MM/DD HH24:MI:SS') fecha,a.* FROM OPTI_SITES_SAVED a where a.tipo_carga is null order by a.date_change desc)";

		RowMapper<Seguimiento> rowMapper = new SeguimientoRowMapper();

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper);

		return list;
	}
	
	@Override
	public List<Seguimiento> getAllSeguimientosHist() {

		String query = "select * from (SELECT to_char(a.date_change, 'YYYY/MM/DD HH24:MI:SS') fecha,a.* FROM OPTI_SITES_SAVED_HIST a where a.tipo_carga is null order by a.date_change desc)";

		RowMapper<Seguimiento> rowMapper = new SeguimientoRowMapper();

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper);

		return list;
		
	}
	

	@Override
	public Seguimiento findSeguimientoById(int id) {

		String query = "SELECT * FROM OPTI_SITES_SAVED WHERE site_id = ? ";

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento seguimiento = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento;
	}
	
	@Override
	public List<Seguimiento> getAllSeguimientosMasivo() {

		String query = "select * from (SELECT to_char(a.date_change, 'YYYY/MM/DD HH24:MI:SS') fecha,a.* FROM OPTI_SITES_SAVED_TEST a where a.tipo_carga = 'masivo' order by a.date_change desc)";

		RowMapper<Seguimiento> rowMapper = new SeguimientoRowMapper();

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper);

		return list;
	}
	
	/*historico por id del la tabla principal*/
	@Override
	public List<Seguimiento> findSeguimientoHistById(int id) {

		String query = "select * from (SELECT to_char(a.date_change, 'YYYY/MM/DD HH24:MI:SS') fecha,a.* FROM OPTI_SITES_SAVED_HIST a WHERE a.site_id = ? order by a.date_change desc)";

		//RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		/*Seguimiento seguimiento = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento;*/
		
		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;
	}
	/**/
	
	/*historico por id de la tabla historica*/
	@Override
	public Seguimiento findSeguimientoHistByIdSolo(int id) {

		String query = "select * from (SELECT to_char(a.date_change, 'DD/MM/YYYY HH24:MI:SS') fecha,a.* FROM OPTI_SITES_SAVED_HIST a WHERE a.site_id_hist = ? order by a.date_change desc)";

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento seguimiento = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento;
		
		/*RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;*/
	}
	/**/



	
	@Override
	public void addSeguimiento(Seguimiento seguimiento) {

		String query1 = "INSERT INTO OPTI_SITES_SAVED (site_id, code_site, site_address, cellname, longitud, latitud, tilt_electric_field, azimuth, \r\n" + 
				"height, tilt_mecanic_field, tipo_antena, potencia, modo_transmision_celda, logic_changes, date_change, usuario, estado_cambio, cambio_coment ) \r\n" + 
				"VALUES (OPTI_SITE_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?,?)";

		jdbcTemplate.update(query1, seguimiento.getCode_site(), seguimiento.getSite_address(),
				seguimiento.getCellname(), seguimiento.getLongitud(), seguimiento.getLatitud(), 
				seguimiento.getTilt_electric_field(), seguimiento.getAzimuth(), 
				seguimiento.getHeight(), seguimiento.getTilt_mecanic_field(), 
				seguimiento.getTipo_antena(), seguimiento.getPotencia(),seguimiento.getModo_transmision_celda(),
				seguimiento.getLogic_changes(),
				 seguimiento.getUsuario(), seguimiento.getEstado_cambio(), seguimiento.getCambio_coment());
		
		String query1update = "UPDATE OPTI_SITES_SAVED SET code_site = case\r\n" + 
				"       --2G--\r\n" + 
				"  			when cellname like '%1' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%2' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%3' then substr(cellname,1,6) \r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%8' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%9' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%0' then substr(cellname,1,6)\r\n" + 
				"        --3G--\r\n" + 
				"        when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}[0-9]C[123][_][[:print:]]*$' , 'i') then substr(cellname,1,7)\r\n" + 
				"        --4G--\r\n" + 
				"        when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}[0-9]C[1234][_][[:print:]]*$' , 'i') then substr(cellname,1,7)\r\n" + 
				"     end ,  site_address = SUBSTR(CELLNAME, INSTR(CELLNAME, '_') + 1) , cellname = ? ,\r\n" + 
				"sector = case \r\n" + 
				"            --2G--\r\n" + 
				"            when cellname like '%1' then 1\r\n" + 
				"            when cellname like '%2' then 2\r\n" + 
				"            when cellname like '%3' then 3\r\n" + 
				"            when cellname like '%4' then 4\r\n" + 
				"            when cellname like '%5' then 5\r\n" + 
				"            when cellname like '%6' then 6\r\n" + 
				"            when cellname like '%7' then 7\r\n" + 
				"            when cellname like '%8' then 8\r\n" + 
				"            when cellname like '%9' then 9\r\n" + 
				"            when cellname like '%0' then 0\r\n" + 
				"            --3G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}1C[123][_][[:print:]]*$' , 'i') then 1\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}2C[123][_][[:print:]]*$' , 'i') then 2\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}3C[123][_][[:print:]]*$' , 'i') then 3\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}4C[123][_][[:print:]]*$' , 'i') then 4\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}5C[123][_][[:print:]]*$' , 'i') then 5\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}6C[123][_][[:print:]]*$' , 'i') then 6\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}7C[123][_][[:print:]]*$' , 'i') then 7\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}8C[123][_][[:print:]]*$' , 'i') then 8                       \r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}9C[123][_][[:print:]]*$' , 'i') then 9\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}0C[123][_][[:print:]]*$' , 'i') then 0\r\n" + 
				"            --4G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}1C[1234][_][[:print:]]*$' , 'i') then 1\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}2C[1234][_][[:print:]]*$' , 'i') then 2\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}3C[1234][_][[:print:]]*$' , 'i') then 3  \r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}4C[1234][_][[:print:]]*$' , 'i') then 4\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}5C[1234][_][[:print:]]*$' , 'i') then 5\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}6C[1234][_][[:print:]]*$' , 'i') then 6\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}7C[1234][_][[:print:]]*$' , 'i') then 7\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}8C[1234][_][[:print:]]*$' , 'i') then 8\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}9C[1234][_][[:print:]]*$' , 'i') then 9\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}0C[1234][_][[:print:]]*$' , 'i') then 0\r\n" + 
				"         end, \r\n" + 
				"band = case \r\n" + 
				"            --2G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[0-9]{4}[_][[:print:]]*[1234]$' , 'i')\r\n" + 
				"            then '1900'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[0-9]{4}[_][[:print:]]*[089]$' , 'i')\r\n" + 
				"            then '850'            \r\n" + 
				"            --3G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{5}C[12][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '850'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{5}C[3][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '1900'\r\n" + 
				"            --4G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[1][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '1900'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[23][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '2600'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[4][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '700'\r\n" + 
				"         end, \r\n" + 
				"\r\n" + 
				"tecnologia = case\r\n" + 
				"             --2G--\r\n" + 
				"             when regexp_like (cellname, '^[a-z]{2}[0-9]{4}[_][[:print:]]*[0-9]$' , 'i')\r\n" + 
				"             then '2G'\r\n" + 
				"             --3G--\r\n" + 
				"             when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{5}C[123][_][[:print:]]*$' , 'i')\r\n" + 
				"             then '3G'\r\n" + 
				"             --4G--   \r\n" + 
				"             when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[1234][_][[:print:]]*$' , 'i')\r\n" + 
				"             then '4G'\r\n" + 
				"             end,\r\n" + 
				"longitud = ?, latitud = ?, tilt_electric_field = ?, azimuth = ?, \r\n" + 
				"height = ?, tilt_mecanic_field = ?, tipo_antena = ?, potencia = ?, modo_transmision_celda = ?, logic_changes = ?, \r\n" + 
				"date_change = SYSDATE , usuario = ? , estado_cambio = ?, cambio_coment = ? \r\n" + 
				"WHERE site_id=(select max(site_id) from OPTI_SITES_SAVED)";			
								
		jdbcTemplate.update(query1update, 
				seguimiento.getCellname(), seguimiento.getLongitud(), seguimiento.getLatitud(), 
				seguimiento.getTilt_electric_field(), seguimiento.getAzimuth(), 
				seguimiento.getHeight(), seguimiento.getTilt_mecanic_field(), 
				seguimiento.getTipo_antena(), seguimiento.getPotencia(),seguimiento.getModo_transmision_celda(),
				seguimiento.getLogic_changes(),
				 seguimiento.getUsuario(), seguimiento.getEstado_cambio(), seguimiento.getCambio_coment());
		
		String query2 = "INSERT INTO OPTI_SITES_SAVED_HIST (site_id_hist, site_id, code_site, site_address, cellname,longitud, latitud,\r\n" + 
				"tilt_electric_field, azimuth, height, tilt_mecanic_field, tipo_antena, potencia, modo_transmision_celda, logic_changes, date_change, \r\n" + 
				"usuario, estado_cambio, cambio_coment) \r\n" + 
				"VALUES (OPTI_SITE_SEQ_HIST.NEXTVAL,(select max(site_id) from OPTI_SITES_SAVED),?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?,?)";

		jdbcTemplate.update(query2, seguimiento.getCode_site(), seguimiento.getSite_address(),
				seguimiento.getCellname(), seguimiento.getLongitud(), seguimiento.getLatitud(), 
				seguimiento.getTilt_electric_field(), seguimiento.getAzimuth(), 
				seguimiento.getHeight(), seguimiento.getTilt_mecanic_field(), 
				seguimiento.getTipo_antena(), seguimiento.getPotencia(),seguimiento.getModo_transmision_celda(),
				seguimiento.getLogic_changes(),
				 seguimiento.getUsuario(), seguimiento.getEstado_cambio(), seguimiento.getCambio_coment());
		
		String query2update = "UPDATE OPTI_SITES_SAVED_HIST SET code_site = case\r\n" + 
				"       --2G--\r\n" + 
				"  			when cellname like '%1' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%2' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%3' then substr(cellname,1,6) \r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%4' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%8' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%9' then substr(cellname,1,6)\r\n" + 
				"				when cellname like '%0' then substr(cellname,1,6)\r\n" + 
				"        --3G--\r\n" + 
				"        when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}[0-9]C[123][_][[:print:]]*$' , 'i') then substr(cellname,1,7)\r\n" + 
				"        --4G--\r\n" + 
				"        when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}[0-9]C[1234][_][[:print:]]*$' , 'i') then substr(cellname,1,7)\r\n" + 
				"     end ,  site_address = SUBSTR(CELLNAME, INSTR(CELLNAME, '_') + 1), cellname = ? ,\r\n" + 
				"sector = case \r\n" + 
				"            --2G--\r\n" + 
				"            when cellname like '%1' then 1\r\n" + 
				"            when cellname like '%2' then 2\r\n" + 
				"            when cellname like '%3' then 3\r\n" + 
				"            when cellname like '%4' then 4\r\n" + 
				"            when cellname like '%5' then 5\r\n" + 
				"            when cellname like '%6' then 6\r\n" + 
				"            when cellname like '%7' then 7\r\n" + 
				"            when cellname like '%8' then 8\r\n" + 
				"            when cellname like '%9' then 9\r\n" + 
				"            when cellname like '%0' then 0\r\n" + 
				"            --3G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}1C[123][_][[:print:]]*$' , 'i') then 1\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}2C[123][_][[:print:]]*$' , 'i') then 2\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}3C[123][_][[:print:]]*$' , 'i') then 3\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}4C[123][_][[:print:]]*$' , 'i') then 4\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}5C[123][_][[:print:]]*$' , 'i') then 5\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}6C[123][_][[:print:]]*$' , 'i') then 6\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}7C[123][_][[:print:]]*$' , 'i') then 7\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}8C[123][_][[:print:]]*$' , 'i') then 8                       \r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}9C[123][_][[:print:]]*$' , 'i') then 9\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{4}0C[123][_][[:print:]]*$' , 'i') then 0\r\n" + 
				"            --4G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}1C[1234][_][[:print:]]*$' , 'i') then 1\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}2C[1234][_][[:print:]]*$' , 'i') then 2\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}3C[1234][_][[:print:]]*$' , 'i') then 3  \r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}4C[1234][_][[:print:]]*$' , 'i') then 4\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}5C[1234][_][[:print:]]*$' , 'i') then 5\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}6C[1234][_][[:print:]]*$' , 'i') then 6\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}7C[1234][_][[:print:]]*$' , 'i') then 7\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}8C[1234][_][[:print:]]*$' , 'i') then 8\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}9C[1234][_][[:print:]]*$' , 'i') then 9\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{4}0C[1234][_][[:print:]]*$' , 'i') then 0\r\n" + 
				"         end, \r\n" + 
				"band = case \r\n" + 
				"            --2G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[0-9]{4}[_][[:print:]]*[1234]$' , 'i')\r\n" + 
				"            then '1900'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[0-9]{4}[_][[:print:]]*[089]$' , 'i')\r\n" + 
				"            then '850'            \r\n" + 
				"            --3G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{5}C[12][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '850'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{5}C[3][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '1900'\r\n" + 
				"            --4G--\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[1][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '1900'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[23][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '2600'\r\n" + 
				"            when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[4][_][[:print:]]*$' , 'i')\r\n" + 
				"            then '700'\r\n" + 
				"         end, \r\n" + 
				"\r\n" + 
				"tecnologia = case\r\n" + 
				"             --2G--\r\n" + 
				"             when regexp_like (cellname, '^[a-z]{2}[0-9]{4}[_][[:print:]]*[0-9]$' , 'i')\r\n" + 
				"             then '2G'\r\n" + 
				"             --3G--\r\n" + 
				"             when regexp_like (cellname, '^[a-z]{2}[UPI][0-9]{5}C[123][_][[:print:]]*$' , 'i')\r\n" + 
				"             then '3G'\r\n" + 
				"             --4G--   \r\n" + 
				"             when regexp_like (cellname, '^[a-z]{2}[LNT][0-9]{5}C[1234][_][[:print:]]*$' , 'i')\r\n" + 
				"             then '4G'\r\n" + 
				"             end,\r\n" + 
				"longitud = ?, latitud = ?, tilt_electric_field = ?, azimuth = ?, \r\n" + 
				"height = ?, tilt_mecanic_field = ?, tipo_antena = ?, potencia = ?, modo_transmision_celda = ?, logic_changes = ?, \r\n" + 
				"date_change = SYSDATE , usuario = ? , estado_cambio = ?, cambio_coment = ? \r\n" +  
				"WHERE site_id_hist=(select max(site_id_hist) from OPTI_SITES_SAVED_HIST)";
		
		jdbcTemplate.update(query2update, 
				seguimiento.getCellname(), seguimiento.getLongitud(), seguimiento.getLatitud(), 
				seguimiento.getTilt_electric_field(), seguimiento.getAzimuth(), 
				seguimiento.getHeight(), seguimiento.getTilt_mecanic_field(), 
				seguimiento.getTipo_antena(), seguimiento.getPotencia(),seguimiento.getModo_transmision_celda(),
				seguimiento.getLogic_changes(),
				 seguimiento.getUsuario(), seguimiento.getEstado_cambio(), seguimiento.getCambio_coment());

	}
	
	@Override
	public void updateSeguimiento(Seguimiento seguimiento) {

		String query = "UPDATE OPTI_SITES_SAVED SET code_site = ?, site_address = ?, cellname = ?, sector = ?, band = ?, tecnologia = ?, longitud = ?, latitud = ?,"
				+ "tilt_electric_field = ?, azimuth = ?, height = ?, tilt_mecanic_field = ?, tipo_antena = ?, potencia = ?, modo_transmision_celda = ?, logic_changes = ?, date_change = SYSDATE , usuario = ? , estado_cambio = ? , cambio_coment = ?"				
				+ "WHERE site_id=?";			
								
		jdbcTemplate.update(query, seguimiento.getCode_site(), seguimiento.getSite_address(),
				seguimiento.getCellname(), seguimiento.getSector(), seguimiento.getBand(), 
				seguimiento.getTecnologia(), seguimiento.getLongitud(), seguimiento.getLatitud(), 
				seguimiento.getTilt_electric_field(), seguimiento.getAzimuth(), seguimiento.getHeight(), 
				seguimiento.getTilt_mecanic_field(), 
				seguimiento.getTipo_antena(), seguimiento.getPotencia(),seguimiento.getModo_transmision_celda(),
				seguimiento.getLogic_changes(), seguimiento.getUsuario(), 
				seguimiento.getEstado_cambio(), seguimiento.getCambio_coment(), seguimiento.getSite_id());

		
		
		String query2 = "INSERT INTO OPTI_SITES_SAVED_HIST (site_id_hist, site_id, code_site, site_address, cellname, sector, band, tecnologia, longitud, latitud, tilt_electric_field, azimuth, \r\n" + 
				"height, tilt_mecanic_field, tipo_antena, potencia, modo_transmision_celda, logic_changes, date_change, usuario, estado_cambio, cambio_coment) VALUES (OPTI_SITE_SEQ_HIST.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?,?)";

		jdbcTemplate.update(query2, seguimiento.getSite_id(), seguimiento.getCode_site(), seguimiento.getSite_address(),
				seguimiento.getCellname(), seguimiento.getSector(), seguimiento.getBand(), seguimiento.getTecnologia(), 
				seguimiento.getLongitud(), seguimiento.getLatitud(), seguimiento.getTilt_electric_field(), seguimiento.getAzimuth(), 
				seguimiento.getHeight(), seguimiento.getTilt_mecanic_field(), 
				seguimiento.getTipo_antena(), seguimiento.getPotencia(),seguimiento.getModo_transmision_celda(),
				seguimiento.getLogic_changes(),
				seguimiento.getUsuario(), seguimiento.getEstado_cambio(), seguimiento.getCambio_coment());

	}

	@Override
	public void deleteSeguimiento(int id) {

		String query = "DELETE FROM OPTI_SITES_SAVED WHERE site_id = ?";

		jdbcTemplate.update(query, id);
	}
	
	
	
	/*KPI individual 2G*/
	@Override
	public Seguimiento kpiIndividualID2G(int id) {

		String query = "select a.site_id_hist, b.result_time,a.cellname,a.tecnologia,\r\n" + 
				"b.ACC_CONGESTION_CS_GSM , --ACC_CS\r\n" + 
				"b.ACC_PS_GSM , --EL MENOR ACC CONGESTION O ACC RADIO\r\n" + 
				"b.ACC_CONGESTION_PS_GSM  , b.ACC_RADIO_PS_GSM , \r\n" + 
				"b.RET_CS_GSM ,b.RET_PS_GSM ,\r\n" + 
				"b.SCDCH_DROP_GSM ,b.SCDCH_BLOCK_GSM ,\r\n" + 
				"b.TCH_DENIED_GSM ,\r\n" + 
				"b.TRAFICO_DATOS_DL_GSM ,b.TRAFICO_DATOS_UL_GSM ,\r\n" + 
				"b.TRAFICO_VOZ_GSM ,	b.TRAFICO_ERL_GSM ,\r\n" + 
				"b.TA_MEN_0_55_KM, b.TA_0_55_TA_1_10_KM, b.TA_1_10_TA_1_65_KM, b.TA_1_65_TA_2_20_KM,\r\n" + 
				"b.AGRUP_TA_2_20_TA_4_40_KM, b.AGRUP_TA_4_40_TA_5_50_KM, b.AGRUP_TA_5_50_TA_9_90_KM,\r\n" + 
				"b.AGRUP_TA_9_90_TA_20_35_KM, b.AGRUP_TA_20_35_TA_34_65_KM, b.TA_MAY_34_65_KM\r\n" + 
				"from ( \r\n" + 
				"       select trunc(date_change,'hh24') result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from OPTI_SITES_SAVED_HIST          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_2g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"a.cellname=b.cellname	\r\n" + 
				"order by b.result_time asc";
		

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento kpi_individual_id_2g = jdbcTemplate.queryForObject(query, rowMapper, id);

		return kpi_individual_id_2g;
		
	}
	/**/
	
	/*KPI HISTORICO 2G*/
	@Override
	public List<Seguimiento> kpiListID2G(int id) {

		String query = "select to_char(a.result_date, 'DD/MM/YYYY HH24:MI:SS') result_date, a.site_id_hist, a.cellname, a.tecnologia,\r\n" + 
				"        b.ACC_CONGESTION_CS_GSM , --ACC_CS\r\n" + 
				"        b.ACC_PS_GSM , --EL MENOR ACC CONGESTION O ACC RADIO\r\n" + 
				"        b.ACC_CONGESTION_PS_GSM  , b.ACC_RADIO_PS_GSM , \r\n" + 
				"        b.RET_CS_GSM ,b.RET_PS_GSM ,\r\n" + 
				"        b.SCDCH_DROP_GSM ,b.SCDCH_BLOCK_GSM ,\r\n" + 
				"        b.TCH_DENIED_GSM ,\r\n" + 
				"        b.TRAFICO_DATOS_DL_GSM ,b.TRAFICO_DATOS_UL_GSM ,\r\n" + 
				"        b.TRAFICO_VOZ_GSM ,	b.TRAFICO_ERL_GSM ,\r\n" + 
				"        b.TA_MEN_0_55_KM, b.TA_0_55_TA_1_10_KM, b.TA_1_10_TA_1_65_KM, b.TA_1_65_TA_2_20_KM,\r\n" + 
				"        b.AGRUP_TA_2_20_TA_4_40_KM, b.AGRUP_TA_4_40_TA_5_50_KM, b.AGRUP_TA_5_50_TA_9_90_KM,\r\n" + 
				"        b.AGRUP_TA_9_90_TA_20_35_KM, b.AGRUP_TA_20_35_TA_34_65_KM, b.TA_MAY_34_65_KM\r\n" + 
				"from ( \r\n" + 
				"       select result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from opti_vw_site_track_hxh          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_2g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"   a.cellname=b.cellname	\r\n" + 
				"order by a.result_date asc";
		
 
		//RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		/*Seguimiento seguimiento = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento;*/
		
		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;
		
	}
	/**/
	
	/*KPI HISTORICO 2G MAX AND MIN*/
	@Override
	public Seguimiento kpiListID2GMaxMin(int id) {

		String query = "select cellname,max_result_time,min_result_time, \r\n" + 
				"--ACC_CONGESTION_CS\r\n" + 
				"max_ACC_CONGESTION_CS_GSM,\r\n" + 
				"min_ACC_CONGESTION_CS_GSM,\r\n" + 
				"--ACC_PS_GSM\r\n" + 
				"max_ACC_PS_GSM,\r\n" + 
				"min_ACC_PS_GSM,\r\n" + 
				"--ACC_CONGESTION_PS_GSM and ACC_RADIO_PS_GSM\r\n" + 
				"greatest(max_ACC_CONGESTION_PS_GSM, min_ACC_CONGESTION_PS_GSM,max_ACC_RADIO_PS_GSM, min_ACC_RADIO_PS_GSM) \r\n" + 
				"as max_ACC_CONG_RAD_PS_GSM,\r\n" + 
				"LEAST(max_ACC_CONGESTION_PS_GSM, min_ACC_CONGESTION_PS_GSM,max_ACC_RADIO_PS_GSM, min_ACC_RADIO_PS_GSM)  \r\n" + 
				"as min_ACC_CONG_RAD_PS_GSM,\r\n" + 
				"--RET_CS_GSM and RET_PS_GSM  \r\n" + 
				"greatest(max_RET_CS_GSM, min_RET_CS_GSM,max_RET_PS_GSM, min_RET_PS_GSM) \r\n" + 
				"as max_RET_GSM,\r\n" + 
				"LEAST(max_RET_CS_GSM, min_RET_CS_GSM,max_RET_PS_GSM, min_RET_PS_GSM)  \r\n" + 
				"as min_RET_GSM,\r\n" + 
				"--SCDCH_DROP_GSM and SCDCH_BLOCK_GSM\r\n" + 
				"greatest(max_SCDCH_DROP_GSM, min_SCDCH_DROP_GSM,max_SCDCH_BLOCK_GSM, min_SCDCH_BLOCK_GSM) \r\n" + 
				"as max_SCDCH_GSM,\r\n" + 
				"LEAST(max_SCDCH_DROP_GSM, min_SCDCH_DROP_GSM,max_SCDCH_BLOCK_GSM, min_SCDCH_BLOCK_GSM) \r\n" + 
				"as min_SCDCH_GSM,\r\n" + 
				"--TCH_DENIED_GSM\r\n" + 
				"max_TCH_DENIED_GSM, min_TCH_DENIED_GSM,\r\n" + 
				"--TRAFICO DATOS DL Y UL\r\n" + 
				"greatest(max_TRAFICO_DATOS_DL_GSM, min_TRAFICO_DATOS_DL_GSM, max_TRAFICO_DATOS_UL_GSM, min_TRAFICO_DATOS_UL_GSM) \r\n" + 
				"as max_TRAFICO_DATOS_GSM,\r\n" + 
				"LEAST(max_TRAFICO_DATOS_DL_GSM, min_TRAFICO_DATOS_DL_GSM, max_TRAFICO_DATOS_UL_GSM, min_TRAFICO_DATOS_UL_GSM)  \r\n" + 
				"as min_TRAFICO_DATOS_GSM,\r\n" + 
				"--TRAFICO VOZ\r\n" + 
				"max_TRAFICO_VOZ_GSM,min_TRAFICO_VOZ_GSM,\r\n" + 
				"--TRAFICO ERL\r\n" + 
				"max_TRAFICO_ERL_GSM, min_TRAFICO_ERL_GSM,\r\n" + 
				"MAX_TA_GMS_AGRUP,\r\n" + 
				"MIN_TA_GMS_AGRUP\r\n" + 
				"FROM \r\n" + 
				"(\r\n" + 
				"select cellname,max(result_time) max_result_time,min(result_time) min_result_time,\r\n" + 
				"       max(ACC_CONGESTION_CS_GSM) max_ACC_CONGESTION_CS_GSM, min(ACC_CONGESTION_CS_GSM) min_ACC_CONGESTION_CS_GSM, \r\n" + 
				"       max(ACC_PS_GSM) max_ACC_PS_GSM, min(ACC_PS_GSM) min_ACC_PS_GSM,\r\n" + 
				"       max(ACC_CONGESTION_PS_GSM) max_ACC_CONGESTION_PS_GSM, min(ACC_CONGESTION_PS_GSM) min_ACC_CONGESTION_PS_GSM,\r\n" + 
				"       max(ACC_RADIO_PS_GSM) max_ACC_RADIO_PS_GSM, min(ACC_RADIO_PS_GSM) min_ACC_RADIO_PS_GSM,\r\n" + 
				"       max(RET_CS_GSM) max_RET_CS_GSM, min(RET_CS_GSM) min_RET_CS_GSM,\r\n" + 
				"       max(RET_PS_GSM) max_RET_PS_GSM, min(RET_PS_GSM) min_RET_PS_GSM,\r\n" + 
				"       max(SCDCH_DROP_GSM) max_SCDCH_DROP_GSM, min(SCDCH_DROP_GSM) min_SCDCH_DROP_GSM,\r\n" + 
				"       max(SCDCH_BLOCK_GSM) max_SCDCH_BLOCK_GSM, min(SCDCH_BLOCK_GSM) min_SCDCH_BLOCK_GSM,\r\n" + 
				"       max(TCH_DENIED_GSM) max_TCH_DENIED_GSM, min(TCH_DENIED_GSM) min_TCH_DENIED_GSM,\r\n" + 
				"       max(TRAFICO_DATOS_DL_GSM) max_TRAFICO_DATOS_DL_GSM, min(TRAFICO_DATOS_DL_GSM) min_TRAFICO_DATOS_DL_GSM,\r\n" + 
				"       max(TRAFICO_DATOS_UL_GSM) max_TRAFICO_DATOS_UL_GSM, min(TRAFICO_DATOS_UL_GSM) min_TRAFICO_DATOS_UL_GSM,\r\n" + 
				"       max(TRAFICO_VOZ_GSM) max_TRAFICO_VOZ_GSM, min(TRAFICO_VOZ_GSM) min_TRAFICO_VOZ_GSM,\r\n" + 
				"       max(TRAFICO_ERL_GSM) max_TRAFICO_ERL_GSM, min(TRAFICO_ERL_GSM) min_TRAFICO_ERL_GSM,\r\n" + 
				"       max(MAX_TA_GMS_AGRUP) MAX_TA_GMS_AGRUP,\r\n" + 
				"       min(MIN_TA_GMS_AGRUP) MIN_TA_GMS_AGRUP\r\n" + 
				"       from\r\n" + 
				"(\r\n" + 
				"SELECT to_char(b.result_time, 'DD/MM/YYYY HH24:MI:SS') result_time,/*BSC_NAME,BTS_NAME,SEGMENT_NAME,*/a.CELLNAME,b.tecnologia,\r\n" + 
				"  b.ACC_CONGESTION_CS_GSM , --ACC_CS\r\n" + 
				"  b.ACC_PS_GSM , --EL MENOR ACC CONGESTION O ACC RADIO\r\n" + 
				"  b.ACC_CONGESTION_PS_GSM  , --\r\n" + 
				"  b.ACC_RADIO_PS_GSM , --\r\n" + 
				"  b.RET_CS_GSM ,\r\n" + 
				"  b.RET_PS_GSM ,\r\n" + 
				"  b.SCDCH_DROP_GSM ,\r\n" + 
				"  b.SCDCH_BLOCK_GSM ,\r\n" + 
				"  b.TCH_DENIED_GSM ,\r\n" + 
				"  b.TRAFICO_DATOS_DL_GSM ,\r\n" + 
				"  b.TRAFICO_DATOS_UL_GSM ,\r\n" + 
				"  b.TRAFICO_VOZ_GSM ,	\r\n" + 
				"  b.TRAFICO_ERL_GSM ,\r\n" + 
				"       --timing advance\r\n" + 
				"       greatest(TA_MEN_0_55_KM, TA_0_55_TA_1_10_KM, TA_1_10_TA_1_65_KM, TA_1_65_TA_2_20_KM,\r\n" + 
				"       AGRUP_TA_2_20_TA_4_40_KM, AGRUP_TA_4_40_TA_5_50_KM, AGRUP_TA_5_50_TA_9_90_KM,\r\n" + 
				"       AGRUP_TA_9_90_TA_20_35_KM, AGRUP_TA_20_35_TA_34_65_KM, TA_MAY_34_65_KM) as MAX_TA_GMS_AGRUP,\r\n" + 
				"       LEAST(TA_MEN_0_55_KM, TA_0_55_TA_1_10_KM, TA_1_10_TA_1_65_KM, TA_1_65_TA_2_20_KM,\r\n" + 
				"       AGRUP_TA_2_20_TA_4_40_KM, AGRUP_TA_4_40_TA_5_50_KM, AGRUP_TA_5_50_TA_9_90_KM,\r\n" + 
				"       AGRUP_TA_9_90_TA_20_35_KM, AGRUP_TA_20_35_TA_34_65_KM, TA_MAY_34_65_KM) as MIN_TA_GMS_AGRUP\r\n" + 
				"from ( \r\n" + 
				"       select result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from opti_vw_site_track_hxh          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_2g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"   a.cellname=b.cellname	\r\n" + 
				"order by b.result_time asc\r\n" + 
				")\r\n" + 
				"group by cellname\r\n" + 
				")";
		

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento seguimiento_kpi_2g_maxmin = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento_kpi_2g_maxmin;
		
		/*RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;*/
		
	}
	/**/
	
	
	/*KPI individual 3G*/
	@Override
	public Seguimiento kpiIndividualID3G(int id) {

		String query = "select a.site_id_hist, b.result_time,a.cellname,a.tecnologia,\r\n" + 
				"       b.acc_cs,  b.acc_ps, b.ret_cs, b.ret_ps, b.throuput_dl, b.dch_users, b.trafico_voz_erlang,	\r\n" + 
				"       b.trafico_datos_dl, b.trafico_datos_ul, b.throuput_ul,\r\n" + 
				"       b.VS_TP_UE_0,  b.VS_TP_UE_1,  b.VS_TP_UE_2,\r\n" + 
				"       b.VS_TP_UE_3,  b.VS_TP_UE_4,  b.VS_TP_UE_5,\r\n" + 
				"       b.VS_TP_UE_6_9,  b.VS_TP_UE_10_15,\r\n" + 
				"       b.VS_TP_UE_16_25,  b.VS_TP_UE_26_35,\r\n" + 
				"       b.VS_TP_UE_36_55,  b.VS_TP_UE_MORE55 \r\n" + 
				"from ( \r\n" + 
				"       select trunc(date_change,'hh24') result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from OPTI_SITES_SAVED_HIST          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_3g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"a.cellname=b.cellname	\r\n" + 
				"order by b.result_time asc";
		

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento kpi_individual_id_3g = jdbcTemplate.queryForObject(query, rowMapper, id);

		return kpi_individual_id_3g;
		
	}
	/**/
	
	/*KPI HISTORICO 3G*/
	@Override
	public List<Seguimiento> kpiListID3G(int id) {

		String query = "select to_char(a.result_date, 'DD/MM/YYYY HH24:MI:SS') result_date, a.site_id_hist, a.cellname, a.tecnologia,\r\n" + 
				"       b.acc_cs,  b.acc_ps, b.ret_cs, b.ret_ps, b.throuput_dl, b.dch_users, b.trafico_voz_erlang,	\r\n" + 
				"       b.trafico_datos_dl, b.trafico_datos_ul, b.throuput_ul,\r\n" + 
				"       b.VS_TP_UE_0,  b.VS_TP_UE_1,  b.VS_TP_UE_2,\r\n" + 
				"       b.VS_TP_UE_3,  b.VS_TP_UE_4,  b.VS_TP_UE_5,\r\n" + 
				"       b.VS_TP_UE_6_9,  b.VS_TP_UE_10_15,\r\n" + 
				"       b.VS_TP_UE_16_25,  b.VS_TP_UE_26_35,\r\n" + 
				"       b.VS_TP_UE_36_55,  b.VS_TP_UE_MORE55 \r\n" + 
				"from ( \r\n" + 
				"       select result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from opti_vw_site_track_hxh          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_3g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"   a.cellname=b.cellname	\r\n" + 
				"order by a.result_date asc";
		

		//RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		/*Seguimiento seguimiento = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento;*/
		
		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;
		
	}
	/**/
	
	/*KPI HISTORICO 3G MAX AND MIN*/
	@Override
	public Seguimiento kpiListID3GMaxMin(int id) {

		String query = "select cellname,max_result_time,min_result_time, \r\n" + 
				"case --acc\r\n" + 
				"  when max_acc_cs >= max_acc_ps then max_acc_cs\r\n" + 
				"  else max_acc_ps\r\n" + 
				"end max_acc, \r\n" + 
				"case \r\n" + 
				"  when min_acc_cs <= min_acc_ps then min_acc_cs\r\n" + 
				"  else min_acc_ps\r\n" + 
				"end min_acc, \r\n" + 
				"case --ret\r\n" + 
				"  when max_ret_cs >= max_ret_ps then max_ret_cs\r\n" + 
				"  else max_ret_ps\r\n" + 
				"end max_ret, \r\n" + 
				"case \r\n" + 
				"  when min_ret_cs <= min_ret_ps then min_ret_cs\r\n" + 
				"  else min_ret_ps\r\n" + 
				"end min_ret, \r\n" + 
				"case --throuput\r\n" + 
				"  when max_throuput_ul >= max_throuput_dl then max_throuput_ul\r\n" + 
				"  else max_throuput_dl\r\n" + 
				"end max_throuput, \r\n" + 
				"case \r\n" + 
				"  when min_throuput_ul <= min_throuput_dl then min_throuput_ul\r\n" + 
				"  else min_throuput_dl\r\n" + 
				"end min_throuput,       \r\n" + 
				"max_dch_users, min_dch_users, \r\n" + 
				"max_trafico_voz_erlang, min_trafico_voz_erlang,\r\n" + 
				"case --trafico_datos\r\n" + 
				"  when max_trafico_datos_dl >= max_trafico_datos_ul then max_trafico_datos_dl\r\n" + 
				"  else max_trafico_datos_ul\r\n" + 
				"end max_trafico_datos, \r\n" + 
				"case \r\n" + 
				"  when min_trafico_datos_dl <= min_trafico_datos_ul then min_trafico_datos_dl\r\n" + 
				"  else min_trafico_datos_ul\r\n" + 
				"end min_trafico_datos,\r\n" + 
				"MAX_VS_TP_UE,\r\n" + 
				"MIN_VS_TP_UE\r\n" + 
				"  from\r\n" + 
				"( \r\n" + 
				"select cellname,max(result_time) max_result_time,min(result_time) min_result_time,\r\n" + 
				"       max(acc_cs) max_acc_cs, min(acc_cs) min_acc_cs, \r\n" + 
				"       max(acc_ps) max_acc_ps, min(acc_ps) min_acc_ps, \r\n" + 
				"       max(ret_cs) max_ret_cs, min(ret_cs) min_ret_cs, \r\n" + 
				"       max(ret_ps) max_ret_ps, min(ret_ps) min_ret_ps, \r\n" + 
				"       max(throuput_dl) max_throuput_dl, min(throuput_dl) min_throuput_dl,\r\n" + 
				"       max(dch_users) max_dch_users, min(dch_users) min_dch_users,\r\n" + 
				"       max(trafico_voz_erlang) max_trafico_voz_erlang, min(trafico_voz_erlang) min_trafico_voz_erlang,	\r\n" + 
				"       max(trafico_datos_dl) max_trafico_datos_dl, min(trafico_datos_dl) min_trafico_datos_dl, \r\n" + 
				"       max(trafico_datos_ul) max_trafico_datos_ul, min(trafico_datos_ul) min_trafico_datos_ul, \r\n" + 
				"       max(throuput_ul) max_throuput_ul, min(throuput_ul) min_throuput_ul,\r\n" + 
				"       max(MAX_VS_TP_UE) MAX_VS_TP_UE,\r\n" + 
				"       min(MIN_VS_TP_UE) MIN_VS_TP_UE\r\n" + 
				"       from\r\n" + 
				"(\r\n" + 
				"select a.site_id_hist, to_char(b.result_time, 'DD/MM/YYYY HH24:MI:SS') result_time,a.cellname,a.tecnologia,\r\n" + 
				"       b.acc_cs,  b.acc_ps, b.ret_cs, b.ret_ps, b.throuput_dl, b.dch_users, b.trafico_voz_erlang,	\r\n" + 
				"       b.trafico_datos_dl, b.trafico_datos_ul, b.throuput_ul,\r\n" + 
				"       b.VS_TP_UE_0,  b.VS_TP_UE_1,  b.VS_TP_UE_2,\r\n" + 
				"       b.VS_TP_UE_3,  b.VS_TP_UE_4,  b.VS_TP_UE_5,\r\n" + 
				"       b.VS_TP_UE_6_9,  b.VS_TP_UE_10_15,\r\n" + 
				"       b.VS_TP_UE_16_25,  b.VS_TP_UE_26_35,\r\n" + 
				"       b.VS_TP_UE_36_55,  b.VS_TP_UE_MORE55,\r\n" + 
				"       greatest( b.VS_TP_UE_0, b.VS_TP_UE_1, b.VS_TP_UE_2, b.VS_TP_UE_3, b.VS_TP_UE_4, b.VS_TP_UE_5,  \r\n" + 
				"                 b.VS_TP_UE_6_9, b.VS_TP_UE_10_15, b.VS_TP_UE_16_25, b.VS_TP_UE_26_35, b.VS_TP_UE_36_55,\r\n" + 
				"                 b.VS_TP_UE_MORE55) \r\n" + 
				"                 as MAX_VS_TP_UE,\r\n" + 
				"       LEAST( b.VS_TP_UE_0, b.VS_TP_UE_1, b.VS_TP_UE_2, b.VS_TP_UE_3, b.VS_TP_UE_4, b.VS_TP_UE_5,  \r\n" + 
				"              b.VS_TP_UE_6_9, b.VS_TP_UE_10_15, b.VS_TP_UE_16_25, b.VS_TP_UE_26_35, b.VS_TP_UE_36_55,\r\n" + 
				"              b.VS_TP_UE_MORE55) \r\n" + 
				"          as MIN_VS_TP_UE\r\n" + 
				"from ( \r\n" + 
				"       select result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from opti_vw_site_track_hxh          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_3g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"   a.cellname=b.cellname	\r\n" + 
				"order by b.result_time asc\r\n" + 
				")\r\n" + 
				"group by cellname\r\n" + 
				")";
		

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento seguimiento_kpi_3g_maxmin = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento_kpi_3g_maxmin;
		
		/*RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;*/
		
	}
	/**/
	
	/*KPI individual 4G*/
	@Override
	public Seguimiento kpiIndividualID4G(int id) {

		String query = "select a.site_id_hist, b.result_time,a.cellname,a.tecnologia,\r\n" + 
				"			 b.PRB_DL, b.PRB_UL,\r\n" + 
				"       b.accessibility, b.retainability, b.beusers, b.cqi, b.throuput_user_dl, b.throuput_user_ul,\r\n" + 
				"       b.DL_TRAFFIC_VOL_MB,b.UL_TRAFFIC_VOL_MB,\r\n" + 
				"       b.l_ra_ta_ue_index0,       b.l_ra_ta_ue_index1,       b.l_ra_ta_ue_index2,\r\n" + 
				"       b.l_ra_ta_ue_index3,       b.l_ra_ta_ue_index4,       b.l_ra_ta_ue_index5,\r\n" + 
				"       b.l_ra_ta_ue_index6,       b.l_ra_ta_ue_index7,       b.l_ra_ta_ue_index8,\r\n" + 
				"       b.l_ra_ta_ue_index9,       b.l_ra_ta_ue_index10,       b.l_ra_ta_ue_index11\r\n" + 
				"from ( \r\n" + 
				"       select trunc(date_change,'hh24') result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from OPTI_SITES_SAVED_HIST          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_4g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"a.cellname=b.cellname	\r\n" + 
				"order by b.result_time asc";
		

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento kpi_individual_id_4g = jdbcTemplate.queryForObject(query, rowMapper, id);

		return kpi_individual_id_4g;
		
	}
	/**/
	
	/*KPI HISTORICO 4G*/
	@Override
	public List<Seguimiento> kpiListID4G(int id) {

		String query = "select to_char(a.result_date, 'DD/MM/YYYY HH24:MI:SS') result_date, a.site_id_hist, a.cellname, a.tecnologia,\r\n" + 
				"			 b.PRB_DL, b.PRB_UL,\r\n" + 
				"       b.accessibility, b.retainability, b.beusers, b.cqi, b.throuput_user_dl, b.throuput_user_ul,\r\n" + 
				"       b.DL_TRAFFIC_VOL_MB,b.UL_TRAFFIC_VOL_MB,\r\n" + 
				"       b.l_ra_ta_ue_index0,       b.l_ra_ta_ue_index1,       b.l_ra_ta_ue_index2,\r\n" + 
				"       b.l_ra_ta_ue_index3,       b.l_ra_ta_ue_index4,       b.l_ra_ta_ue_index5,\r\n" + 
				"       b.l_ra_ta_ue_index6,       b.l_ra_ta_ue_index7,       b.l_ra_ta_ue_index8,\r\n" + 
				"       b.l_ra_ta_ue_index9,       b.l_ra_ta_ue_index10,       b.l_ra_ta_ue_index11\r\n" + 
				"from ( \r\n" + 
				"       select result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from opti_vw_site_track_hxh          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_4g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"   a.cellname=b.cellname	\r\n" + 
				"order by a.result_date asc";
		

		//RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		/*Seguimiento seguimiento = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento;*/
		
		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;
		
	}
	/**/
	
	/*KPI HISTORICO 4G MAX AND MIN*/
	@Override
	public Seguimiento kpiListID4GMaxMin(int id) {

		String query = "select cellname,max_result_time,min_result_time, \r\n" + 
				"case --PRB\r\n" + 
				"  when max_prb_dl >= max_prb_ul then max_prb_dl\r\n" + 
				"  else max_prb_ul\r\n" + 
				"end max_prb_lte,  \r\n" + 
				"case \r\n" + 
				"  when min_prb_dl <= min_prb_ul then min_prb_dl\r\n" + 
				"  else min_prb_ul\r\n" + 
				"end min_prb_lte,\r\n" + 
				"max_accessibility, min_accessibility,\r\n" + 
				"max_retainability, min_retainability,\r\n" + 
				"max_beusers, min_beusers,\r\n" + 
				"case --THROUPUT\r\n" + 
				"  when max_throuput_user_dl >= max_throuput_user_ul then max_throuput_user_dl\r\n" + 
				"  else max_throuput_user_ul\r\n" + 
				"end max_throuput_user_lte,  \r\n" + 
				"case \r\n" + 
				"  when min_throuput_user_dl <= min_throuput_user_ul then min_throuput_user_dl\r\n" + 
				"  else min_throuput_user_ul\r\n" + 
				"end min_throuput_user_lte,\r\n" + 
				"case --TRAFFIC_VOL_MB\r\n" + 
				"  when max_dl_traffic_vol_mb >= max_ul_traffic_vol_mb then max_dl_traffic_vol_mb\r\n" + 
				"  else max_ul_traffic_vol_mb\r\n" + 
				"end max_traffic_vol_mb_lte,  \r\n" + 
				"case \r\n" + 
				"  when min_dl_traffic_vol_mb <= min_ul_traffic_vol_mb then min_dl_traffic_vol_mb\r\n" + 
				"  else min_ul_traffic_vol_mb\r\n" + 
				"end min_traffic_vol_mb_lte,\r\n" + 
				"max_l_ra_ta_ue_index, min_l_ra_ta_ue_index\r\n" + 
				"  from\r\n" + 
				"(\r\n" + 
				"select cellname,max(result_time) max_result_time,min(result_time) min_result_time,\r\n" + 
				"       max(PRB_DL) max_prb_dl, min(PRB_DL) min_prb_dl, max(PRB_UL) max_prb_ul,\r\n" + 
				"       min(PRB_UL) min_prb_ul,\r\n" + 
				"       max(accessibility) max_accessibility, min(accessibility) min_accessibility, \r\n" + 
				"       max(retainability) max_retainability, min(retainability) min_retainability, \r\n" + 
				"       max(beusers) max_beusers, min(beusers) min_beusers, \r\n" + 
				"       max(cqi) max_cqi, min(cqi) min_cqi, \r\n" + 
				"       max(throuput_user_dl) max_throuput_user_dl, min(throuput_user_dl) min_throuput_user_dl, \r\n" + 
				"       max(throuput_user_ul) max_throuput_user_ul, min(throuput_user_ul) min_throuput_user_ul,\r\n" + 
				"       max(DL_TRAFFIC_VOL_MB) max_DL_TRAFFIC_VOL_MB,min(DL_TRAFFIC_VOL_MB) min_DL_TRAFFIC_VOL_MB,\r\n" + 
				"       max(UL_TRAFFIC_VOL_MB) max_UL_TRAFFIC_VOL_MB,min(UL_TRAFFIC_VOL_MB) min_UL_TRAFFIC_VOL_MB,\r\n" + 
				"       max(max_l_ra_ta_ue_index) max_l_ra_ta_ue_index , min(min_l_ra_ta_ue_index) min_l_ra_ta_ue_index\r\n" + 
				"       from\r\n" + 
				"(\r\n" + 
				"select a.site_id_hist, to_char(b.result_time, 'DD/MM/YYYY HH24:MI:SS') result_time,a.cellname,a.tecnologia,\r\n" + 
				"			 b.PRB_DL, b.PRB_UL,\r\n" + 
				"       b.accessibility, b.retainability, b.beusers, b.cqi, b.throuput_user_dl, b.throuput_user_ul,\r\n" + 
				"       b.DL_TRAFFIC_VOL_MB,b.UL_TRAFFIC_VOL_MB,\r\n" + 
				"       b.l_ra_ta_ue_index0,        b.l_ra_ta_ue_index1,        b.l_ra_ta_ue_index2,\r\n" + 
				"        b.l_ra_ta_ue_index3,        b.l_ra_ta_ue_index4,        b.l_ra_ta_ue_index5,\r\n" + 
				"        b.l_ra_ta_ue_index6,        b.l_ra_ta_ue_index7,        b.l_ra_ta_ue_index8,\r\n" + 
				"        b.l_ra_ta_ue_index9,        b.l_ra_ta_ue_index10,        b.l_ra_ta_ue_index11,\r\n" + 
				"      greatest(l_ra_ta_ue_index0,  l_ra_ta_ue_index1,  l_ra_ta_ue_index2,  l_ra_ta_ue_index3,\r\n" + 
				"                l_ra_ta_ue_index4,  l_ra_ta_ue_index5,  l_ra_ta_ue_index6,  l_ra_ta_ue_index7,\r\n" + 
				"                l_ra_ta_ue_index8,  l_ra_ta_ue_index9,  l_ra_ta_ue_index10,  l_ra_ta_ue_index11 ) \r\n" + 
				"                as max_l_ra_ta_ue_index,\r\n" + 
				"       LEAST(l_ra_ta_ue_index0,  l_ra_ta_ue_index1,  l_ra_ta_ue_index2,  l_ra_ta_ue_index3,\r\n" + 
				"             l_ra_ta_ue_index4,  l_ra_ta_ue_index5,  l_ra_ta_ue_index6,  l_ra_ta_ue_index7,\r\n" + 
				"             l_ra_ta_ue_index8,  l_ra_ta_ue_index9,  l_ra_ta_ue_index10,  l_ra_ta_ue_index11) \r\n" + 
				"          as min_l_ra_ta_ue_index    \r\n" + 
				"from ( \r\n" + 
				"       select result_date, site_id_hist, cellname, tecnologia \r\n" + 
				"       from opti_vw_site_track_hxh          	\r\n" + 
				"			 where SITE_ID_HIST = ? \r\n" + 
				"     ) a					\r\n" + 
				"left join opti_track_kpi_4g b						 \r\n" + 
				"on a.result_date=b.result_time and\r\n" + 
				"   a.cellname=b.cellname	\r\n" + 
				"order by b.result_time asc\r\n" + 
				")\r\n" + 
				"group by cellname\r\n" + 
				")";
		

		RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		Seguimiento seguimiento_kpi_4g_maxmin = jdbcTemplate.queryForObject(query, rowMapper, id);

		return seguimiento_kpi_4g_maxmin;
		
		/*RowMapper<Seguimiento> rowMapper = new BeanPropertyRowMapper<Seguimiento>(Seguimiento.class);

		List<Seguimiento> list = jdbcTemplate.query(query, rowMapper, id);

		return list;*/
		
	}
	/**/
	

}
