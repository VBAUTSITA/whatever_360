package com.tracker.web.reporte02;

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
public class Reporte02Controller {

	@Autowired
	private Reporte02Repository reporte02Repository;
	
	@GetMapping("/data-2")
	public String listarBaselines(Model model) {
	// Retorna la vista para la página principal del reporte
	return "reportes/reporte02";
	}

    
	/*
    @GetMapping("/data/reporte01")
    @ResponseBody
    public Map<String, Object> getReporte01() {
    List<Reporte01> dataList = reporte02Repository.findAll();

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
	Page<Reporte01> page = reporte02Repository.findAll(pageable);
	return page.getContent();
	}
	*/
	@GetMapping("/data/reporte02")
	@ResponseBody
	public Map<String, Object> getReporte01(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
	@RequestParam(value = "search", required = false) String searchTerm,  // Cambiado aquí
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw) {

	System.out.println("Término de búsqueda: " + searchTerm);

	Pageable pageable = PageRequest.of(page, size);
	Page<Reporte02> reportPage;

	if (searchTerm == null || searchTerm.isEmpty()) {
	reportPage = reporte02Repository.findAll(pageable);
	} else {
	reportPage = reporte02Repository.search(searchTerm, pageable);
	}

	System.out.println("Resultados encontrados: " + reportPage.getTotalElements());

	Map<String, Object> response = new HashMap<>();
	response.put("draw", draw);
	response.put("recordsTotal", reportPage.getTotalElements());
	response.put("recordsFiltered", reportPage.getTotalElements());
	response.put("data", reportPage.getContent());

	return response;
	}
    

