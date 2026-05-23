package com.tracker.web.cuentas.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class CuentasRowMapper implements RowMapper<Cuentas> {

	@Override
	public Cuentas mapRow(ResultSet rs, int rowNum) throws SQLException {

		Cuentas cuentas = new Cuentas();
		cuentas.setId_usuario(rs.getInt("id_usuario"));
		cuentas.setCodigo_usuario(rs.getString("codigo_usuario"));
		cuentas.setNombre_usuario(rs.getString("nombre_usuario"));
		cuentas.setApellidos_usuario(rs.getString("apellidos_usuario"));
		cuentas.setCorreo_usuario(rs.getString("correo_usuario"));
		cuentas.setClave_usuario(rs.getString("clave_usuario"));
		
		cuentas.setHorario_registrado(rs.getDate("horario_registrado"));
		cuentas.setClave_usuario_bcry(rs.getString("clave_usuario_bcry"));
		

		return cuentas;
	}
}
