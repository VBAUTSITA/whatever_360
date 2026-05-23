package com.tracker.web.reporte04;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Reporte04Repository extends JpaRepository<Reporte04, String> {
	
	@Query(value ="SELECT * FROM vw_plani_reporte_2g_tm r WHERE " +
			"LOWER(r.bsc_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mbts_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.btsname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.segment_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ci) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.lac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.trxid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.trxname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freq) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.trxno) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.idtype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ismainbcch) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.istmptrx) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gtrxgroupid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.tch_total) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.pdtch_total) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mcc) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mnc) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ncc) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bcc) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.exttp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.iuotp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.flexmaio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.csvsp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.csdsp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.pshpsp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.pslpsvp) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bspbcchblks) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bspagblksres) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bsprachblks) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.type) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.opname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.vipcell) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mocncmcell) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.hybhifreqbandsupport) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.glocellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nsei) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bvci) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sysdesc) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.syscontact) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.syslocation) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sysservices) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.admstat) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.distrito) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.provincia) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.departamento) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sub_region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cluster_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.actstatus) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.direccion) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_rru) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.manufacturerdata) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ubigeo_inei) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.jefatura_radio) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.azimuth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.height) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.latitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.longitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.electrical_tilt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mechanical_tilt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gbts_power_type) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.egbts_power_type) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_antena) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.modelo_rru_a_reportar) LIKE LOWER('%' || :searchTerm || '%')" + 
			"", nativeQuery = true)
	Page<Reporte04> search(@Param("searchTerm") String searchTerm, Pageable pageable);
	
}
