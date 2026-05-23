package com.tracker.web.baseline.maestro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/baseline-admin") //ruta adicional
public class DataSqlController {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/carga-usuarios")
	public String showPage() {
	return "baseline/delete-and-copy";  // nombre del archivo HTML 
	}
	
	@GetMapping("/carga-baseline-usuarios/execute-sql")
	@ResponseBody
	public String executeSql() {
		try {
		jdbcTemplate.execute("DELETE FROM PLANI_BS_PARAMETROS_4G_USUARIOS");
		jdbcTemplate.execute("COMMIT");
		jdbcTemplate.execute("INSERT INTO PLANI_BS_PARAMETROS_4G_USUARIOS SELECT * FROM PLANI_BS_PARAMETROS_4G_DINAMIC");
		return "Se cargo al portal de usuarios el Baseline";
	} catch (Exception e) {
	return "Error executing SQL: " + e.getMessage();
	}
	}
	
}