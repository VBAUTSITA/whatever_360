package com.tracker.web.reporte03;

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
public class Reporte03Controller {

	@Autowired
	private Reporte03Repository reporte03Repository;
	
	@GetMapping("/data-3")
	public String listarBaselines(Model model) {
	// Retorna la vista para la página principal del reporte
	return "reportes/reporte03";
	}

    
	/*
    @GetMapping("/data/reporte01")
    @ResponseBody
    public Map<String, Object> getReporte01() {
    List<Reporte01> dataList = reporte03Repository.findAll();

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
	Page<Reporte01> page = reporte03Repository.findAll(pageable);
	return page.getContent();
	}
	*/
	@GetMapping("/data/reporte03")
	@ResponseBody
	public Map<String, Object> getReporte01(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
	@RequestParam(value = "search", required = false) String searchTerm,  // Cambiado aquí
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw) {

	System.out.println("Término de búsqueda: " + searchTerm);

	Pageable pageable = PageRequest.of(page, size);
	Page<Reporte03> reportPage;

	if (searchTerm == null || searchTerm.isEmpty()) {
	reportPage = reporte03Repository.findAll(pageable);
	} else {
	reportPage = reporte03Repository.search(searchTerm, pageable);
	}

	System.out.println("Resultados encontrados: " + reportPage.getTotalElements());

	Map<String, Object> response = new HashMap<>();
	response.put("draw", draw);
	response.put("recordsTotal", reportPage.getTotalElements());
	response.put("recordsFiltered", reportPage.getTotalElements());
	response.put("data", reportPage.getContent());

	return response;
	}
    

