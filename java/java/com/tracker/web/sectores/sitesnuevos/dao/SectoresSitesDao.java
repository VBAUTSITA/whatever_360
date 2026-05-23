package com.tracker.web.sectores.sitesnuevos.dao;


import com.tracker.web.sectores.sitesnuevos.dto.DetalleRow;
import com.tracker.web.sectores.sitesnuevos.dto.ResumenMatriz;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class SectoresSitesDao {
private final JdbcTemplate jdbc;


public SectoresSitesDao(JdbcTemplate jdbc) {
this.jdbc = jdbc;
}


// ===== Matriz: prioridad × tipo_sitio (cuenta sites únicos) =====
public List<ResumenMatriz> resumenMatriz() {
String sql =
"SELECT PRIORIDAD_SECTOR, TIPO_SITIO, COUNT(DISTINCT MBTS_NAME) AS CANTIDAD_SITES " +
"FROM VW_PLAN_SECTOR_SITES_NUEVOS GROUP BY PRIORIDAD_SECTOR, TIPO_SITIO";
return jdbc.query(sql, new RowMapper<ResumenMatriz>() {
@Override
public ResumenMatriz mapRow(ResultSet rs, int rowNum) throws SQLException {
ResumenMatriz r = new ResumenMatriz();
r.setPrioridadSector(rs.getString("PRIORIDAD_SECTOR"));
r.setTipoSitio(rs.getString("TIPO_SITIO"));
r.setCantidadSites(rs.getInt("CANTIDAD_SITES"));
return r;
}
});
}


// ===== Conteo real de sectores por prioridad (columna izquierda) =====
public Map<String, Integer> sectoresRealesPorPrioridad() {
String sql =
"SELECT prioridad_sector, COUNT(DISTINCT sector_name) AS cantidad_sectores " +
"FROM plan_map_sect_crit_4gweek GROUP BY prioridad_sector";
return jdbc.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
@Override
public Map<String, Integer> extractData(ResultSet rs) throws SQLException {
Map<String, Integer> out = new HashMap<String, Integer>();
while (rs.next()) {
out.put(rs.getString("prioridad_sector"), rs.getInt("cantidad_sectores"));
}
return out;
}
});
}


//===== Tipos de sitio para cabecera =====
public List<String> tiposSitio() {
String sql = "SELECT DISTINCT TIPO_SITIO FROM VW_PLAN_SECTOR_SITES_NUEVOS WHERE TIPO_SITIO IS NOT NULL ORDER BY 1";
return jdbc.query(sql, new RowMapper<String>() {
@Override public String mapRow(ResultSet rs, int rowNum) throws SQLException { return rs.getString(1); }
});
}


//===== Detalle por prioridad + tipo_sitio =====
public List<DetalleRow> detalle(String prioridad, String tipoSitio) {
String sql =
"SELECT JEFATURA, MBTS_NAME, ADRESS, SECTOR, PRIORIDAD_SECTOR, " +
" TIPO_SITIO, SOLUCION_REQ, OBSERVACION " +
"FROM VW_PLAN_SECTOR_SITES_NUEVOS " +
"WHERE PRIORIDAD_SECTOR = ? AND TIPO_SITIO = ? " +
"ORDER BY SECTOR, MBTS_NAME";
return jdbc.query(sql, new Object[]{prioridad, tipoSitio}, new RowMapper<DetalleRow>() {
@Override
public DetalleRow mapRow(ResultSet rs, int rowNum) throws SQLException {
DetalleRow d = new DetalleRow();
d.setJefatura(rs.getString("JEFATURA"));
d.setMbtsName(rs.getString("MBTS_NAME"));
d.setAdress(rs.getString("ADRESS"));
d.setSector(rs.getString("SECTOR"));
d.setPrioridadSector(rs.getString("PRIORIDAD_SECTOR"));
d.setTipoSitio(rs.getString("TIPO_SITIO"));
d.setSolucionReq(rs.getString("SOLUCION_REQ"));
d.setObservacion(rs.getString("OBSERVACION"));
return d;
}
});
}


