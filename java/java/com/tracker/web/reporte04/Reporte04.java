package com.tracker.web.reporte04;

import javax.persistence.*;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "vw_plani_reporte_2g_tm")
public class Reporte04 {
	
	@Id
	private String cellname;
	private String bsc_name;
	private String mbts_name;
	private String btsname;
	private String site_name;
	private String site_address;
	private String segment_name;
	private String cellid;
	
	private String ci;
	private String lac;
	private String rac;
	private String trxid;
	private String trxname;
	private String freq;
	private String trxno;
	private String idtype;
	private String ismainbcch;
	private String istmptrx;
	private String gtrxgroupid;
	private String tch_total;
	private String pdtch_total;
	private String mcc;
	private String mnc;
	private String ncc;
	private String bcc;
	private String exttp;
	private String iuotp;
	private String flexmaio;
	private String csvsp;
	private String csdsp;
	private String pshpsp;
	private String pslpsvp;
	private String bspbcchblks;
	private String bspagblksres;
	private String bsprachblks;
	private String type;
	private String opname;
	private String vipcell;
	private String mocncmcell;
	private String hybhifreqbandsupport;
	private String glocellid;
	private String nsei;
	private String bvci;
	private String sysdesc;
	private String syscontact;
	private String syslocation;
	private String sysservices;
	private String admstat;
	private String distrito;
	private String provincia;
	private String departamento;
	private String sub_region;
	private String cluster_name;
	private String actstatus;
	private String direccion;
	private String modelo_rru;
	private String manufacturerdata;
	private String ubigeo_inei;
	private String jefatura_radio;
	private String azimuth;
	private String height;
	private String latitud;
	private String longitud;
	private String electrical_tilt;
	private String mechanical_tilt;
	private String gbts_power_type;
	private String egbts_power_type;
	private String modelo_antena;
	private String modelo_rru_a_reportar;
	
