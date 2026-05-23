package com.tracker.web.pap.report01;

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
@RequestMapping("/proyectos-construccion")
public class ReportePP01Controller {

	@Autowired
	private ReportePP01Repository reporte01Repository;
	
	@GetMapping("/data/dash01")
	@ResponseBody
	public List<ReportePP01> getReportePP01() {
		return reporte01Repository.findAll();
	}
	
	@GetMapping("/data-1")
	public String listarConstrucciones(Model model) {
	// Retorna la vista para la página principal del reporte
	return "PP_Reportes/reporte01";
	}
	
	@GetMapping("/seccion")
	public String listarCatReportes(Model model) {
	// Retorna la vista para la página principal del reporte
	return "reportes/reportes_cat";
	}    
	
    
	@GetMapping("/data/reporte01")
	@ResponseBody
	public Map<String, Object> getReporte01(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
	@RequestParam(value = "search", required = false) String searchTerm,  // Cambiado aquí
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw) {

	System.out.println("Término de búsqueda: " + searchTerm);

	Pageable pageable = PageRequest.of(page, size);
	Page<ReportePP01> reportPage;

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
    String filename = "Rep_Seguimiento_Constr_" + formattedDate + ".csv";

    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    PrintWriter writer = response.getWriter();	

    // Encabezados del CSV
    writer.println("tipo_seguimiento,ot,req_alta,codigosite,nombresite,estado_sitio,tipo_estacion,etiqueta,proyecto,sub_proyecto,region,subregion,contrata,status_ot,estado_total,fase_proyecto,etapa_evolucion,subproy_const,forecast_etapa,forecast_proyecto,cambios_forecast_proyecto,latitud,longitud,fecha_activacion,fecha_on_air,seguimiento_historico,departamento,provincia,distrito,fecha_carga,weekFechaActivacion");

	// Obtener todos los datos para la exportación
	List<ReportePP01> dataList = reporte01Repository.findAll();
	
	// Escribir datos en el CSV	
	for (ReportePP01 data : dataList) {
	    writer.println(	  
	    	escapeCsvField(data.getTipoSeguimiento()) + "," +
	        escapeCsvField(data.getOt()) + "," +	        
	        escapeCsvField(data.getReqAlta()) + "," +
	        escapeCsvField(data.getCodigosite()) + "," +
	        escapeCsvField(data.getNombresite()) + "," +
	        escapeCsvField(data.getEstadoSitio()) + "," +
	        escapeCsvField(data.getTipoEstacion()) + "," +
	        escapeCsvField(data.getEtiqueta()) + "," +
	        escapeCsvField(data.getProyecto()) + "," +
	        escapeCsvField(data.getSubProyecto()) + "," +
	        escapeCsvField(data.getRegion()) + "," +
	        escapeCsvField(data.getSubregion()) + "," +
	        escapeCsvField(data.getContrata()) + "," +
	        escapeCsvField(data.getStatusOt()) + "," +
	        escapeCsvField(data.getEstadoTotal()) + "," +
	        escapeCsvField(data.getFaseProyecto()) + "," +
	        escapeCsvField(data.getEtapaEvolucion()) + "," +
	        escapeCsvField(data.getSubproyConst()) + "," +
	        escapeCsvField(data.getForecastEtapa()) + "," +
	        escapeCsvField(data.getForecastProyecto()) + "," +
	        escapeCsvField(data.getCambiosForecastProyecto()) + "," +
	        escapeCsvField(data.getLatitud()) + "," +
	        escapeCsvField(data.getLongitud()) + "," +
	        escapeCsvField(data.getFechaActivacion()) + "," +
	        escapeCsvField(data.getFechaOnAir()) + "," +
	        escapeCsvField(data.getSeguimientoHistorico()) + "," +
	        escapeCsvField(data.getDepartamento()) + "," +
	        escapeCsvField(data.getProvincia()) + "," +
	        escapeCsvField(data.getDistrito()) + "," +
	        escapeCsvField(data.getFechaCarga()) + "," +
	        escapeCsvField(data.getWeekFechaActivacion())
	        
	    	);
		}	
	
	}	
	
	//para evitar el problema con columnas que tengan comas
	private String escapeCsvField(Object field) {
	    if (field == null) return "";
	    String value;
	    if (field instanceof Date) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        value = sdf.format((Date) field);
	    } else {
	        value = field.toString();
	    }
	    value = value.replace("\"", "\"\"");
	    return "\"" + value + "\"";
	}
	
	/*DASH02*/
	
	@Autowired
    private ReportePP02Repository reporte02Repository;

    @GetMapping("/data/dash02")
    @ResponseBody
    public List<ReportePP02> getAllData() {
        return reporte02Repository.findAll();
    }

    @GetMapping("/data/reporte02")
    @ResponseBody
    public Map<String, Object> getReporte02(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "draw", defaultValue = "1") Integer draw) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReportePP02> pageResult = (search == null || search.isEmpty()) ?
                reporte02Repository.findAll(pageable) :
                reporte02Repository.search(search, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", pageResult.getTotalElements());
        response.put("recordsFiltered", pageResult.getTotalElements());
        response.put("data", pageResult.getContent());

        return response;
    }

    @GetMapping("/export02")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String filename = "Rep_Seguimiento_Expansiones_" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        PrintWriter writer = response.getWriter();

        writer.println("id,codigo,nombre,responsable,proyecto,sub_proyecto,jefatura_rf,sub_region,contratista,region,sector,ot_pap,req,anio,semana,estatus_general,mes");
        for (ReportePP02 item : reporte02Repository.findAll()) {
        	writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,\"%s\"\n",
        	        item.getId(), item.getCodigo(), item.getNombre(), item.getResponsable(), item.getProyecto(), item.getSubProyecto(),
        	        item.getJefaturaRf(), item.getSubRegion(), item.getContratista(), item.getRegion(), item.getSector(),
        	        item.getOtPap(), item.getReq(), item.getAnio(), item.getSemana(), item.getEstatusGeneral(), item.getMes());
        }
    }

	
}



