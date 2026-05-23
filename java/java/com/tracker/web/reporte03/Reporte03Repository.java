package com.tracker.web.reporte03;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface Reporte03Repository extends JpaRepository<Reporte03, String> {
	
	@Query(value ="SELECT * FROM tm_conf_actual_celda_5g r WHERE " +
			"LOWER(r.rbts) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rbts_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rbts_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gnbid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.enodobid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gnodebfunctionname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gnodob_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gnodob_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sector_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.carrier) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.distintivo) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellactivestate) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nrcellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.duplexmode) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.classname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.subcarrierspacing) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.trackingareaid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ssbfreqpos) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.physicalcellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ulbandwidth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dlbandwidth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ssbperiod) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.logicalrootsequenceindex) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ssbdescmethod) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ulnarfcn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dlnarfcn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.frequencyband) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.celladminstate) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.tac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nrducellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nrducelltrpid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.txrxmode) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellradius) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cyclicprefixlength) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.lampsitecellflag) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nrducellactivestate) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nrducellname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.prachfreqstartposition) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rannotificationareaid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sib1period) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.slotassignment) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.slotstructure) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.taoffset) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sub_region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.departamento) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.provincia) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.distrito) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.latitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.longitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cluster_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.locipv4) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.direccion) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.codigo_sitio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nombre_sitio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.num_sector) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sector) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.codigo_nombre) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.azimuth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.height) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.electrical_tilt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mechanical_tilt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ubigeo_inei) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freq_band) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.jefatura_radio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bw) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.prb) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mbts) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mbts_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mbts_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freq_band_num) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.maxtransmitpower) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.id_tipo_cobertura) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.fecha_actualizacion) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.manufacturer_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.moduletype_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.serialnumber_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ubbp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.manufacturer_ubbp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.moduletype_ubbp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.serialnumber_ubbp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cabinetno_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.subrackno_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.slotno_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_antena) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.serialnumber_antena) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_bbu) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_rru_a_reportar) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cant_rrus) LIKE LOWER('%' || :searchTerm || '%') " + 
			"order by cellname asc", nativeQuery = true)
	Page<Reporte03> search(@Param("searchTerm") String searchTerm, Pageable pageable);
	
}