	@GetMapping("/export03")
	public void exportData(HttpServletResponse response) throws IOException {
	response.setContentType("text/csv");
	//response.setHeader("Content-Disposition", "attachment; filename=Reporte_red_5G.csv");
	//PrintWriter writer = response.getWriter();
	
	// Obtener la fecha y hora actual
    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
    Date now = new Date();
    String formattedDate = formatter.format(now);
    // Crear el nombre del archivo con fecha y hora
    String filename = "Reporte_red_5G_" + formattedDate + ".csv";

    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    PrintWriter writer = response.getWriter();
    
	// Encabezados del CSV
	writer.println("rbts,rbts_name,rbts_address,gnbid,enodobid,gnodebfunctionname,gnodob_name,gnodob_address,site_name,site_address,sector_name,carrier,distintivo,cellname,cellactivestate,cellid,nrcellid,duplexmode,classname,subcarrierspacing,trackingareaid,ssbfreqpos,physicalcellid,ulbandwidth,dlbandwidth,ssbperiod,logicalrootsequenceindex,ssbdescmethod,ulnarfcn,dlnarfcn,frequencyband,celladminstate,tac,nrducellid,nrducelltrpid,txrxmode,cellradius,cyclicprefixlength,lampsitecellflag,nrducellactivestate,nrducellname,prachfreqstartposition,rannotificationareaid,sib1period,slotassignment,slotstructure,taoffset,region,sub_region,departamento,provincia,distrito,latitud,longitud,cluster_name,locipv4,direccion,codigo_sitio,nombre_sitio,num_sector,sector,codigo_nombre,azimuth,height,electrical_tilt,mechanical_tilt,ubigeo_inei,freq_band,jefatura_radio,bw,prb,mbts,mbts_name,mbts_address,freq_band_num,maxtransmitpower,id_tipo_cobertura,fecha_actualizacion,rru,manufacturer_rru,moduletype_rru,serialnumber_rru,ubbp,manufacturer_ubbp,moduletype_ubbp,serialnumber_ubbp,cabinetno_rru,subrackno_rru,slotno_rru,modelo_antena,serialnumber_antena,modelo_bbu,modelo_rru_a_reportar,cant_rrus");
	
	// Obtener todos los datos para la exportación
	List<Reporte03> dataList = reporte03Repository.findAll();
	
	// Escribir datos en el CSV
	/*for (Reporte01 data : dataList) {
	writer.println(
			data.getCellname() + "," + data.getMbts_name() + "," + data.getEnodebid() 
			);
	}*/
	
	for (Reporte03 data : dataList) {
		writer.println(
						escapeCsvField(data.getRbts()) + "," +
						escapeCsvField(data.getRbts_name()) + "," +
						escapeCsvField(data.getRbts_address()) + "," +
						escapeCsvField(data.getGnbid()) + "," +
						escapeCsvField(data.getEnodobid()) + "," +
						escapeCsvField(data.getGnodebfunctionname()) + "," +
						escapeCsvField(data.getGnodob_name()) + "," +
						escapeCsvField(data.getGnodob_address()) + "," +
						escapeCsvField(data.getSite_name()) + "," +
						escapeCsvField(data.getSite_address()) + "," +
						escapeCsvField(data.getSector_name()) + "," +
						escapeCsvField(data.getCarrier()) + "," +
						escapeCsvField(data.getDistintivo()) + "," +
						escapeCsvField(data.getCellname()) + "," +
						escapeCsvField(data.getCellactivestate()) + "," +
						escapeCsvField(data.getCellid()) + "," +
						escapeCsvField(data.getNrcellid()) + "," +
						escapeCsvField(data.getDuplexmode()) + "," +
						escapeCsvField(data.getClassname()) + "," +
						escapeCsvField(data.getSubcarrierspacing()) + "," +
						escapeCsvField(data.getTrackingareaid()) + "," +
						escapeCsvField(data.getSsbfreqpos()) + "," +
						escapeCsvField(data.getPhysicalcellid()) + "," +
						escapeCsvField(data.getUlbandwidth()) + "," +
						escapeCsvField(data.getDlbandwidth()) + "," +
						escapeCsvField(data.getSsbperiod()) + "," +
						escapeCsvField(data.getLogicalrootsequenceindex()) + "," +
						escapeCsvField(data.getSsbdescmethod()) + "," +
						escapeCsvField(data.getUlnarfcn()) + "," +
						escapeCsvField(data.getDlnarfcn()) + "," +
						escapeCsvField(data.getFrequencyband()) + "," +
						escapeCsvField(data.getCelladminstate()) + "," +
						escapeCsvField(data.getTac()) + "," +
						escapeCsvField(data.getNrducellid()) + "," +
						escapeCsvField(data.getNrducelltrpid()) + "," +
						escapeCsvField(data.getTxrxmode()) + "," +
						escapeCsvField(data.getCellradius()) + "," +
						escapeCsvField(data.getCyclicprefixlength()) + "," +
						escapeCsvField(data.getLampsitecellflag()) + "," +
						escapeCsvField(data.getNrducellactivestate()) + "," +
						escapeCsvField(data.getNrducellname()) + "," +
						escapeCsvField(data.getPrachfreqstartposition()) + "," +
						escapeCsvField(data.getRannotificationareaid()) + "," +
						escapeCsvField(data.getSib1period()) + "," +
						escapeCsvField(data.getSlotassignment()) + "," +
						escapeCsvField(data.getSlotstructure()) + "," +
						escapeCsvField(data.getTaoffset()) + "," +
						escapeCsvField(data.getRegion()) + "," +
						escapeCsvField(data.getSub_region()) + "," +
						escapeCsvField(data.getDepartamento()) + "," +
						escapeCsvField(data.getProvincia()) + "," +
						escapeCsvField(data.getDistrito()) + "," +
						escapeCsvField(data.getLatitud()) + "," +
						escapeCsvField(data.getLongitud()) + "," +
						escapeCsvField(data.getCluster_name()) + "," +
						escapeCsvField(data.getLocipv4()) + "," +
						escapeCsvField(data.getDireccion()) + "," +
						escapeCsvField(data.getCodigo_sitio()) + "," +
						escapeCsvField(data.getNombre_sitio()) + "," +
						escapeCsvField(data.getNum_sector()) + "," +
						escapeCsvField(data.getSector()) + "," +
						escapeCsvField(data.getCodigo_nombre()) + "," +
						escapeCsvField(data.getAzimuth()) + "," +
						escapeCsvField(data.getHeight()) + "," +
						escapeCsvField(data.getElectrical_tilt()) + "," +
						escapeCsvField(data.getMechanical_tilt()) + "," +
						escapeCsvField(data.getUbigeo_inei()) + "," +
						escapeCsvField(data.getFreq_band()) + "," +
						escapeCsvField(data.getJefatura_radio()) + "," +
						escapeCsvField(data.getBw()) + "," +
						escapeCsvField(data.getPrb()) + "," +
						escapeCsvField(data.getMbts()) + "," +
						escapeCsvField(data.getMbts_name()) + "," +
						escapeCsvField(data.getMbts_address()) + "," +
						escapeCsvField(data.getFreq_band_num()) + "," +
						escapeCsvField(data.getMaxtransmitpower()) + "," +
						escapeCsvField(data.getId_tipo_cobertura()) + "," +
						escapeCsvField(data.getFecha_actualizacion()) + "," +
						escapeCsvField(data.getRru()) + "," +
						escapeCsvField(data.getManufacturer_rru()) + "," +
						escapeCsvField(data.getModuletype_rru()) + "," +
						escapeCsvField(data.getSerialnumber_rru()) + "," +
						escapeCsvField(data.getUbbp()) + "," +
						escapeCsvField(data.getManufacturer_ubbp()) + "," +
						escapeCsvField(data.getModuletype_ubbp()) + "," +
						escapeCsvField(data.getSerialnumber_ubbp()) + "," +
						escapeCsvField(data.getCabinetno_rru()) + "," +
						escapeCsvField(data.getSubrackno_rru()) + "," +
						escapeCsvField(data.getSlotno_rru()) + "," +
						escapeCsvField(data.getModelo_antena()) + "," +
						escapeCsvField(data.getSerialnumber_antena()) + "," +
						escapeCsvField(data.getModelo_bbu()) + "," +
						escapeCsvField(data.getModelo_rru_a_reportar()) + "," +
						escapeCsvField(data.getCant_rrus())
						
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



