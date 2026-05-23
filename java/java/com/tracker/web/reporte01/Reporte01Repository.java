package com.tracker.web.reporte01;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface Reporte01Repository extends JpaRepository<Reporte01, String> {
	
	@Query(value ="SELECT * FROM tm_conf_actual_celda_4g r WHERE " +
			"LOWER(r.cellname) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mbts_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.enodob) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.enodebid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.enodob_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.enodob_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.site_address) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sector_name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.carrier) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.distintivo) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.celda_id) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.localcellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.phycellid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.fddtddind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.name) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freqband) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellactivestate) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.userlabel) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.txrxmode) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dlbandwidth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ulbandwidth) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dlearfcn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ulearfcn) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.bw) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freq_band) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.celladminstate) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.tac) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.prb) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.nbcellflag) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.objid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.additionalspectrumemission) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.aircellflag) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellradius) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cellspecificoffset) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cpricompression) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.crsportnum) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.csgind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.customizedbandwidthcfgind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.dlcyclicprefix) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.emergencyareaidcfgind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.highspeedflag) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.multirrucellflag) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.preamblefmt) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.qoffsetfreq) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.rootsequenceidx) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.uepowermaxcfgind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ulcyclicprefix) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.ulearfcncfgind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.workmode) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.eucellstandbymode) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.specialsubframepatterns) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.subframeassignment) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.cnopsharinggroupid) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.csirsperiod) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.freqpriorityforanr) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.intrafreqanrind) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.crsportmap) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.boardtype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.manufacturerdata) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.moduletype) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gummei_primario) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mme_usn_primario) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.gummei_secundario) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.mme_usn_secundario) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.sub_region) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.departamento) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.provincia) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.distrito) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.latitud) LIKE LOWER('%' || :searchTerm || '%') OR " + 
			"LOWER(r.longitud) LIKE LOWER('%' || :searchTerm || '%')" +
			"order by cellname asc", nativeQuery = true)
	Page<Reporte01> search(@Param("searchTerm") String searchTerm, Pageable pageable);
	
}
