package com.tracker.web.seguimiento.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SeguimientoRowMapper implements RowMapper<Seguimiento> {

	@Override
	public Seguimiento mapRow(ResultSet rs, int rowNum) throws SQLException {

		Seguimiento seguimiento = new Seguimiento();

		seguimiento.setSite_id(rs.getInt("site_id"));
		seguimiento.setSite_id_hist(rs.getInt("site_id_hist"));
		seguimiento.setCode_site(rs.getString("code_site"));
		seguimiento.setSite_address(rs.getString("site_address"));
		seguimiento.setCellname(rs.getString("cellname"));
		seguimiento.setLongitud(rs.getFloat("longitud"));
		seguimiento.setLatitud(rs.getFloat("latitud"));
		seguimiento.setSector(rs.getString("sector"));
		seguimiento.setBand(rs.getString("band"));
		seguimiento.setTecnologia(rs.getString("tecnologia"));
		seguimiento.setTilt_electric_field(rs.getString("tilt_electric_field"));
		seguimiento.setAzimuth(rs.getString("azimuth"));
		seguimiento.setHeight(rs.getString("height"));
		seguimiento.setTilt_mecanic_field(rs.getString("tilt_mecanic_field"));
		seguimiento.setTipo_antena(rs.getString("tipo_antena"));
		seguimiento.setPotencia(rs.getString("potencia"));
		seguimiento.setModo_transmision_celda(rs.getString("modo_transmision_celda"));
		seguimiento.setLogic_changes(rs.getString("logic_changes"));
		seguimiento.setDate_change(rs.getDate("date_change"));
		seguimiento.setUsuario(rs.getString("usuario"));
		seguimiento.setFecha(rs.getString("fecha"));
		seguimiento.setEstado_cambio(rs.getString("estado_cambio"));
		seguimiento.setCambio_coment(rs.getString("cambio_coment"));
		

		return seguimiento;
	}

}
