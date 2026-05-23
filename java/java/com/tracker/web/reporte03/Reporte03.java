package com.tracker.web.reporte03;

import javax.persistence.*;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "tm_conf_actual_celda_5g")
public class Reporte03 {
	
	@Id
	private String cellname;
	
	private String rbts;
	private String rbts_name;
	private String rbts_address;
	private String gnbid;
	private String enodobid;
	private String gnodebfunctionname;
	private String gnodob_name;
	private String gnodob_address;
	private String site_name;
	private String site_address;
	private String sector_name;
	private String carrier;
	private String distintivo;
	
	private String cellactivestate;
	private String cellid;
	private String nrcellid;
	private String duplexmode;
	private String classname;
	private String subcarrierspacing;
	private String trackingareaid;
	private String ssbfreqpos;
	private String physicalcellid;
	private String ulbandwidth;
	private String dlbandwidth;
	private String ssbperiod;
	private String logicalrootsequenceindex;
	private String ssbdescmethod;
	private String ulnarfcn;
	private String dlnarfcn;
	private String frequencyband;
	private String celladminstate;
	private String tac;
	private String nrducellid;
	private String nrducelltrpid;
	private String txrxmode;
	private String cellradius;
	private String cyclicprefixlength;
	private String lampsitecellflag;
	private String nrducellactivestate;
	private String nrducellname;
	private String prachfreqstartposition;
	private String rannotificationareaid;
	private String sib1period;
	private String slotassignment;
	private String slotstructure;
	private String taoffset;
	private String region;
	private String sub_region;
	private String departamento;
	private String provincia;
	private String distrito;
	private String latitud;
	private String longitud;
	private String cluster_name;
	private String locipv4;
	private String direccion;
	private String codigo_sitio;
	private String nombre_sitio;
	private String num_sector;
	private String sector;
	private String codigo_nombre;
	private String azimuth;
	private String height;
	private String electrical_tilt;
	private String mechanical_tilt;
	private String ubigeo_inei;
	private String freq_band;
	private String jefatura_radio;
	private String bw;
	private String prb;
	private String mbts;
	private String mbts_name;
	private String mbts_address;
	private String freq_band_num;
	private String maxtransmitpower;
	private String id_tipo_cobertura;
	private String fecha_actualizacion;
	private String rru;
	private String manufacturer_rru;
	private String moduletype_rru;
	private String serialnumber_rru;
	private String ubbp;
	private String manufacturer_ubbp;
	private String moduletype_ubbp;
	private String serialnumber_ubbp;
	private String cabinetno_rru;
	private String subrackno_rru;
	private String slotno_rru;
	private String modelo_antena;
	private String serialnumber_antena;
	private String modelo_bbu;
	private String modelo_rru_a_reportar;
	private String cant_rrus;
	