	public String getCellname() {
		return cellname;
	}
	public void setCellname(String cellname) {
		this.cellname = cellname;
	}
	public String getBsc_name() {
		return bsc_name;
	}
	public void setBsc_name(String bsc_name) {
		this.bsc_name = bsc_name;
	}
	public String getMbts_name() {
		return mbts_name;
	}
	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
	public String getBtsname() {
		return btsname;
	}
	public void setBtsname(String btsname) {
		this.btsname = btsname;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}
	public String getSite_address() {
		return site_address;
	}
	public void setSite_address(String site_address) {
		this.site_address = site_address;
	}
	public String getSegment_name() {
		return segment_name;
	}
	public void setSegment_name(String segment_name) {
		this.segment_name = segment_name;
	}
	public String getCellid() {
		return cellid;
	}
	public void setCellid(String cellid) {
		this.cellid = cellid;
	}
	public String getCi() {
		return ci;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public String getLac() {
		return lac;
	}
	public void setLac(String lac) {
		this.lac = lac;
	}
	public String getRac() {
		return rac;
	}
	public void setRac(String rac) {
		this.rac = rac;
	}
	public String getTrxid() {
		return trxid;
	}
	public void setTrxid(String trxid) {
		this.trxid = trxid;
	}
	public String getTrxname() {
		return trxname;
	}
	public void setTrxname(String trxname) {
		this.trxname = trxname;
	}
	public String getFreq() {
		return freq;
	}
	public void setFreq(String freq) {
		this.freq = freq;
	}
	public String getTrxno() {
		return trxno;
	}
	public void setTrxno(String trxno) {
		this.trxno = trxno;
	}
	public String getIdtype() {
		return idtype;
	}
	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}
	public String getIsmainbcch() {
		return ismainbcch;
	}
	public void setIsmainbcch(String ismainbcch) {
		this.ismainbcch = ismainbcch;
	}
	public String getIstmptrx() {
		return istmptrx;
	}
	public void setIstmptrx(String istmptrx) {
		this.istmptrx = istmptrx;
	}
	public String getGtrxgroupid() {
		return gtrxgroupid;
	}
	public void setGtrxgroupid(String gtrxgroupid) {
		this.gtrxgroupid = gtrxgroupid;
	}
	public String getTch_total() {
		return tch_total;
	}
	public void setTch_total(String tch_total) {
		this.tch_total = tch_total;
	}
	public String getPdtch_total() {
		return pdtch_total;
	}
	public void setPdtch_total(String pdtch_total) {
		this.pdtch_total = pdtch_total;
	}
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	public String getMnc() {
		return mnc;
	}
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}
	public String getNcc() {
		return ncc;
	}
	public void setNcc(String ncc) {
		this.ncc = ncc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getExttp() {
		return exttp;
	}
	public void setExttp(String exttp) {
		this.exttp = exttp;
	}
	public String getIuotp() {
		return iuotp;
	}
	public void setIuotp(String iuotp) {
		this.iuotp = iuotp;
	}
	public String getFlexmaio() {
		return flexmaio;
	}
	public void setFlexmaio(String flexmaio) {
		this.flexmaio = flexmaio;
	}
	public String getCsvsp() {
		return csvsp;
	}
	public void setCsvsp(String csvsp) {
		this.csvsp = csvsp;
	}
	public String getCsdsp() {
		return csdsp;
	}
	public void setCsdsp(String csdsp) {
		this.csdsp = csdsp;
	}
	public String getPshpsp() {
		return pshpsp;
	}
	public void setPshpsp(String pshpsp) {
		this.pshpsp = pshpsp;
	}
	public String getPslpsvp() {
		return pslpsvp;
	}
	public void setPslpsvp(String pslpsvp) {
		this.pslpsvp = pslpsvp;
	}
	public String getBspbcchblks() {
		return bspbcchblks;
	}
	public void setBspbcchblks(String bspbcchblks) {
		this.bspbcchblks = bspbcchblks;
	}
	public String getBspagblksres() {
		return bspagblksres;
	}
	public void setBspagblksres(String bspagblksres) {
		this.bspagblksres = bspagblksres;
	}
	public String getBsprachblks() {
		return bsprachblks;
	}
	public void setBsprachblks(String bsprachblks) {
		this.bsprachblks = bsprachblks;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOpname() {
		return opname;
	}
	public void setOpname(String opname) {
		this.opname = opname;
	}
	public String getVipcell() {
		return vipcell;
	}
	public void setVipcell(String vipcell) {
		this.vipcell = vipcell;
	}
	public String getMocncmcell() {
		return mocncmcell;
	}
	public void setMocncmcell(String mocncmcell) {
		this.mocncmcell = mocncmcell;
	}
	public String getHybhifreqbandsupport() {
		return hybhifreqbandsupport;
	}
	public void setHybhifreqbandsupport(String hybhifreqbandsupport) {
		this.hybhifreqbandsupport = hybhifreqbandsupport;
	}
	public String getGlocellid() {
		return glocellid;
	}
	public void setGlocellid(String glocellid) {
		this.glocellid = glocellid;
	}
	public String getNsei() {
		return nsei;
	}
	public void setNsei(String nsei) {
		this.nsei = nsei;
	}
	public String getBvci() {
		return bvci;
	}
	public void setBvci(String bvci) {
		this.bvci = bvci;
	}
	public String getSysdesc() {
		return sysdesc;
	}
	public void setSysdesc(String sysdesc) {
		this.sysdesc = sysdesc;
	}
	public String getSyscontact() {
		return syscontact;
	}
	public void setSyscontact(String syscontact) {
		this.syscontact = syscontact;
	}
	public String getSyslocation() {
		return syslocation;
	}
	public void setSyslocation(String syslocation) {
		this.syslocation = syslocation;
	}
	public String getSysservices() {
		return sysservices;
	}
	public void setSysservices(String sysservices) {
		this.sysservices = sysservices;
	}
	public String getAdmstat() {
		return admstat;
	}
	public void setAdmstat(String admstat) {
		this.admstat = admstat;
	}
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getSub_region() {
		return sub_region;
	}
	public void setSub_region(String sub_region) {
		this.sub_region = sub_region;
	}
	public String getCluster_name() {
		return cluster_name;
	}
	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}
	public String getActstatus() {
		return actstatus;
	}
	public void setActstatus(String actstatus) {
		this.actstatus = actstatus;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getModelo_rru() {
		return modelo_rru;
	}
	public void setModelo_rru(String modelo_rru) {
		this.modelo_rru = modelo_rru;
	}
	public String getManufacturerdata() {
		return manufacturerdata;
	}
	public void setManufacturerdata(String manufacturerdata) {
		this.manufacturerdata = manufacturerdata;
	}
	public String getUbigeo_inei() {
		return ubigeo_inei;
	}
	public void setUbigeo_inei(String ubigeo_inei) {
		this.ubigeo_inei = ubigeo_inei;
	}
	public String getJefatura_radio() {
		return jefatura_radio;
	}
	public void setJefatura_radio(String jefatura_radio) {
		this.jefatura_radio = jefatura_radio;
	}
	public String getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(String azimuth) {
		this.azimuth = azimuth;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getLatitud() {
		return latitud;
	}
	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}
	public String getLongitud() {
		return longitud;
	}
	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}
	public String getElectrical_tilt() {
		return electrical_tilt;
	}
	public void setElectrical_tilt(String electrical_tilt) {
		this.electrical_tilt = electrical_tilt;
	}
	public String getMechanical_tilt() {
		return mechanical_tilt;
	}
	public void setMechanical_tilt(String mechanical_tilt) {
		this.mechanical_tilt = mechanical_tilt;
	}
	public String getGbts_power_type() {
		return gbts_power_type;
	}
	public void setGbts_power_type(String gbts_power_type) {
		this.gbts_power_type = gbts_power_type;
	}
	public String getEgbts_power_type() {
		return egbts_power_type;
	}
	public void setEgbts_power_type(String egbts_power_type) {
		this.egbts_power_type = egbts_power_type;
	}
	public String getModelo_antena() {
		return modelo_antena;
	}
	public void setModelo_antena(String modelo_antena) {
		this.modelo_antena = modelo_antena;
	}
	public String getModelo_rru_a_reportar() {
		return modelo_rru_a_reportar;
	}
	public void setModelo_rru_a_reportar(String modelo_rru_a_reportar) {
		this.modelo_rru_a_reportar = modelo_rru_a_reportar;
	}
	
	
}


