package com.tracker.web.reporte01;

import javax.persistence.*;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "tm_conf_actual_celda_4g")
public class Reporte01 {
	
	@Id
	private String cellname;	
	private String mbts_name; 
	private String enodebid; 
	private String enodob; 
	private String enodob_name; 
	private String enodob_address; 
	private String site_name; 
	private String site_address; 
	private String sector_name; 
	private String carrier; 
	private String distintivo; 
	//private String cellname; 
	private String cellid; 
	private String celda_id; 
	private String localcellid; 
	private String phycellid; 
	private String fddtddind; 
	private String name; 
	private String freqband; 
	private String cellactivestate; 
	private String userlabel; 
	private String txrxmode; 
	private String dlbandwidth; 
	private String ulbandwidth; 
	private String dlearfcn; 
	private String ulearfcn; 
	private String bw; 
	private String freq_band; 
	private String celladminstate; 
	private String tac; 
	private String prb; 
	private String nbcellflag; 
	private String objid; 
	private String additionalspectrumemission; 
	private String aircellflag; 
	private String cellradius; 
	private String cellspecificoffset; 
	private String cpricompression; 
	private String crsportnum; 
	private String csgind; 
	private String customizedbandwidthcfgind; 
	private String dlcyclicprefix; 
	private String emergencyareaidcfgind; 
	private String highspeedflag; 
	private String multirrucellflag; 
	private String preamblefmt; 
	private String qoffsetfreq; 
	private String rootsequenceidx; 
	private String uepowermaxcfgind; 
	private String ulcyclicprefix; 
	private String ulearfcncfgind; 
	private String workmode; 
	private String eucellstandbymode; 
	private String specialsubframepatterns; 
	private String subframeassignment; 
	private String cnopsharinggroupid; 
	private String csirsperiod; 
	private String freqpriorityforanr; 
	private String intrafreqanrind; 
	private String crsportmap; 
	private String boardtype; 
	private String manufacturerdata; 
	private String moduletype; 
	private String gummei_primario; 
	private String mme_usn_primario; 
	private String gummei_secundario; 
	private String mme_usn_secundario; 
	private String region; 
	private String sub_region; 
	private String departamento; 
	private String provincia; 
	private String distrito; 
	private String latitud; 
	private String longitud; 
	private String cluster_name; 
	private String direccion; 
	private String codigo_sitio; 
	private String nombre_sitio; 
	private String sector; 
	private String referencesignalpwr; 
	private String ltecellindex; 
	private String num_sector; 
	private String rru; 
	private String manufacturer_rru; 
	private String moduletype_rru; 
	private String ubbp; 
	private String manufacturer_ubbp; 
	private String moduletype_ubbp; 
	private String serialnumber_rru; 
	private String codigo_nombre; 
	private String serialnumber_ubbp; 
	private String tipo_cobertura; 
	private String azimuth; 
	private String height; 
	private String electrical_tilt; 
	private String mechanical_tilt; 
	private String ulbandwidth_desc; 
	private String dlbandwidth_desc; 
	private String ubigeo_inei; 
	private String tipo_sitio_lte; 
	private String jefatura_radio; 
	private String flag_transmision_tercero; 
	private String freq_band_num; 
	private String modelo_antena; 
	private String cabinetno_rru; 
	private String subrackno_rru; 
	private String slotno_rru; 
	private String num_celda_mimo; 
	private String id_tipo_cobertura; 
	private String user_plane_ip; 
	private String control_plane_ip; 
	private String fecha_actualizacion; 
	private String cant_rrus; 
	private String serialnumber_antena; 
	private String modelo_bbu; 
	private String modelo_rru_a_reportar; 
	private String ip_primario; 
	private String ip_secundario;
	private String tipo_sitio;
	
