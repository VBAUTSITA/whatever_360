package com.tracker.web.reporte02;

import javax.persistence.*;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "tm_conf_actual_celda_3g")
public class Reporte02 {
	
	@Id
	private String cellname;
	private String mbts;
	private String codigo_sitio;
	private String nombre_sitio;
	private String central;
	private String rncid;
	private String nodebname;
	private String nodeb_name;
	private String nodeb_address;
	private String site_name;
	private String site_address;
	private String sector_name;	
	private String carrier;
	private String locell;
	private String cellid;
	private String maxtxpower;
	private String pscrambcode;
	private String lac;
	private String rac;
	private String tcell;
	private String actstatus;
	private String bandind;
	private String blkstatus;
	private String uarfcndownlink;
	private String uarfcnuplink;
	private String freqband;
	private String hspdschcodenum;
	private String uarfcnuplinkind;
	private String cfgracind;
	private String cnopgrpindex;
	private String ninsyncind;
	private String noutsyncind;
	private String trlfailure;
	private String dltpcpattern01count;
	private String txdiversityind;
	private String spgid;
	private String sac;
	private String cio;
	private String srn;
	private String sn;
	private String ssn;
	private String vplimitind;
	private String dssflag;
	private String dsssmallcovmaxtxpower;
	private String cchcnopindex;
	private String dpgid;
	private String sysdesc;
	private String syscontact;
	private String syslocation;
	private String sysservices;
	private String supportrncinpool;
	private String loadsharingtype;
	private String redundancytype;
	private String boardtype;
	private String manufacturerdata;
	private String moduletype;
	private String central_master;
	private String central_backup;
	private String region;
	private String sub_region;
	private String departamento;
	private String provincia;
	private String distrito;
	private String latitud;
	private String longitud;
	private String cluster_name;
	private String direccion;
	private String num_sector;
	private String sector;
	private String boardtype_rru;
	private String manufacturer_rru;
	private String moduletype_rru;
	private String serialnumber_rru;
	private String codigo_nombre;
	private String azimuth;
	private String height;
	private String electrical_tilt;
	private String mechanical_tilt;
	private String nodebid;
	private String ubigeo_inei;
	private String jefatura_radio;
	private String hosttype;
	private String phychid;
	private String pcpichpower;
	private String maxpcpichpower;
	private String minpcpichpower;
	private String dsssmallcovpcpichpower;
	private String freq_band_num;
	private String id_tipo_cobertura;
	private String fecha_actualizacion;
	private String subrackno_rru;
	private String slotno_rru;
	private String cabinetno_rru;
	private String cant_rrus;
	private String modelo_antena;
	private String serialnumber_antena;
	private String modelo_bbu;
	private String modelo_rru_a_reportar;
	