	public String getCellname() {
		return cellname;
	}
	public void setCellname(String cellname) {
		this.cellname = cellname;
	}
	public String getRbts() {
		return rbts;
	}
	public void setRbts(String rbts) {
		this.rbts = rbts;
	}
	public String getRbts_name() {
		return rbts_name;
	}
	public void setRbts_name(String rbts_name) {
		this.rbts_name = rbts_name;
	}
	public String getRbts_address() {
		return rbts_address;
	}
	public void setRbts_address(String rbts_address) {
		this.rbts_address = rbts_address;
	}
	public String getGnbid() {
		return gnbid;
	}
	public void setGnbid(String gnbid) {
		this.gnbid = gnbid;
	}
	public String getEnodobid() {
		return enodobid;
	}
	public void setEnodobid(String enodobid) {
		this.enodobid = enodobid;
	}
	public String getGnodebfunctionname() {
		return gnodebfunctionname;
	}
	public void setGnodebfunctionname(String gnodebfunctionname) {
		this.gnodebfunctionname = gnodebfunctionname;
	}
	public String getGnodob_name() {
		return gnodob_name;
	}
	public void setGnodob_name(String gnodob_name) {
		this.gnodob_name = gnodob_name;
	}
	public String getGnodob_address() {
		return gnodob_address;
	}
	public void setGnodob_address(String gnodob_address) {
		this.gnodob_address = gnodob_address;
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
	public String getDistintivo() {
		return distintivo;
	}
	public void setDistintivo(String distintivo) {
		this.distintivo = distintivo;
	}
	public String getCellactivestate() {
		return cellactivestate;
	}
	public void setCellactivestate(String cellactivestate) {
		this.cellactivestate = cellactivestate;
	}
	public String getCellid() {
		return cellid;
	}
	public void setCellid(String cellid) {
		this.cellid = cellid;
	}
	public String getNrcellid() {
		return nrcellid;
	}
	public void setNrcellid(String nrcellid) {
		this.nrcellid = nrcellid;
	}
	public String getDuplexmode() {
		return duplexmode;
	}
	public void setDuplexmode(String duplexmode) {
		this.duplexmode = duplexmode;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getSubcarrierspacing() {
		return subcarrierspacing;
	}
	public void setSubcarrierspacing(String subcarrierspacing) {
		this.subcarrierspacing = subcarrierspacing;
	}
	public String getTrackingareaid() {
		return trackingareaid;
	}
	public void setTrackingareaid(String trackingareaid) {
		this.trackingareaid = trackingareaid;
	}
	public String getSsbfreqpos() {
		return ssbfreqpos;
	}
	public void setSsbfreqpos(String ssbfreqpos) {
		this.ssbfreqpos = ssbfreqpos;
	}
	public String getPhysicalcellid() {
		return physicalcellid;
	}
	public void setPhysicalcellid(String physicalcellid) {
		this.physicalcellid = physicalcellid;
	}
	public String getUlbandwidth() {
		return ulbandwidth;
	}
	public void setUlbandwidth(String ulbandwidth) {
		this.ulbandwidth = ulbandwidth;
	}
	public String getDlbandwidth() {
		return dlbandwidth;
	}
	public void setDlbandwidth(String dlbandwidth) {
		this.dlbandwidth = dlbandwidth;
	}
	public String getSsbperiod() {
		return ssbperiod;
	}
	public void setSsbperiod(String ssbperiod) {
		this.ssbperiod = ssbperiod;
	}
	public String getLogicalrootsequenceindex() {
		return logicalrootsequenceindex;
	}
	public void setLogicalrootsequenceindex(String logicalrootsequenceindex) {
		this.logicalrootsequenceindex = logicalrootsequenceindex;
	}
	public String getSsbdescmethod() {
		return ssbdescmethod;
	}
	public void setSsbdescmethod(String ssbdescmethod) {
		this.ssbdescmethod = ssbdescmethod;
	}
	public String getUlnarfcn() {
		return ulnarfcn;
	}
	public void setUlnarfcn(String ulnarfcn) {
		this.ulnarfcn = ulnarfcn;
	}
	public String getDlnarfcn() {
		return dlnarfcn;
	}
	public void setDlnarfcn(String dlnarfcn) {
		this.dlnarfcn = dlnarfcn;
	}
	public String getFrequencyband() {
		return frequencyband;
	}
	public void setFrequencyband(String frequencyband) {
		this.frequencyband = frequencyband;
	}
	public String getCelladminstate() {
		return celladminstate;
	}
	public void setCelladminstate(String celladminstate) {
		this.celladminstate = celladminstate;
	}
	public String getTac() {
		return tac;
	}
	public void setTac(String tac) {
		this.tac = tac;
	}
	public String getNrducellid() {
		return nrducellid;
	}
	public void setNrducellid(String nrducellid) {
		this.nrducellid = nrducellid;
	}
	public String getNrducelltrpid() {
		return nrducelltrpid;
	}
	public void setNrducelltrpid(String nrducelltrpid) {
		this.nrducelltrpid = nrducelltrpid;
	}
	public String getTxrxmode() {
		return txrxmode;
	}
	public void setTxrxmode(String txrxmode) {
		this.txrxmode = txrxmode;
	}
	public String getCellradius() {
		return cellradius;
	}
	public void setCellradius(String cellradius) {
		this.cellradius = cellradius;
	}
	public String getCyclicprefixlength() {
		return cyclicprefixlength;
	}
	public void setCyclicprefixlength(String cyclicprefixlength) {
		this.cyclicprefixlength = cyclicprefixlength;
	}
	public String getLampsitecellflag() {
		return lampsitecellflag;
	}
	public void setLampsitecellflag(String lampsitecellflag) {
		this.lampsitecellflag = lampsitecellflag;
	}
	public String getNrducellactivestate() {
		return nrducellactivestate;
	}
	public void setNrducellactivestate(String nrducellactivestate) {
		this.nrducellactivestate = nrducellactivestate;
	}
	public String getNrducellname() {
		return nrducellname;
	}
	public void setNrducellname(String nrducellname) {
		this.nrducellname = nrducellname;
	}
	public String getPrachfreqstartposition() {
		return prachfreqstartposition;
	}
	public void setPrachfreqstartposition(String prachfreqstartposition) {
		this.prachfreqstartposition = prachfreqstartposition;
	}
	public String getRannotificationareaid() {
		return rannotificationareaid;
	}
	public void setRannotificationareaid(String rannotificationareaid) {
		this.rannotificationareaid = rannotificationareaid;
	}
	public String getSib1period() {
		return sib1period;
	}
	public void setSib1period(String sib1period) {
		this.sib1period = sib1period;
	}
	public String getSlotassignment() {
		return slotassignment;
	}
	public void setSlotassignment(String slotassignment) {
		this.slotassignment = slotassignment;
	}
	public String getSlotstructure() {
		return slotstructure;
	}
	public void setSlotstructure(String slotstructure) {
		this.slotstructure = slotstructure;
	}
	public String getTaoffset() {
		return taoffset;
	}
	public void setTaoffset(String taoffset) {
		this.taoffset = taoffset;
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
	public String getLocipv4() {
		return locipv4;
	}
	public void setLocipv4(String locipv4) {
		this.locipv4 = locipv4;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
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
	public String getUbigeo_inei() {
		return ubigeo_inei;
	}
	public void setUbigeo_inei(String ubigeo_inei) {
		this.ubigeo_inei = ubigeo_inei;
	}
	public String getFreq_band() {
		return freq_band;
	}
	public void setFreq_band(String freq_band) {
		this.freq_band = freq_band;
	}
	public String getJefatura_radio() {
		return jefatura_radio;
	}
	public void setJefatura_radio(String jefatura_radio) {
		this.jefatura_radio = jefatura_radio;
	}
	public String getBw() {
		return bw;
	}
	public void setBw(String bw) {
		this.bw = bw;
	}
	public String getPrb() {
		return prb;
	}
	public void setPrb(String prb) {
		this.prb = prb;
	}
	public String getMbts() {
		return mbts;
	}
	public void setMbts(String mbts) {
		this.mbts = mbts;
	}
	public String getMbts_name() {
		return mbts_name;
	}
	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
	public String getMbts_address() {
		return mbts_address;
	}
	public void setMbts_address(String mbts_address) {
		this.mbts_address = mbts_address;
	}
	public String getFreq_band_num() {
		return freq_band_num;
	}
	public void setFreq_band_num(String freq_band_num) {
		this.freq_band_num = freq_band_num;
	}
	public String getMaxtransmitpower() {
		return maxtransmitpower;
	}
	public void setMaxtransmitpower(String maxtransmitpower) {
		this.maxtransmitpower = maxtransmitpower;
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
	public String getRru() {
		return rru;
	}
	public void setRru(String rru) {
		this.rru = rru;
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
	public String getUbbp() {
		return ubbp;
	}
	public void setUbbp(String ubbp) {
		this.ubbp = ubbp;
	}
	public String getManufacturer_ubbp() {
		return manufacturer_ubbp;
	}
	public void setManufacturer_ubbp(String manufacturer_ubbp) {
		this.manufacturer_ubbp = manufacturer_ubbp;
	}
	public String getModuletype_ubbp() {
		return moduletype_ubbp;
	}
	public void setModuletype_ubbp(String moduletype_ubbp) {
		this.moduletype_ubbp = moduletype_ubbp;
	}
	public String getSerialnumber_ubbp() {
		return serialnumber_ubbp;
	}
	public void setSerialnumber_ubbp(String serialnumber_ubbp) {
		this.serialnumber_ubbp = serialnumber_ubbp;
	}
	public String getCabinetno_rru() {
		return cabinetno_rru;
	}
	public void setCabinetno_rru(String cabinetno_rru) {
		this.cabinetno_rru = cabinetno_rru;
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
	public String getCant_rrus() {
		return cant_rrus;
	}
	public void setCant_rrus(String cant_rrus) {
		this.cant_rrus = cant_rrus;
	}

	
	
}


