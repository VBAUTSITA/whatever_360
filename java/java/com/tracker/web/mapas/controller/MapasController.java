package com.tracker.web.mapas.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;

/*import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
*/
//import com.tracker.web.mapas.model.Mapas;
//import com.tracker.web.mapas.service.MapasServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import org.apache.commons.io.IOUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/*
@Controller
@RequestMapping("/mapas")
public class MapasController {	
	
	

	@RequestMapping(value = "/capas" , method = RequestMethod.GET)
	public ModelAndView getAllMapas() {

		ModelAndView model = new ModelAndView();

		model.setViewName("mapas/capas_home");

		return model;
	}
}*/
	
@Controller
@RequestMapping("/mapas")
public class MapasController {

	@Autowired
	private ArchivoRepository archivoRepository;

	/*@GetMapping("/listar")
	public String listarArchivos(Model model) {
		model.addAttribute("archivos", archivoRepository.findAll());		
	    return "mapas/capas_home_user";
	
	}*/
	
	@GetMapping("/listar")
	public String listarArchivos(Model model) {
		model.addAttribute("archivos", archivoRepository.findAll());		
	    return "mapas/capas_home_user";
	
	}
	
	
	@GetMapping("/listar-admin")
	public String listarArchivosAdmin(Model model) {
		model.addAttribute("archivos", archivoRepository.findAll());		
	    return "mapas/capas_home";
	
	}
	
	/*
	@PostMapping("/subir")
	public String subirArchivo(@RequestParam("archivo") MultipartFile archivo, HttpServletRequest request, @RequestParam("descripcion_capa") String descripcion_capa, @RequestParam("usuario_archivo") String usuario_archivo, @RequestParam("fecha_carga") Date fecha_carga) throws IOException {
	String nombreArchivo = StringUtils.cleanPath(archivo.getOriginalFilename());	
    // Obtener la ruta desde la variable de entorno
    //String rutaBase = System.getenv("SPRING_UPLOADS_LOCATION") + "uploads";
	//String rutaBase = "/uploads";
	//String ruta = rutaBase + "/uploads/mapas_cargas/" + nombreArchivo;
	String ruta = "/uploads/mapas_cargas/" + nombreArchivo;
	String rutaAbsoluta = request.getServletContext().getRealPath(ruta);
	Files.copy(archivo.getInputStream(), Paths.get(rutaAbsoluta), StandardCopyOption.REPLACE_EXISTING);
	Archivo nuevoArchivo = new Archivo();
	nuevoArchivo.setNombre_archivo_capa(nombreArchivo);
	nuevoArchivo.setArchivo_capa(ruta);
	nuevoArchivo.setDescripcion_capa(descripcion_capa);
	nuevoArchivo.setUsuario_archivo(usuario_archivo);
	nuevoArchivo.setFecha_carga(fecha_carga);
	archivoRepository.save(nuevoArchivo);
	return "redirect:/mapas/listar";
	}	
	*/
	
	@PostMapping("/subir")
	public String subirArchivo(@RequestParam("archivo") MultipartFile archivo, HttpServletRequest request, @RequestParam("descripcion_capa") String descripcion_capa, @RequestParam("usuario_archivo") String usuario_archivo, @RequestParam("fecha_carga") String /*Date*/ fecha_carga) throws IOException {
	String nombreArchivo = StringUtils.cleanPath(archivo.getOriginalFilename());	
    
	//generamos un nombre unico para el archivo y evitamos su reemplazo con el replace existing
	//genera un nombre aleatorio unico global -->  UUID.randomUUID().toString().substring(0, 8) 
	//genera la hora actual -->  System.currentTimeMillis()
	//String uniqueFilename =  UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + "_" + nombreArchivo;
	String uniqueFilename =  UUID.randomUUID().toString().substring(0, 8) + "_" + nombreArchivo;
	String ruta = "/uploads/mapas_cargas/" + uniqueFilename;
	String rutaAbsoluta = request.getServletContext().getRealPath(ruta);
	Files.copy(archivo.getInputStream(), Paths.get(rutaAbsoluta), StandardCopyOption.REPLACE_EXISTING);
	Archivo nuevoArchivo = new Archivo();
	nuevoArchivo.setNombre_archivo_capa(nombreArchivo);
	nuevoArchivo.setArchivo_capa(ruta);
	nuevoArchivo.setDescripcion_capa(descripcion_capa);
	nuevoArchivo.setUsuario_archivo(usuario_archivo);
	nuevoArchivo.setFecha_carga(fecha_carga);
	archivoRepository.save(nuevoArchivo);
	return "redirect:/mapas/listar-admin";
	}	
	
	
	private String obtenerRutaAbsoluta(Archivo archivo) {
		//***se debe agregar la ruta del fichero y no la URL de la web
		  //return "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_capa();
		  //return "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp";		  
		  return "/index1/tomcat/webapps/web-tracker";
	}
	
	
	
	@GetMapping("/descargar/{id}")
	public void descargarArchivo(@PathVariable("id") Long id, HttpServletResponse response) throws IOException, NotFoundException {
	Archivo archivo = archivoRepository.findById(id).orElseThrow(() -> new NotFoundException());
	
	//String rutaAbsoluta = archivo.getArchivo_capa();
	//se agrega la ruta porque no esta jalando automaticamente 
	//String rutaAbsoluta = "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_capa();
	String rutaAbsoluta = obtenerRutaAbsoluta(archivo) + archivo.getArchivo_capa();
	
	File file = new File(rutaAbsoluta);
	InputStream inputStream = new FileInputStream(file);
	response.setContentType("application/octet-stream");
	response.setHeader("Content-Disposition", "attachment; filename=" + archivo.getNombre_archivo_capa());
	IOUtils.copy(inputStream, response.getOutputStream());
	response.flushBuffer();
	}
	

	/*
	//formato fallido 
	@DeleteMapping("/eliminar/{id}")
	public String eliminarArchivo(@PathVariable("id") Long id) throws NotFoundException {
	Archivo archivo = archivoRepository.findById(id).orElseThrow(() -> new NotFoundException());
	
	//String rutaAbsoluta = archivo.getArchivo_capa();
	//se agrega la ruta porque no esta jalando automaticamente 
	String rutaAbsoluta = "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_capa();
	
	File file = new File(rutaAbsoluta);
	file.delete();
	archivoRepository.deleteById(id);
	return "redirect:/mapas/listar";
	}
	*/
	
	
	/*
	//solucion pero falta eliminar el archivo 
	@RequestMapping(value= "/eliminar/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable("id") Long id ) {
		archivoRepository.deleteById(id);
		return "redirect:/mapas/listar";
	}*/
	
	
	
	@RequestMapping(value= "/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarArchivo(@PathVariable("id") Long id) throws NotFoundException {
	Archivo archivo = archivoRepository.findById(id).orElseThrow(() -> new NotFoundException());
	
	//String rutaAbsoluta = archivo.getArchivo_capa();
	//se agrega la ruta porque no esta jalando automaticamente 
	//String rutaAbsoluta = "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_capa();
	String rutaAbsoluta = obtenerRutaAbsoluta(archivo) + archivo.getArchivo_capa();
	
	File file = new File(rutaAbsoluta);
	file.delete();
	archivoRepository.deleteById(id);
	return "redirect:/mapas/listar-admin";
	}
	
}