	public String getCellname() {
		return cellname;
	}
	public void setCellname(String cellname) {
		this.cellname = cellname;
	}
	public String getMbts_name() {
		return mbts_name;
	}
	public void setMbts_name(String mbts_name) {
		this.mbts_name = mbts_name;
	}
	public String getEnodebid() {
		return enodebid;
	}
	public void setEnodebid(String enodebid) {
		this.enodebid = enodebid;
	}
	public String getEnodob() {
		return enodob;
	}
	public void setEnodob(String enodob) {
		this.enodob = enodob;
	}
	public String getEnodob_name() {
		return enodob_name;
	}
	public void setEnodob_name(String enodob_name) {
		this.enodob_name = enodob_name;
	}
	public String getEnodob_address() {
		return enodob_address;
	}
	public void setEnodob_address(String enodob_address) {
		this.enodob_address = enodob_address;
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
	public String getCellid() {
		return cellid;
	}
	public void setCellid(String cellid) {
		this.cellid = cellid;
	}
	public String getCelda_id() {
		return celda_id;
	}
	public void setCelda_id(String celda_id) {
		this.celda_id = celda_id;
	}
	public String getLocalcellid() {
		return localcellid;
	}
	public void setLocalcellid(String localcellid) {
		this.localcellid = localcellid;
	}
	public String getPhycellid() {
		return phycellid;
	}
	public void setPhycellid(String phycellid) {
		this.phycellid = phycellid;
	}
	public String getFddtddind() {
		return fddtddind;
	}
	public void setFddtddind(String fddtddind) {
		this.fddtddind = fddtddind;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFreqband() {
		return freqband;
	}
	public void setFreqband(String freqband) {
		this.freqband = freqband;
	}
	public String getCellactivestate() {
		return cellactivestate;
	}
	public void setCellactivestate(String cellactivestate) {
		this.cellactivestate = cellactivestate;
	}
	public String getUserlabel() {
		return userlabel;
	}
	public void setUserlabel(String userlabel) {
		this.userlabel = userlabel;
	}
	public String getTxrxmode() {
		return txrxmode;
	}
	public void setTxrxmode(String txrxmode) {
		this.txrxmode = txrxmode;
	}
	public String getDlbandwidth() {
		return dlbandwidth;
	}
	public void setDlbandwidth(String dlbandwidth) {
		this.dlbandwidth = dlbandwidth;
	}
	public String getUlbandwidth() {
		return ulbandwidth;
	}
	public void setUlbandwidth(String ulbandwidth) {
		this.ulbandwidth = ulbandwidth;
	}
	public String getDlearfcn() {
		return dlearfcn;
	}
	public void setDlearfcn(String dlearfcn) {
		this.dlearfcn = dlearfcn;
	}
	public String getUlearfcn() {
		return ulearfcn;
	}
	public void setUlearfcn(String ulearfcn) {
		this.ulearfcn = ulearfcn;
	}
	public String getBw() {
		return bw;
	}
	public void setBw(String bw) {
		this.bw = bw;
	}
	public String getFreq_band() {
		return freq_band;
	}
	public void setFreq_band(String freq_band) {
		this.freq_band = freq_band;
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
	public String getPrb() {
		return prb;
	}
	public void setPrb(String prb) {
		this.prb = prb;
	}
	public String getNbcellflag() {
		return nbcellflag;
	}
	public void setNbcellflag(String nbcellflag) {
		this.nbcellflag = nbcellflag;
	}
	public String getObjid() {
		return objid;
	}
	public void setObjid(String objid) {
		this.objid = objid;
	}
	public String getAdditionalspectrumemission() {
		return additionalspectrumemission;
	}
	public void setAdditionalspectrumemission(String additionalspectrumemission) {
		this.additionalspectrumemission = additionalspectrumemission;
	}
	public String getAircellflag() {
		return aircellflag;
	}
	public void setAircellflag(String aircellflag) {
		this.aircellflag = aircellflag;
	}
	public String getCellradius() {
		return cellradius;
	}
	public void setCellradius(String cellradius) {
		this.cellradius = cellradius;
	}
	public String getCellspecificoffset() {
		return cellspecificoffset;
	}
	public void setCellspecificoffset(String cellspecificoffset) {
		this.cellspecificoffset = cellspecificoffset;
	}
	public String getCpricompression() {
		return cpricompression;
	}
	public void setCpricompression(String cpricompression) {
		this.cpricompression = cpricompression;
	}
	public String getCrsportnum() {
		return crsportnum;
	}
	public void setCrsportnum(String crsportnum) {
		this.crsportnum = crsportnum;
	}
	public String getCsgind() {
		return csgind;
	}
	public void setCsgind(String csgind) {
		this.csgind = csgind;
	}
	public String getCustomizedbandwidthcfgind() {
		return customizedbandwidthcfgind;
	}
	public void setCustomizedbandwidthcfgind(String customizedbandwidthcfgind) {
		this.customizedbandwidthcfgind = customizedbandwidthcfgind;
	}
	public String getDlcyclicprefix() {
		return dlcyclicprefix;
	}
	public void setDlcyclicprefix(String dlcyclicprefix) {
		this.dlcyclicprefix = dlcyclicprefix;
	}
	public String getEmergencyareaidcfgind() {
		return emergencyareaidcfgind;
	}
	public void setEmergencyareaidcfgind(String emergencyareaidcfgind) {
		this.emergencyareaidcfgind = emergencyareaidcfgind;
	}
	public String getHighspeedflag() {
		return highspeedflag;
	}
	public void setHighspeedflag(String highspeedflag) {
		this.highspeedflag = highspeedflag;
	}
	public String getMultirrucellflag() {
		return multirrucellflag;
	}
	public void setMultirrucellflag(String multirrucellflag) {
		this.multirrucellflag = multirrucellflag;
	}
	public String getPreamblefmt() {
		return preamblefmt;
	}
	public void setPreamblefmt(String preamblefmt) {
		this.preamblefmt = preamblefmt;
	}
	public String getQoffsetfreq() {
		return qoffsetfreq;
	}
	public void setQoffsetfreq(String qoffsetfreq) {
		this.qoffsetfreq = qoffsetfreq;
	}
	public String getRootsequenceidx() {
		return rootsequenceidx;
	}
	public void setRootsequenceidx(String rootsequenceidx) {
		this.rootsequenceidx = rootsequenceidx;
	}
	public String getUepowermaxcfgind() {
		return uepowermaxcfgind;
	}
	public void setUepowermaxcfgind(String uepowermaxcfgind) {
		this.uepowermaxcfgind = uepowermaxcfgind;
	}
	public String getUlcyclicprefix() {
		return ulcyclicprefix;
	}
	public void setUlcyclicprefix(String ulcyclicprefix) {
		this.ulcyclicprefix = ulcyclicprefix;
	}
	public String getUlearfcncfgind() {
		return ulearfcncfgind;
	}
	public void setUlearfcncfgind(String ulearfcncfgind) {
		this.ulearfcncfgind = ulearfcncfgind;
	}
	public String getWorkmode() {
		return workmode;
	}
	public void setWorkmode(String workmode) {
		this.workmode = workmode;
	}
	public String getEucellstandbymode() {
		return eucellstandbymode;
	}
	public void setEucellstandbymode(String eucellstandbymode) {
		this.eucellstandbymode = eucellstandbymode;
	}
	public String getSpecialsubframepatterns() {
		return specialsubframepatterns;
	}
	public void setSpecialsubframepatterns(String specialsubframepatterns) {
		this.specialsubframepatterns = specialsubframepatterns;
	}
	public String getSubframeassignment() {
		return subframeassignment;
	}
	public void setSubframeassignment(String subframeassignment) {
		this.subframeassignment = subframeassignment;
	}
	public String getCnopsharinggroupid() {
		return cnopsharinggroupid;
	}
	public void setCnopsharinggroupid(String cnopsharinggroupid) {
		this.cnopsharinggroupid = cnopsharinggroupid;
	}
	public String getCsirsperiod() {
		return csirsperiod;
	}
	public void setCsirsperiod(String csirsperiod) {
		this.csirsperiod = csirsperiod;
	}
	public String getFreqpriorityforanr() {
		return freqpriorityforanr;
	}
	public void setFreqpriorityforanr(String freqpriorityforanr) {
		this.freqpriorityforanr = freqpriorityforanr;
	}
	public String getIntrafreqanrind() {
		return intrafreqanrind;
	}
	public void setIntrafreqanrind(String intrafreqanrind) {
		this.intrafreqanrind = intrafreqanrind;
	}
	public String getCrsportmap() {
		return crsportmap;
	}
	public void setCrsportmap(String crsportmap) {
		this.crsportmap = crsportmap;
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
	public String getGummei_primario() {
		return gummei_primario;
	}
	public void setGummei_primario(String gummei_primario) {
		this.gummei_primario = gummei_primario;
	}
	public String getMme_usn_primario() {
		return mme_usn_primario;
	}
	public void setMme_usn_primario(String mme_usn_primario) {
		this.mme_usn_primario = mme_usn_primario;
	}
	public String getGummei_secundario() {
		return gummei_secundario;
	}
	public void setGummei_secundario(String gummei_secundario) {
		this.gummei_secundario = gummei_secundario;
	}
	public String getMme_usn_secundario() {
		return mme_usn_secundario;
	}
	public void setMme_usn_secundario(String mme_usn_secundario) {
		this.mme_usn_secundario = mme_usn_secundario;
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
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getReferencesignalpwr() {
		return referencesignalpwr;
	}
	public void setReferencesignalpwr(String referencesignalpwr) {
		this.referencesignalpwr = referencesignalpwr;
	}
	public String getLtecellindex() {
		return ltecellindex;
	}
	public void setLtecellindex(String ltecellindex) {
		this.ltecellindex = ltecellindex;
	}
	public String getNum_sector() {
		return num_sector;
	}
	public void setNum_sector(String num_sector) {
		this.num_sector = num_sector;
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
	public String getSerialnumber_ubbp() {
		return serialnumber_ubbp;
	}
	public void setSerialnumber_ubbp(String serialnumber_ubbp) {
		this.serialnumber_ubbp = serialnumber_ubbp;
	}
	public String getTipo_cobertura() {
		return tipo_cobertura;
	}
	public void setTipo_cobertura(String tipo_cobertura) {
		this.tipo_cobertura = tipo_cobertura;
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
	public String getUlbandwidth_desc() {
		return ulbandwidth_desc;
	}
	public void setUlbandwidth_desc(String ulbandwidth_desc) {
		this.ulbandwidth_desc = ulbandwidth_desc;
	}
	public String getDlbandwidth_desc() {
		return dlbandwidth_desc;
	}
	public void setDlbandwidth_desc(String dlbandwidth_desc) {
		this.dlbandwidth_desc = dlbandwidth_desc;
	}
	public String getUbigeo_inei() {
		return ubigeo_inei;
	}
	public void setUbigeo_inei(String ubigeo_inei) {
		this.ubigeo_inei = ubigeo_inei;
	}
	public String getTipo_sitio_lte() {
		return tipo_sitio_lte;
	}
	public void setTipo_sitio_lte(String tipo_sitio_lte) {
		this.tipo_sitio_lte = tipo_sitio_lte;
	}
	public String getJefatura_radio() {
		return jefatura_radio;
	}
	public void setJefatura_radio(String jefatura_radio) {
		this.jefatura_radio = jefatura_radio;
	}
	public String getFlag_transmision_tercero() {
		return flag_transmision_tercero;
	}
	public void setFlag_transmision_tercero(String flag_transmision_tercero) {
		this.flag_transmision_tercero = flag_transmision_tercero;
	}
	public String getFreq_band_num() {
		return freq_band_num;
	}
	public void setFreq_band_num(String freq_band_num) {
		this.freq_band_num = freq_band_num;
	}
	public String getModelo_antena() {
		return modelo_antena;
	}
	public void setModelo_antena(String modelo_antena) {
		this.modelo_antena = modelo_antena;
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
	public String getNum_celda_mimo() {
		return num_celda_mimo;
	}
	public void setNum_celda_mimo(String num_celda_mimo) {
		this.num_celda_mimo = num_celda_mimo;
	}
	public String getId_tipo_cobertura() {
		return id_tipo_cobertura;
	}
	public void setId_tipo_cobertura(String id_tipo_cobertura) {
		this.id_tipo_cobertura = id_tipo_cobertura;
	}
	public String getUser_plane_ip() {
		return user_plane_ip;
	}
	public void setUser_plane_ip(String user_plane_ip) {
		this.user_plane_ip = user_plane_ip;
	}
	public String getControl_plane_ip() {
		return control_plane_ip;
	}
	public void setControl_plane_ip(String control_plane_ip) {
		this.control_plane_ip = control_plane_ip;
	}
	public String getFecha_actualizacion() {
		return fecha_actualizacion;
	}
	public void setFecha_actualizacion(String fecha_actualizacion) {
		this.fecha_actualizacion = fecha_actualizacion;
	}
	public String getCant_rrus() {
		return cant_rrus;
	}
	public void setCant_rrus(String cant_rrus) {
		this.cant_rrus = cant_rrus;
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
	public String getIp_primario() {
		return ip_primario;
	}
	public void setIp_primario(String ip_primario) {
		this.ip_primario = ip_primario;
	}
	public String getIp_secundario() {
		return ip_secundario;
	}
	public void setIp_secundario(String ip_secundario) {
		this.ip_secundario = ip_secundario;
	}
	public String getTipo_sitio() {
		return tipo_sitio;
	}
	public void setTipo_sitio(String tipo_sitio) {
		this.tipo_sitio = tipo_sitio;
	}
	
	

}