//===== Detalle de sectores reales (click en cantidad(sectores)) =====
public List<String> detalleSectoresReales(String prioridad) {
String sql = "SELECT DISTINCT sector_name FROM plan_map_sect_crit_4gweek WHERE prioridad_sector = ? ORDER BY 1";
return jdbc.query(sql, new Object[]{prioridad}, new RowMapper<String>() {
@Override public String mapRow(ResultSet rs, int rowNum) throws SQLException { return rs.getString(1); }
});
}



//===== Sectores ASIGNADOS por prioridad (intersección oficial vs sites) =====
public Map<String, Integer> sectoresAsignadosPorPrioridad() {
 // Contamos sectores oficiales que ya aparecen asignados en la vista de sites
 // Match por prioridad y por nombre de sector TRIM/UPPER (evita fallos por espacios/mayúsculas)
 String sql =
     "SELECT a.prioridad_sector AS prioridad, " +
     "       COUNT(DISTINCT a.sector_name) AS asignados " +
     "FROM plan_map_sect_crit_4gweek a " +
     "JOIN VW_PLAN_SECTOR_SITES_NUEVOS b " +
     "  ON a.prioridad_sector = b.PRIORIDAD_SECTOR " +
     " AND UPPER(TRIM(a.sector_name)) = UPPER(TRIM(b.SECTOR)) " +
     "GROUP BY a.prioridad_sector";

 return jdbc.query(sql, new org.springframework.jdbc.core.ResultSetExtractor<Map<String,Integer>>() {
     @Override public Map<String, Integer> extractData(ResultSet rs) throws SQLException {
         Map<String,Integer> out = new java.util.HashMap<String,Integer>();
         while (rs.next()) {
             out.put(rs.getString("prioridad"), rs.getInt("asignados"));
         }
         return out;
     }
 });
}

//===== Detalle de NO ASIGNADOS (lista de sectores) =====
public java.util.List<String> detalleSectoresNoAsignados(String prioridad) {
 // Sectores oficiales de esa prioridad que NO existen en sites nuevos (misma prioridad)
 String sql =
     "SELECT DISTINCT a.sector_name " +
     "FROM plan_map_sect_crit_4gweek a " +
     "WHERE a.prioridad_sector = ? " +
     "  AND NOT EXISTS ( " +
     "      SELECT 1 FROM VW_PLAN_SECTOR_SITES_NUEVOS b " +
     "       WHERE b.PRIORIDAD_SECTOR = a.prioridad_sector " +
     "         AND UPPER(TRIM(b.SECTOR)) = UPPER(TRIM(a.sector_name)) " +
     "  ) " +
     "ORDER BY 1";
 return jdbc.query(sql, new Object[]{prioridad}, new org.springframework.jdbc.core.RowMapper<String>() {
     @Override public String mapRow(ResultSet rs, int rowNum) throws SQLException { return rs.getString(1); }
 });
}


//===== Detalle de ASIGNADOS (lista de sectores) =====
public java.util.List<String> detalleSectoresAsignados(String prioridad) {
 // Sectores oficiales de esa prioridad que SÍ existen en sites nuevos (misma prioridad)
 String sql =
     "SELECT DISTINCT a.sector_name " +
     "FROM plan_map_sect_crit_4gweek a " +
     "WHERE a.prioridad_sector = ? " +
     "  AND EXISTS ( " +
     "      SELECT 1 FROM VW_PLAN_SECTOR_SITES_NUEVOS b " +
     "       WHERE b.PRIORIDAD_SECTOR = a.prioridad_sector " +
     "         AND UPPER(TRIM(b.SECTOR)) = UPPER(TRIM(a.sector_name)) " +
     "  ) " +
     "ORDER BY 1";
 return jdbc.query(sql, new Object[]{prioridad},
     new org.springframework.jdbc.core.RowMapper<String>() {
         @Override public String mapRow(java.sql.ResultSet rs, int rowNum)
                 throws java.sql.SQLException {
             return rs.getString(1);
         }
     }
 );
}



}