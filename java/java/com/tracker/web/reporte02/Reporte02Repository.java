package com.tracker.web.reporte02;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface Reporte02Repository extends JpaRepository<Reporte02, String> {
	
	@Query(value ="SELECT * FROM tm_conf_actual_celda_3g r WHERE " +
			"LOWER(r.mbts) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.codigo_sitio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nombre_sitio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.central) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rncid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nodebname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nodeb_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nodeb_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sector_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.carrier) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.locell) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.maxtxpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.pscrambcode) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.lac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.tcell) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.actstatus) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bandind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.blkstatus) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.uarfcndownlink) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.uarfcnuplink) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freqband) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.hspdschcodenum) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.uarfcnuplinkind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cfgracind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cnopgrpindex) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ninsyncind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.noutsyncind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.trlfailure) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dltpcpattern01count) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.txdiversityind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.spgid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.srn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ssn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.vplimitind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dssflag) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dsssmallcovmaxtxpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cchcnopindex) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dpgid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sysdesc) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.syscontact) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.syslocation) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sysservices) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.supportrncinpool) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.loadsharingtype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.redundancytype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.boardtype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.manufacturerdata) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.moduletype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.central_master) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.central_backup) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sub_region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.departamento) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.provincia) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.distrito) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.latitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.longitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cluster_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.direccion) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.num_sector) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sector) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.boardtype_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.manufacturer_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.moduletype_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.serialnumber_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.codigo_nombre) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.azimuth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.height) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.electrical_tilt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mechanical_tilt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nodebid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ubigeo_inei) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.jefatura_radio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.hosttype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.phychid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.pcpichpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.maxpcpichpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.minpcpichpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dsssmallcovpcpichpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freq_band_num) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.id_tipo_cobertura) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.fecha_actualizacion) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.subrackno_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.slotno_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cabinetno_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cant_rrus) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_antena) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.serialnumber_antena) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_bbu) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_rru_a_reportar) LIKE LOWER('%' || :searchTerm || '%')" + 
			"order by cellname asc", nativeQuery = true)
	Page<Reporte02> search(@Param("searchTerm") String searchTerm, Pageable pageable);
	
}
