package com.tracker.web.reporte04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
public class Reporte04Controller {

	@Autowired
	private Reporte04Repository reporte04Repository;
	
	@GetMapping("/data-4")
	public String listarBaselines(Model model) {
	// Retorna la vista para la página principal del reporte
	return "reportes/reporte04";
	}

	
	
	@GetMapping("/data/reporte04")
	@ResponseBody
	public Map<String, Object> getReporte01(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
	@RequestParam(value = "search", required = false) String searchTerm,  // Cambiado aquí
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw) {

	System.out.println("Término de búsqueda: " + searchTerm);

	Pageable pageable = PageRequest.of(page, size);
	Page<Reporte04> reportPage;

	if (searchTerm == null || searchTerm.isEmpty()) {
	reportPage = reporte04Repository.findAll(pageable);
	} else {
	reportPage = reporte04Repository.search(searchTerm, pageable);
	}

	System.out.println("Resultados encontrados: " + reportPage.getTotalElements());

	Map<String, Object> response = new HashMap<>();
	response.put("draw", draw);
	response.put("recordsTotal", reportPage.getTotalElements());
	response.put("recordsFiltered", reportPage.getTotalElements());
	response.put("data", reportPage.getContent());

	return response;
	}
	
    

	@GetMapping("/export04")
	public void exportData(HttpServletResponse response) throws IOException {
	response.setContentType("text/csv");
	//response.setHeader("Content-Disposition", "attachment; filename=Reporte_red_5G.csv");
	//PrintWriter writer = response.getWriter();
	
	// Obtener la fecha y hora actual
    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
    Date now = new Date();
    String formattedDate = formatter.format(now);
    // Crear el nombre del archivo con fecha y hora
    String filename = "Reporte_red_2G_" + formattedDate + ".csv";

    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    PrintWriter writer = response.getWriter();
    
	// Encabezados del CSV
	writer.println("bsc_name,mbts_name,btsname,site_name,site_address,segment_name,cellid,cellname,ci,lac,rac,trxid,trxname,freq,trxno,idtype,ismainbcch,istmptrx,gtrxgroupid,tch_total,pdtch_total,mcc,mnc,ncc,bcc,exttp,iuotp,flexmaio,csvsp,csdsp,pshpsp,pslpsvp,bspbcchblks,bspagblksres,bsprachblks,type,opname,vipcell,mocncmcell,hybhifreqbandsupport,glocellid,nsei,bvci,sysdesc,syscontact,syslocation,sysservices,admstat,distrito,provincia,departamento,sub_region,cluster_name,actstatus,direccion,modelo_rru,manufacturerdata,ubigeo_inei,jefatura_radio,azimuth,height,latitud,longitud,electrical_tilt,mechanical_tilt,gbts_power_type,egbts_power_type,modelo_antena,modelo_rru_a_reportar");
	
	// Obtener todos los datos para la exportación
	List<Reporte04> dataList = reporte04Repository.findAll();
	
	// Escribir datos en el CSV
	/*for (Reporte01 data : dataList) {
	writer.println(
			data.getCellname() + "," + data.getMbts_name() + "," + data.getEnodebid() 
			);
	}*/
	
	for (Reporte04 data : dataList) {
		writer.println(
						escapeCsvField(data.getBsc_name()) + "," +
						escapeCsvField(data.getMbts_name()) + "," +
						escapeCsvField(data.getBtsname()) + "," +
						escapeCsvField(data.getSite_name()) + "," +
						escapeCsvField(data.getSite_address()) + "," +
						escapeCsvField(data.getSegment_name()) + "," +
						escapeCsvField(data.getCellid()) + "," +
						escapeCsvField(data.getCellname()) + "," +
						escapeCsvField(data.getCi()) + "," +
						escapeCsvField(data.getLac()) + "," +
						escapeCsvField(data.getRac()) + "," +
						escapeCsvField(data.getTrxid()) + "," +
						escapeCsvField(data.getTrxname()) + "," +
						escapeCsvField(data.getFreq()) + "," +
						escapeCsvField(data.getTrxno()) + "," +
						escapeCsvField(data.getIdtype()) + "," +
						escapeCsvField(data.getIsmainbcch()) + "," +
						escapeCsvField(data.getIstmptrx()) + "," +
						escapeCsvField(data.getGtrxgroupid()) + "," +
						escapeCsvField(data.getTch_total()) + "," +
						escapeCsvField(data.getPdtch_total()) + "," +
						escapeCsvField(data.getMcc()) + "," +
						escapeCsvField(data.getMnc()) + "," +
						escapeCsvField(data.getNcc()) + "," +
						escapeCsvField(data.getBcc()) + "," +
						escapeCsvField(data.getExttp()) + "," +
						escapeCsvField(data.getIuotp()) + "," +
						escapeCsvField(data.getFlexmaio()) + "," +
						escapeCsvField(data.getCsvsp()) + "," +
						escapeCsvField(data.getCsdsp()) + "," +
						escapeCsvField(data.getPshpsp()) + "," +
						escapeCsvField(data.getPslpsvp()) + "," +
						escapeCsvField(data.getBspbcchblks()) + "," +
						escapeCsvField(data.getBspagblksres()) + "," +
						escapeCsvField(data.getBsprachblks()) + "," +
						escapeCsvField(data.getType()) + "," +
						escapeCsvField(data.getOpname()) + "," +
						escapeCsvField(data.getVipcell()) + "," +
						escapeCsvField(data.getMocncmcell()) + "," +
						escapeCsvField(data.getHybhifreqbandsupport()) + "," +
						escapeCsvField(data.getGlocellid()) + "," +
						escapeCsvField(data.getNsei()) + "," +
						escapeCsvField(data.getBvci()) + "," +
						escapeCsvField(data.getSysdesc()) + "," +
						escapeCsvField(data.getSyscontact()) + "," +
						escapeCsvField(data.getSyslocation()) + "," +
						escapeCsvField(data.getSysservices()) + "," +
						escapeCsvField(data.getAdmstat()) + "," +
						escapeCsvField(data.getDistrito()) + "," +
						escapeCsvField(data.getProvincia()) + "," +
						escapeCsvField(data.getDepartamento()) + "," +
						escapeCsvField(data.getSub_region()) + "," +
						escapeCsvField(data.getCluster_name()) + "," +
						escapeCsvField(data.getActstatus()) + "," +
						escapeCsvField(data.getDireccion()) + "," +
						escapeCsvField(data.getModelo_rru()) + "," +
						escapeCsvField(data.getManufacturerdata()) + "," +
						escapeCsvField(data.getUbigeo_inei()) + "," +
						escapeCsvField(data.getJefatura_radio()) + "," +
						escapeCsvField(data.getAzimuth()) + "," +
						escapeCsvField(data.getHeight()) + "," +
						escapeCsvField(data.getLatitud()) + "," +
						escapeCsvField(data.getLongitud()) + "," +
						escapeCsvField(data.getElectrical_tilt()) + "," +
						escapeCsvField(data.getMechanical_tilt()) + "," +
						escapeCsvField(data.getGbts_power_type()) + "," +
						escapeCsvField(data.getEgbts_power_type()) + "," +
						escapeCsvField(data.getModelo_antena()) + "," +
						escapeCsvField(data.getModelo_rru_a_reportar())
						
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