	public String getCellname() {
		return cellname;
	}
	public void setCellname(String cellname) {
		this.cellname = cellname;
	}
	public String getMbts() {
		return mbts;
	}
	public void setMbts(String mbts) {
		this.mbts = mbts;
	}
	public String getCodigo_sitio() {
		return codigo_sitio;
	}
	public void setCodigo_sitio(String codigo_sitio) {
		this.codigo_sitio = codigo_sitio;
	}
	public String getNombre_sitio() {
		return nombre_sitio;
	}
	public void setNombre_sitio(String nombre_sitio) {
		this.nombre_sitio = nombre_sitio;
	}
	public String getCentral() {
		return central;
	}
	public void setCentral(String central) {
		this.central = central;
	}
	public String getRncid() {
		return rncid;
	}
	public void setRncid(String rncid) {
		this.rncid = rncid;
	}
	public String getNodebname() {
		return nodebname;
	}
	public void setNodebname(String nodebname) {
		this.nodebname = nodebname;
	}
	public String getNodeb_name() {
		return nodeb_name;
	}
	public void setNodeb_name(String nodeb_name) {
		this.nodeb_name = nodeb_name;
	}
	public String getNodeb_address() {
		return nodeb_address;
	}
	public void setNodeb_address(String nodeb_address) {
		this.nodeb_address = nodeb_address;
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
	public String getSector_name() {
		return sector_name;
	}
	public void setSector_name(String sector_name) {
		this.sector_name = sector_name;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getLocell() {
		return locell;
	}
	public void setLocell(String locell) {
		this.locell = locell;
	}
	public String getCellid() {
		return cellid;
	}
	public void setCellid(String cellid) {
		this.cellid = cellid;
	}
	public String getMaxtxpower() {
		return maxtxpower;
	}
	public void setMaxtxpower(String maxtxpower) {
		this.maxtxpower = maxtxpower;
	}
	public String getPscrambcode() {
		return pscrambcode;
	}
	public void setPscrambcode(String pscrambcode) {
		this.pscrambcode = pscrambcode;
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
	public String getTcell() {
		return tcell;
	}
	public void setTcell(String tcell) {
		this.tcell = tcell;
	}
	public String getActstatus() {
		return actstatus;
	}
	public void setActstatus(String actstatus) {
		this.actstatus = actstatus;
	}
	public String getBandind() {
		return bandind;
	}
	public void setBandind(String bandind) {
		this.bandind = bandind;
	}
	public String getBlkstatus() {
		return blkstatus;
	}
	public void setBlkstatus(String blkstatus) {
		this.blkstatus = blkstatus;
	}
	public String getUarfcndownlink() {
		return uarfcndownlink;
	}
	public void setUarfcndownlink(String uarfcndownlink) {
		this.uarfcndownlink = uarfcndownlink;
	}
	public String getUarfcnuplink() {
		return uarfcnuplink;
	}
	public void setUarfcnuplink(String uarfcnuplink) {
		this.uarfcnuplink = uarfcnuplink;
	}
	public String getFreqband() {
		return freqband;
	}
	public void setFreqband(String freqband) {
		this.freqband = freqband;
	}
	public String getHspdschcodenum() {
		return hspdschcodenum;
	}
	public void setHspdschcodenum(String hspdschcodenum) {
		this.hspdschcodenum = hspdschcodenum;
	}
	public String getUarfcnuplinkind() {
		return uarfcnuplinkind;
	}
	public void setUarfcnuplinkind(String uarfcnuplinkind) {
		this.uarfcnuplinkind = uarfcnuplinkind;
	}
	public String getCfgracind() {
		return cfgracind;
	}
	public void setCfgracind(String cfgracind) {
		this.cfgracind = cfgracind;
	}
	public String getCnopgrpindex() {
		return cnopgrpindex;
	}
	public void setCnopgrpindex(String cnopgrpindex) {
		this.cnopgrpindex = cnopgrpindex;
	}
	public String getNinsyncind() {
		return ninsyncind;
	}
	public void setNinsyncind(String ninsyncind) {
		this.ninsyncind = ninsyncind;
	}
	public String getNoutsyncind() {
		return noutsyncind;
	}
	public void setNoutsyncind(String noutsyncind) {
		this.noutsyncind = noutsyncind;
	}
	public String getTrlfailure() {
		return trlfailure;
	}
	public void setTrlfailure(String trlfailure) {
		this.trlfailure = trlfailure;
	}
	public String getDltpcpattern01count() {
		return dltpcpattern01count;
	}
	public void setDltpcpattern01count(String dltpcpattern01count) {
		this.dltpcpattern01count = dltpcpattern01count;
	}
	public String getTxdiversityind() {
		return txdiversityind;
	}
	public void setTxdiversityind(String txdiversityind) {
		this.txdiversityind = txdiversityind;
	}
	public String getSpgid() {
		return spgid;
	}
	public void setSpgid(String spgid) {
		this.spgid = spgid;
	}
	public String getSac() {
		return sac;
	}
	public void setSac(String sac) {
		this.sac = sac;
	}
	public String getCio() {
		return cio;
	}
	public void setCio(String cio) {
		this.cio = cio;
	}
	public String getSrn() {
		return srn;
	}
	public void setSrn(String srn) {
		this.srn = srn;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getVplimitind() {
		return vplimitind;
	}
	public void setVplimitind(String vplimitind) {
		this.vplimitind = vplimitind;
	}
	public String getDssflag() {
		return dssflag;
	}
	public void setDssflag(String dssflag) {
		this.dssflag = dssflag;
	}
	public String getDsssmallcovmaxtxpower() {
		return dsssmallcovmaxtxpower;
	}
	public void setDsssmallcovmaxtxpower(String dsssmallcovmaxtxpower) {
		this.dsssmallcovmaxtxpower = dsssmallcovmaxtxpower;
	}
	public String getCchcnopindex() {
		return cchcnopindex;
	}
	public void setCchcnopindex(String cchcnopindex) {
		this.cchcnopindex = cchcnopindex;
	}
	public String getDpgid() {
		return dpgid;
	}
	public void setDpgid(String dpgid) {
		this.dpgid = dpgid;
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
	public String getSupportrncinpool() {
		return supportrncinpool;
	}
	public void setSupportrncinpool(String supportrncinpool) {
		this.supportrncinpool = supportrncinpool;
	}
	public String getLoadsharingtype() {
		return loadsharingtype;
	}
	public void setLoadsharingtype(String loadsharingtype) {
		this.loadsharingtype = loadsharingtype;
	}
	public String getRedundancytype() {
		return redundancytype;
	}
	public void setRedundancytype(String redundancytype) {
		this.redundancytype = redundancytype;
	}
	public String getBoardtype() {
		return boardtype;
	}
	public void setBoardtype(String boardtype) {
		this.boardtype = boardtype;
	}
	public String getManufacturerdata() {
		return manufacturerdata;
	}
	public void setManufacturerdata(String manufacturerdata) {
		this.manufacturerdata = manufacturerdata;
	}
	public String getModuletype() {
		return moduletype;
	}
	public void setModuletype(String moduletype) {
		this.moduletype = moduletype;
	}
	public String getCentral_master() {
		return central_master;
	}
	public void setCentral_master(String central_master) {
		this.central_master = central_master;
	}
	public String getCentral_backup() {
		return central_backup;
	}
	public void setCentral_backup(String central_backup) {
		this.central_backup = central_backup;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getSub_region() {
		return sub_region;
	}
	public void setSub_region(String sub_region) {
		this.sub_region = sub_region;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
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
	public String getCluster_name() {
		return cluster_name;
	}
	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getNum_sector() {
		return num_sector;
	}
	public void setNum_sector(String num_sector) {
		this.num_sector = num_sector;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getBoardtype_rru() {
		return boardtype_rru;
	}
	public void setBoardtype_rru(String boardtype_rru) {
		this.boardtype_rru = boardtype_rru;
	}
	public String getManufacturer_rru() {
		return manufacturer_rru;
	}
	public void setManufacturer_rru(String manufacturer_rru) {
		this.manufacturer_rru = manufacturer_rru;
	}
	public String getModuletype_rru() {
		return moduletype_rru;
	}
	public void setModuletype_rru(String moduletype_rru) {
		this.moduletype_rru = moduletype_rru;
	}
	public String getSerialnumber_rru() {
		return serialnumber_rru;
	}
	public void setSerialnumber_rru(String serialnumber_rru) {
		this.serialnumber_rru = serialnumber_rru;
	}
	public String getCodigo_nombre() {
		return codigo_nombre;
	}
	public void setCodigo_nombre(String codigo_nombre) {
		this.codigo_nombre = codigo_nombre;
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
	public String getNodebid() {
		return nodebid;
	}
	public void setNodebid(String nodebid) {
		this.nodebid = nodebid;
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
	public String getHosttype() {
		return hosttype;
	}
	public void setHosttype(String hosttype) {
		this.hosttype = hosttype;
	}
	public String getPhychid() {
		return phychid;
	}
	public void setPhychid(String phychid) {
		this.phychid = phychid;
	}
	public String getPcpichpower() {
		return pcpichpower;
	}
	public void setPcpichpower(String pcpichpower) {
		this.pcpichpower = pcpichpower;
	}
	public String getMaxpcpichpower() {
		return maxpcpichpower;
	}
	public void setMaxpcpichpower(String maxpcpichpower) {
		this.maxpcpichpower = maxpcpichpower;
	}
	public String getMinpcpichpower() {
		return minpcpichpower;
	}
	public void setMinpcpichpower(String minpcpichpower) {
		this.minpcpichpower = minpcpichpower;
	}
	public String getDsssmallcovpcpichpower() {
		return dsssmallcovpcpichpower;
	}
	public void setDsssmallcovpcpichpower(String dsssmallcovpcpichpower) {
		this.dsssmallcovpcpichpower = dsssmallcovpcpichpower;
	}
	public String getFreq_band_num() {
		return freq_band_num;
	}
	public void setFreq_band_num(String freq_band_num) {
		this.freq_band_num = freq_band_num;
	}
	public String getId_tipo_cobertura() {
		return id_tipo_cobertura;
	}
	public void setId_tipo_cobertura(String id_tipo_cobertura) {
		this.id_tipo_cobertura = id_tipo_cobertura;
	}
	public String getFecha_actualizacion() {
		return fecha_actualizacion;
	}
	public void setFecha_actualizacion(String fecha_actualizacion) {
		this.fecha_actualizacion = fecha_actualizacion;
	}
	public String getSubrackno_rru() {
		return subrackno_rru;
	}
	public void setSubrackno_rru(String subrackno_rru) {
		this.subrackno_rru = subrackno_rru;
	}
	public String getSlotno_rru() {
		return slotno_rru;
	}
	public void setSlotno_rru(String slotno_rru) {
		this.slotno_rru = slotno_rru;
	}
	public String getCabinetno_rru() {
		return cabinetno_rru;
	}
	public void setCabinetno_rru(String cabinetno_rru) {
		this.cabinetno_rru = cabinetno_rru;
	}
	public String getCant_rrus() {
		return cant_rrus;
	}
	public void setCant_rrus(String cant_rrus) {
		this.cant_rrus = cant_rrus;
	}
	public String getModelo_antena() {
		return modelo_antena;
	}
	public void setModelo_antena(String modelo_antena) {
		this.modelo_antena = modelo_antena;
	}
	public String getSerialnumber_antena() {
		return serialnumber_antena;
	}
	public void setSerialnumber_antena(String serialnumber_antena) {
		this.serialnumber_antena = serialnumber_antena;
	}
	public String getModelo_bbu() {
		return modelo_bbu;
	}
	public void setModelo_bbu(String modelo_bbu) {
		this.modelo_bbu = modelo_bbu;
	}
	public String getModelo_rru_a_reportar() {
		return modelo_rru_a_reportar;
	}
	public void setModelo_rru_a_reportar(String modelo_rru_a_reportar) {
		this.modelo_rru_a_reportar = modelo_rru_a_reportar;
	}	
	
}


