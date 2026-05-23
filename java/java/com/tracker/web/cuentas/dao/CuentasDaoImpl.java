package com.tracker.web.cuentas.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.web.cuentas.model.Cuentas;
import com.tracker.web.cuentas.model.CuentasRowMapper;

@Transactional
@Repository
public class CuentasDaoImpl implements CuentasDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	@Override
	public void addCuentas(Cuentas cuentas) {

		String query = "INSERT INTO opti_tracker_cuentas (id_usuario, codigo_usuario, nombre_usuario, apellidos_usuario, correo_usuario, clave_usuario,horario_registrado,clave_usuario_bcry) "
				+ "VALUES (OPT_CUENTA_SEQ.NEXTVAL,?,?,?,?,?,SYSDATE,?)";

		jdbcTemplate.update(query,  cuentas.getCodigo_usuario(), cuentas.getNombre_usuario(), 
									cuentas.getApellidos_usuario(), cuentas.getCorreo_usuario(), cuentas.getClave_usuario(),
									cuentas.getClave_usuario_bcry()
				);
		
	}
}