package com.tracker.web.reporte01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class Reporte01Controller {

	@Autowired
	private Reporte01Repository reporte01Repository;
	
	@GetMapping("/data-1")
	public String listarBaselines(Model model) {
	// Retorna la vista para la página principal del reporte
	return "reportes/reporte01";
	}
	
	@GetMapping("/seccion")
	public String listarCatReportes(Model model) {
	// Retorna la vista para la página principal del reporte
	return "reportes/reportes_cat";
	}

    
	/*
    @GetMapping("/data/reporte01")
    @ResponseBody
    public Map<String, Object> getReporte01() {
    List<Reporte01> dataList = reporte01Repository.findAll();

    Map<String, Object> response = new HashMap<>();
    response.put("data", dataList); // Solo 'data' para la lista
    return response;
    }
    */
    
	/*
	@GetMapping("/data/reporte01")
	@ResponseBody
	public List<Reporte01> getReporte01(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) {
	// Crear un Pageable con tamaño de página 100
	Pageable pageable = PageRequest.of(offset / 10000, 10000);
	Page<Reporte01> page = reporte01Repository.findAll(pageable);
	return page.getContent();
	}
	*/
	@GetMapping("/data/reporte01")
	@ResponseBody
	public Map<String, Object> getReporte01(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
	@RequestParam(value = "search", required = false) String searchTerm,  // Cambiado aquí
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw) {

	System.out.println("Término de búsqueda: " + searchTerm);

	Pageable pageable = PageRequest.of(page, size);
	Page<Reporte01> reportPage;

	if (searchTerm == null || searchTerm.isEmpty()) {
	reportPage = reporte01Repository.findAll(pageable);
	} else {
	reportPage = reporte01Repository.search(searchTerm, pageable);
	}

	System.out.println("Resultados encontrados: " + reportPage.getTotalElements());

	Map<String, Object> response = new HashMap<>();
	response.put("draw", draw);
	response.put("recordsTotal", reportPage.getTotalElements());
	response.put("recordsFiltered", reportPage.getTotalElements());
	response.put("data", reportPage.getContent());

	return response;
	}
    

	@GetMapping("/export01")
	public void exportData(HttpServletResponse response) throws IOException {
	response.setContentType("text/csv");
	//response.setHeader("Content-Disposition", "attachment; filename=Reporte_red_4G.csv");
	//PrintWriter writer = response.getWriter();
	
	// Obtener la fecha y hora actual
    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
    Date now = new Date();
    String formattedDate = formatter.format(now);
    // Crear el nombre del archivo con fecha y hora
    String filename = "Reporte_red_4G_" + formattedDate + ".csv";

    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    PrintWriter writer = response.getWriter();
	
	// Encabezados del CSV
	writer.println("Cellname,Mbts_name,Enodebid,Enodob,Enodob_name,Enodob_address,Site_name,Site_address,Tipo_sitio,Sector_name,Carrier,Distintivo,Cellid,Celda_id,Localcellid,Phycellid,Fddtddind,Name,Freqband,Cellactivestate,Userlabel,Txrxmode,Dlbandwidth,Ulbandwidth,Dlearfcn,Ulearfcn,Bw,Freq_band,Celladminstate,Tac,Prb,Nbcellflag,Objid,Additionalspectrumemission,Aircellflag,Cellradius,Cellspecificoffset,Cpricompression,Crsportnum,Csgind,Customizedbandwidthcfgind,Dlcyclicprefix,Emergencyareaidcfgind,Highspeedflag,Multirrucellflag,Preamblefmt,Qoffsetfreq,Rootsequenceidx,Uepowermaxcfgind,Ulcyclicprefix,Ulearfcncfgind,Workmode,Eucellstandbymode,Specialsubframepatterns,Subframeassignment,Cnopsharinggroupid,Csirsperiod,Freqpriorityforanr,Intrafreqanrind,Crsportmap,Boardtype,Manufacturerdata,Moduletype,Gummei_primario,Mme_usn_primario,Gummei_secundario,Mme_usn_secundario,Region,Sub_region,Departamento,Provincia,Distrito,Latitud,Longitud,Cluster_name,Direccion,Codigo_sitio,Nombre_sitio,Sector,Referencesignalpwr,Ltecellindex,Num_sector,Rru,Manufacturer_rru,Moduletype_rru,Ubbp,Manufacturer_ubbp,Moduletype_ubbp,Serialnumber_rru,Codigo_nombre,Serialnumber_ubbp,Tipo_cobertura,Azimuth,Height,Electrical_tilt,Mechanical_tilt,Ulbandwidth_desc,Dlbandwidth_desc,Ubigeo_inei,Tipo_sitio_lte,Jefatura_radio,Flag_transmision_tercero,Freq_band_num,Modelo_antena,Cabinetno_rru,Subrackno_rru,Slotno_rru,Num_celda_mimo,Id_tipo_cobertura,User_plane_ip,Control_plane_ip,Fecha_actualizacion,Cant_rrus,Serialnumber_antena,Modelo_bbu,Modelo_rru_a_reportar,Ip_primario,Ip_secundario");
	
	// Obtener todos los datos para la exportación
	List<Reporte01> dataList = reporte01Repository.findAll();
	
	// Escribir datos en el CSV
	/*for (Reporte01 data : dataList) {
	writer.println(
			data.getCellname() + "," + data.getMbts_name() + "," + data.getEnodebid() + "," +
			data.getEnodob() + "," + data.getEnodob_name() + "," + data.getEnodob_address() + "," +
			data.getSite_name() + "," + data.getSite_address() + "," + data.getSector_name() + "," +
			data.getCarrier() + "," + data.getDistintivo() + "," + data.getCellid() + "," +
			data.getCelda_id() + "," + data.getLocalcellid() + "," + data.getPhycellid() + "," +
			data.getFddtddind() + "," + data.getName() + "," + data.getFreqband() + "," +
			data.getCellactivestate() + "," + data.getUserlabel() + "," + data.getTxrxmode() + "," +
			data.getDlbandwidth() + "," + data.getUlbandwidth() + "," + data.getDlearfcn() + "," +
			data.getUlearfcn() + "," + data.getBw() + "," + data.getFreq_band() + "," +
			data.getCelladminstate() + "," + data.getTac() + "," + data.getPrb() + "," +
			data.getNbcellflag() + "," + data.getObjid() + "," + data.getAdditionalspectrumemission() + "," +
			data.getAircellflag() + "," + data.getCellradius() + "," + data.getCellspecificoffset() + "," +
			data.getCpricompression() + "," + data.getCrsportnum() + "," + data.getCsgind() + "," +
			data.getCustomizedbandwidthcfgind() + "," + data.getDlcyclicprefix() + "," + data.getEmergencyareaidcfgind() + "," +
			data.getHighspeedflag() + "," + data.getMultirrucellflag() + "," + data.getPreamblefmt() + "," +
			data.getQoffsetfreq() + "," + data.getRootsequenceidx() + "," + data.getUepowermaxcfgind() + "," +
			data.getUlcyclicprefix() + "," + data.getUlearfcncfgind() + "," + data.getWorkmode() + "," +
			data.getEucellstandbymode() + "," + data.getSpecialsubframepatterns() + "," + data.getSubframeassignment() + "," +
			data.getCnopsharinggroupid() + "," + data.getCsirsperiod() + "," + data.getFreqpriorityforanr() + "," +
			data.getIntrafreqanrind() + "," + data.getCrsportmap() + "," + data.getBoardtype() + "," +
			data.getManufacturerdata() + "," + data.getModuletype() + "," + data.getGummei_primario() + "," +
			data.getMme_usn_primario() + "," + data.getGummei_secundario() + "," + data.getMme_usn_secundario() + "," +
			data.getRegion() + "," + data.getSub_region() + "," + data.getDepartamento() + "," +
			data.getProvincia() + "," + data.getDistrito() + "," + data.getLatitud() + "," +
			data.getLongitud() + "," + data.getCluster_name() + "," + data.getDireccion() + "," +
			data.getCodigo_sitio() + "," + data.getNombre_sitio() + "," + data.getSector() + "," +
			data.getReferencesignalpwr() + "," + data.getLtecellindex() + "," + data.getNum_sector() + "," +
			data.getRru() + "," + data.getManufacturer_rru() + "," + data.getModuletype_rru() + "," +
			data.getUbbp() + "," + data.getManufacturer_ubbp() + "," + data.getModuletype_ubbp() + "," +
			data.getSerialnumber_rru() + "," + data.getCodigo_nombre() + "," + data.getSerialnumber_ubbp() + "," +
			data.getTipo_cobertura() + "," + data.getAzimuth() + "," + data.getHeight() + "," +
			data.getElectrical_tilt() + "," + data.getMechanical_tilt() + "," + data.getUlbandwidth_desc() + "," +
			data.getDlbandwidth_desc() + "," + data.getUbigeo_inei() + "," + data.getTipo_sitio_lte() + "," +
			data.getJefatura_radio() + "," + data.getFlag_transmision_tercero() + "," + data.getFreq_band_num() + "," +
			data.getModelo_antena() + "," + data.getCabinetno_rru() + "," + data.getSubrackno_rru() + "," +
			data.getSlotno_rru() + "," + data.getNum_celda_mimo() + "," + data.getId_tipo_cobertura() + "," +
			data.getUser_plane_ip() + "," + data.getControl_plane_ip() + "," + data.getFecha_actualizacion() + "," +
			data.getCant_rrus() + "," + data.getSerialnumber_antena() + "," + data.getModelo_bbu() + "," +
			data.getModelo_rru_a_reportar() + "," + data.getIp_primario() + "," + data.getIp_secundario()
			);
	}*/
	
	
	//NUEVA COLUMNA LUEGO DE SITE_NAME
	for (Reporte01 data : dataList) {
		writer.println(
				escapeCsvField(data.getCellname()) + "," + escapeCsvField(data.getMbts_name()) + "," + escapeCsvField(data.getEnodebid()) + "," +
				escapeCsvField(data.getEnodob()) + "," + escapeCsvField(data.getEnodob_name()) + "," + escapeCsvField(data.getEnodob_address()) + "," +
				escapeCsvField(data.getSite_name()) + "," + escapeCsvField(data.getSite_address()) + "," + 
				
				escapeCsvField(data.getTipo_sitio()) + "," + 
				
				escapeCsvField(data.getSector_name()) + "," +
				escapeCsvField(data.getCarrier()) + "," + escapeCsvField(data.getDistintivo()) + "," + escapeCsvField(data.getCellid()) + "," +
				escapeCsvField(data.getCelda_id()) + "," + escapeCsvField(data.getLocalcellid()) + "," + escapeCsvField(data.getPhycellid()) + "," +
				escapeCsvField(data.getFddtddind()) + "," + escapeCsvField(data.getName()) + "," + escapeCsvField(data.getFreqband()) + "," +
				escapeCsvField(data.getCellactivestate()) + "," + escapeCsvField(data.getUserlabel()) + "," + escapeCsvField(data.getTxrxmode()) + "," +
				escapeCsvField(data.getDlbandwidth()) + "," + escapeCsvField(data.getUlbandwidth()) + "," + escapeCsvField(data.getDlearfcn()) + "," +
				escapeCsvField(data.getUlearfcn()) + "," + escapeCsvField(data.getBw()) + "," + escapeCsvField(data.getFreq_band()) + "," +
				escapeCsvField(data.getCelladminstate()) + "," + escapeCsvField(data.getTac()) + "," + escapeCsvField(data.getPrb()) + "," +
				escapeCsvField(data.getNbcellflag()) + "," + escapeCsvField(data.getObjid()) + "," + escapeCsvField(data.getAdditionalspectrumemission()) + "," +
				escapeCsvField(data.getAircellflag()) + "," + escapeCsvField(data.getCellradius()) + "," + escapeCsvField(data.getCellspecificoffset()) + "," +
				escapeCsvField(data.getCpricompression()) + "," + escapeCsvField(data.getCrsportnum()) + "," + escapeCsvField(data.getCsgind()) + "," +
				escapeCsvField(data.getCustomizedbandwidthcfgind()) + "," + escapeCsvField(data.getDlcyclicprefix()) + "," + escapeCsvField(data.getEmergencyareaidcfgind()) + "," +
				escapeCsvField(data.getHighspeedflag()) + "," + escapeCsvField(data.getMultirrucellflag()) + "," + escapeCsvField(data.getPreamblefmt()) + "," +
				escapeCsvField(data.getQoffsetfreq()) + "," + escapeCsvField(data.getRootsequenceidx()) + "," + escapeCsvField(data.getUepowermaxcfgind()) + "," +
				escapeCsvField(data.getUlcyclicprefix()) + "," + escapeCsvField(data.getUlearfcncfgind()) + "," + escapeCsvField(data.getWorkmode()) + "," +
				escapeCsvField(data.getEucellstandbymode()) + "," + escapeCsvField(data.getSpecialsubframepatterns()) + "," + escapeCsvField(data.getSubframeassignment()) + "," +
				escapeCsvField(data.getCnopsharinggroupid()) + "," + escapeCsvField(data.getCsirsperiod()) + "," + escapeCsvField(data.getFreqpriorityforanr()) + "," +
				escapeCsvField(data.getIntrafreqanrind()) + "," + escapeCsvField(data.getCrsportmap()) + "," + escapeCsvField(data.getBoardtype()) + "," +
				escapeCsvField(data.getManufacturerdata()) + "," + escapeCsvField(data.getModuletype()) + "," + escapeCsvField(data.getGummei_primario()) + "," +
				escapeCsvField(data.getMme_usn_primario()) + "," + escapeCsvField(data.getGummei_secundario()) + "," + escapeCsvField(data.getMme_usn_secundario()) + "," +
				escapeCsvField(data.getRegion()) + "," + escapeCsvField(data.getSub_region()) + "," + escapeCsvField(data.getDepartamento()) + "," +
				escapeCsvField(data.getProvincia()) + "," + escapeCsvField(data.getDistrito()) + "," + escapeCsvField(data.getLatitud()) + "," +
				escapeCsvField(data.getLongitud()) + "," + escapeCsvField(data.getCluster_name()) + "," + escapeCsvField(data.getDireccion()) + "," +
				escapeCsvField(data.getCodigo_sitio()) + "," + escapeCsvField(data.getNombre_sitio()) + "," + escapeCsvField(data.getSector()) + "," +
				escapeCsvField(data.getReferencesignalpwr()) + "," + escapeCsvField(data.getLtecellindex()) + "," + escapeCsvField(data.getNum_sector()) + "," +
				escapeCsvField(data.getRru()) + "," + escapeCsvField(data.getManufacturer_rru()) + "," + escapeCsvField(data.getModuletype_rru()) + "," +
				escapeCsvField(data.getUbbp()) + "," + escapeCsvField(data.getManufacturer_ubbp()) + "," + escapeCsvField(data.getModuletype_ubbp()) + "," +
				escapeCsvField(data.getSerialnumber_rru()) + "," + escapeCsvField(data.getCodigo_nombre()) + "," + escapeCsvField(data.getSerialnumber_ubbp()) + "," +
				escapeCsvField(data.getTipo_cobertura()) + "," + escapeCsvField(data.getAzimuth()) + "," + escapeCsvField(data.getHeight()) + "," +
				escapeCsvField(data.getElectrical_tilt()) + "," + escapeCsvField(data.getMechanical_tilt()) + "," + escapeCsvField(data.getUlbandwidth_desc()) + "," +
				escapeCsvField(data.getDlbandwidth_desc()) + "," + escapeCsvField(data.getUbigeo_inei()) + "," + escapeCsvField(data.getTipo_sitio_lte()) + "," +
				escapeCsvField(data.getJefatura_radio()) + "," + escapeCsvField(data.getFlag_transmision_tercero()) + "," + escapeCsvField(data.getFreq_band_num()) + "," +
				escapeCsvField(data.getModelo_antena()) + "," + escapeCsvField(data.getCabinetno_rru()) + "," + escapeCsvField(data.getSubrackno_rru()) + "," +
				escapeCsvField(data.getSlotno_rru()) + "," + escapeCsvField(data.getNum_celda_mimo()) + "," + escapeCsvField(data.getId_tipo_cobertura()) + "," +
				escapeCsvField(data.getUser_plane_ip()) + "," + escapeCsvField(data.getControl_plane_ip()) + "," + escapeCsvField(data.getFecha_actualizacion()) + "," +
				escapeCsvField(data.getCant_rrus()) + "," + escapeCsvField(data.getSerialnumber_antena()) + "," + escapeCsvField(data.getModelo_bbu()) + "," +
				escapeCsvField(data.getModelo_rru_a_reportar()) + "," + escapeCsvField(data.getIp_primario()) + "," + escapeCsvField(data.getIp_secundario())
				);
		}
	
	/*
	for (Reporte01 data : dataList) {
		writer.println(
		escapeCsvField(data.getCellname()) + "," +
		escapeCsvField(data.getMbts_name()) + "," +
		escapeCsvField(data.getEnodebid()) + "," +
		escapeCsvField(data.getEnodob())
		);
	}
	*/
	
	}		
	//para evitar el problema con columnas que tengan comas
	private String escapeCsvField(String field) {
		if (field == null) {
		return "";
		}
		String escapedField = field.replace("\"", "\"\"");
		return "\"" + escapedField + "\"";
		}
	
}