	@GetMapping("/export02")
	public void exportData(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		//response.setHeader("Content-Disposition", "attachment; filename=Reporte_red_3G.csv");
		//PrintWriter writer = response.getWriter();
		
		// Obtener la fecha y hora actual
	    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
	    Date now = new Date();
	    String formattedDate = formatter.format(now);
	    // Crear el nombre del archivo con fecha y hora
	    String filename = "Reporte_red_3G_" + formattedDate + ".csv";

	    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	    PrintWriter writer = response.getWriter();
	
	// Encabezados del CSV
	writer.println("Mbts,Codigo_sitio,Nombre_sitio,Central,Rncid,Nodebname,Nodeb_name,Nodeb_address,Site_name,Site_address,Sector_name,Cellname,Carrier,Locell,Cellid,Maxtxpower,Pscrambcode,Lac,Rac,Tcell,Actstatus,Bandind,Blkstatus,Uarfcndownlink,Uarfcnuplink,Freqband,Hspdschcodenum,Uarfcnuplinkind,Cfgracind,Cnopgrpindex,Ninsyncind,Noutsyncind,Trlfailure,Dltpcpattern01count,Txdiversityind,Spgid,Sac,Cio,Srn,Sn,Ssn,Vplimitind,Dssflag,Dsssmallcovmaxtxpower,Cchcnopindex,Dpgid,Sysdesc,Syscontact,Syslocation,Sysservices,Supportrncinpool,Loadsharingtype,Redundancytype,Boardtype,Manufacturerdata,Moduletype,Central_master,Central_backup,Region,Sub_region,Departamento,Provincia,Distrito,Latitud,Longitud,Cluster_name,Direccion,Num_sector,Sector,Boardtype_rru,Manufacturer_rru,Moduletype_rru,Serialnumber_rru,Codigo_nombre,Azimuth,Height,Electrical_tilt,Mechanical_tilt,Nodebid,Ubigeo_inei,Jefatura_radio,Hosttype,Phychid,Pcpichpower,Maxpcpichpower,Minpcpichpower,Dsssmallcovpcpichpower,Freq_band_num,Id_tipo_cobertura,Fecha_actualizacion,Subrackno_rru,Slotno_rru,Cabinetno_rru,Cant_rrus,Modelo_antena,Serialnumber_antena,Modelo_bbu,Modelo_rru_a_reportar");
	
	// Obtener todos los datos para la exportación
	List<Reporte02> dataList = reporte02Repository.findAll();
	
	// Escribir datos en el CSV
	/*for (Reporte01 data : dataList) {
	writer.println(
			data.getCellname() + "," + data.getMbts_name() + "," + data.getEnodebid() 
			);
	}*/
	
	for (Reporte02 data : dataList) {
		writer.println(
								escapeCsvField(data.getMbts()) + "," +
								escapeCsvField(data.getCodigo_sitio()) + "," +
								escapeCsvField(data.getNombre_sitio()) + "," +
								escapeCsvField(data.getCentral()) + "," +
								escapeCsvField(data.getRncid()) + "," +
								escapeCsvField(data.getNodebname()) + "," +
								escapeCsvField(data.getNodeb_name()) + "," +
								escapeCsvField(data.getNodeb_address()) + "," +
								escapeCsvField(data.getSite_name()) + "," +
								escapeCsvField(data.getSite_address()) + "," +
								escapeCsvField(data.getSector_name()) + "," +
								escapeCsvField(data.getCellname()) + "," +
								escapeCsvField(data.getCarrier()) + "," +
								escapeCsvField(data.getLocell()) + "," +
								escapeCsvField(data.getCellid()) + "," +
								escapeCsvField(data.getMaxtxpower()) + "," +
								escapeCsvField(data.getPscrambcode()) + "," +
								escapeCsvField(data.getLac()) + "," +
								escapeCsvField(data.getRac()) + "," +
								escapeCsvField(data.getTcell()) + "," +
								escapeCsvField(data.getActstatus()) + "," +
								escapeCsvField(data.getBandind()) + "," +
								escapeCsvField(data.getBlkstatus()) + "," +
								escapeCsvField(data.getUarfcndownlink()) + "," +
								escapeCsvField(data.getUarfcnuplink()) + "," +
								escapeCsvField(data.getFreqband()) + "," +
								escapeCsvField(data.getHspdschcodenum()) + "," +
								escapeCsvField(data.getUarfcnuplinkind()) + "," +
								escapeCsvField(data.getCfgracind()) + "," +
								escapeCsvField(data.getCnopgrpindex()) + "," +
								escapeCsvField(data.getNinsyncind()) + "," +
								escapeCsvField(data.getNoutsyncind()) + "," +
								escapeCsvField(data.getTrlfailure()) + "," +
								escapeCsvField(data.getDltpcpattern01count()) + "," +
								escapeCsvField(data.getTxdiversityind()) + "," +
								escapeCsvField(data.getSpgid()) + "," +
								escapeCsvField(data.getSac()) + "," +
								escapeCsvField(data.getCio()) + "," +
								escapeCsvField(data.getSrn()) + "," +
								escapeCsvField(data.getSn()) + "," +
								escapeCsvField(data.getSsn()) + "," +
								escapeCsvField(data.getVplimitind()) + "," +
								escapeCsvField(data.getDssflag()) + "," +
								escapeCsvField(data.getDsssmallcovmaxtxpower()) + "," +
								escapeCsvField(data.getCchcnopindex()) + "," +
								escapeCsvField(data.getDpgid()) + "," +
								escapeCsvField(data.getSysdesc()) + "," +
								escapeCsvField(data.getSyscontact()) + "," +
								escapeCsvField(data.getSyslocation()) + "," +
								escapeCsvField(data.getSysservices()) + "," +
								escapeCsvField(data.getSupportrncinpool()) + "," +
								escapeCsvField(data.getLoadsharingtype()) + "," +
								escapeCsvField(data.getRedundancytype()) + "," +
								escapeCsvField(data.getBoardtype()) + "," +
								escapeCsvField(data.getManufacturerdata()) + "," +
								escapeCsvField(data.getModuletype()) + "," +
								escapeCsvField(data.getCentral_master()) + "," +
								escapeCsvField(data.getCentral_backup()) + "," +
								escapeCsvField(data.getRegion()) + "," +
								escapeCsvField(data.getSub_region()) + "," +
								escapeCsvField(data.getDepartamento()) + "," +
								escapeCsvField(data.getProvincia()) + "," +
								escapeCsvField(data.getDistrito()) + "," +
								escapeCsvField(data.getLatitud()) + "," +
								escapeCsvField(data.getLongitud()) + "," +
								escapeCsvField(data.getCluster_name()) + "," +
								escapeCsvField(data.getDireccion()) + "," +
								escapeCsvField(data.getNum_sector()) + "," +
								escapeCsvField(data.getSector()) + "," +
								escapeCsvField(data.getBoardtype_rru()) + "," +
								escapeCsvField(data.getManufacturer_rru()) + "," +
								escapeCsvField(data.getModuletype_rru()) + "," +
								escapeCsvField(data.getSerialnumber_rru()) + "," +
								escapeCsvField(data.getCodigo_nombre()) + "," +
								escapeCsvField(data.getAzimuth()) + "," +
								escapeCsvField(data.getHeight()) + "," +
								escapeCsvField(data.getElectrical_tilt()) + "," +
								escapeCsvField(data.getMechanical_tilt()) + "," +
								escapeCsvField(data.getNodebid()) + "," +
								escapeCsvField(data.getUbigeo_inei()) + "," +
								escapeCsvField(data.getJefatura_radio()) + "," +
								escapeCsvField(data.getHosttype()) + "," +
								escapeCsvField(data.getPhychid()) + "," +
								escapeCsvField(data.getPcpichpower()) + "," +
								escapeCsvField(data.getMaxpcpichpower()) + "," +
								escapeCsvField(data.getMinpcpichpower()) + "," +
								escapeCsvField(data.getDsssmallcovpcpichpower()) + "," +
								escapeCsvField(data.getFreq_band_num()) + "," +
								escapeCsvField(data.getId_tipo_cobertura()) + "," +
								escapeCsvField(data.getFecha_actualizacion()) + "," +
								escapeCsvField(data.getSubrackno_rru()) + "," +
								escapeCsvField(data.getSlotno_rru()) + "," +
								escapeCsvField(data.getCabinetno_rru()) + "," +
								escapeCsvField(data.getCant_rrus()) + "," +
								escapeCsvField(data.getModelo_antena()) + "," +
								escapeCsvField(data.getSerialnumber_antena()) + "," +
								escapeCsvField(data.getModelo_bbu()) + "," +
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



