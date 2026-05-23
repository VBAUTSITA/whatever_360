package com.tracker.web.libreria.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;


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
import javax.transaction.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//categoria_libreria fue cambiado a categoria_libreria
	
@Controller
@RequestMapping("/archivador")
public class LibreriaController {

	@Autowired
	private ArchivadoRepository archivadoRepository;
	
	@Autowired
	private ArchivoComentarioRepository archivoComentarioRepository;

	/*@GetMapping("/listar")
	public String listarArchivos(Model model) {
		model.addAttribute("archivos", archivoRepository.findAll());		
	    return "mapas/capas_home_user";
	
	}*/
	
	@GetMapping("/listar")
	public String listarArchivos(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/libreria_home_user";
	
	}
	
	@GetMapping("/listar-test")
	public String listarArchivosTest(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/current_libreria_home_user";
	
	}
	
	@GetMapping("/listar-admin")
	public String listarArchivosAdmin(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/libreria_home";
	
	}
	
	@GetMapping("/listar-categorias")
	public String listarArchivosHomeCat(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_home_cat";
	
	}
	
	@GetMapping("/listar-categorias-2")
	public String listarArchivosHomeCat2(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/current_libreria_home_cat";
	
	}
	
	@GetMapping("/listar-categoria-1")
	public String listarArchivosCat1(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_1";
	
	}
	@GetMapping("/listar-categoria-2")
	public String listarArchivosCat2(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_2";
	
	}
	@GetMapping("/listar-categoria-3")
	public String listarArchivosCat3(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_3";
	
	}
	@GetMapping("/listar-categoria-4")
	public String listarArchivosCat4(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_4";
	
	}
	@GetMapping("/listar-categoria-5")
	public String listarArchivosCat5(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_5";
	
	}
	@GetMapping("/listar-categoria-6")
	public String listarArchivosCat6(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_6";
	
	}
	@GetMapping("/listar-categoria-7")
	public String listarArchivosCat7(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_7";
	
	}
	@GetMapping("/listar-categoria-8")
	public String listarArchivosCat8(Model model) {
		model.addAttribute("archivos", archivadoRepository.findAll());		
	    return "libreria/categoria_libreria/libreria_cat_8";
	
	}

	
	

	
	
	/*
	private String obtenerRutaAbsoluta(Archivado archivo) {
		  //***se debe agregar la ruta del fichero y no la URL de la web
		  //return "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_capa();
		  //return "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp";		  
		  return "/index2/tomcat/webapps/web-tracker";
	}*/
	
	private static final String BASE_PATH = "/index2/web-tracker-data";
	
	@PostMapping("/subir")
	public String subirArchivo(
	        @RequestParam("archivo") MultipartFile archivo,
	        HttpServletRequest request,
	        @RequestParam("descripcion_archivo_libreria") String descripcion_capa,
	        @RequestParam("usuario_libreria") String usuario_archivo,
	        @RequestParam("fecha_carga") String fecha_carga,
	        @RequestParam("categoria_libreria") String categoria_libreria
	) throws IOException {
		
		

	    String nombreArchivo =
	            StringUtils.cleanPath(archivo.getOriginalFilename());

	    String uniqueFilename =
	            UUID.randomUUID().toString().substring(0, 8)
	            + "_" + nombreArchivo;

	    // 🔑 CHANGE: include category in path
	    String ruta =
	            "/uploads/archivador_cargas/"
	            + categoria_libreria
	            + "/"
	            + uniqueFilename;
	    
	    /*
	    String rutaAbsoluta =
	            request.getServletContext().getRealPath(ruta);*/
	    
	    Path rutaAbsoluta = Paths.get(BASE_PATH + ruta);
	    
	    Files.createDirectories(rutaAbsoluta.getParent());
	    
	    Files.copy(
	    	    archivo.getInputStream(),
	    	    rutaAbsoluta,
	    	    StandardCopyOption.REPLACE_EXISTING
	    	);


	    Archivado nuevoArchivo = new Archivado();
	    nuevoArchivo.setNombre_archivo_libreria(nombreArchivo);
	    nuevoArchivo.setArchivo_libreria(ruta);
	    nuevoArchivo.setDescripcion_archivo_libreria(descripcion_capa);
	    nuevoArchivo.setUsuario_libreria(usuario_archivo);
	    nuevoArchivo.setCategoria_libreria(categoria_libreria);
	    nuevoArchivo.setFecha_carga(fecha_carga);

	    archivadoRepository.save(nuevoArchivo);

	    return "redirect:/archivador/listar";
	}
	
	
	@GetMapping("/descargar/{categoria}/{id}")
	public void descargarArchivo(
	        @PathVariable("categoria") String categoria,
	        @PathVariable("id") Long id,
	        HttpServletResponse response
	) throws IOException, NotFoundException {

	    Archivado archivo = archivadoRepository.findById(id)
	            .orElseThrow(NotFoundException::new);

	    // category validation (leave as-is for now)
	    if (!archivo.getCategoria_libreria().equalsIgnoreCase(categoria)) {
	        response.sendError(HttpServletResponse.SC_FORBIDDEN);
	        return;
	    }

	    Path rutaAbsoluta =
	            Paths.get("/index2/web-tracker-data" + archivo.getArchivo_libreria());

	    if (!Files.exists(rutaAbsoluta)) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND);
	        return;
	    }

	    response.setContentType("application/octet-stream");
	    response.setHeader(
	            "Content-Disposition",
	            "attachment; filename=" + archivo.getNombre_archivo_libreria()
	    );

	    try (InputStream in = Files.newInputStream(rutaAbsoluta);
	         OutputStream out = response.getOutputStream()) {

	        IOUtils.copy(in, out);
	        out.flush();
	    }
	}
	
	/*
	@GetMapping("/descargar/{categoria}/{id}")
	public void descargarArchivo(
	        @PathVariable("categoria") String categoria,
	        @PathVariable("id") Long id,
	        HttpServletResponse response
	) throws IOException, NotFoundException {

	    Archivado archivo = archivadoRepository.findById(id)
	            .orElseThrow(NotFoundException::new);

	    // Category validation
	    if (!archivo.getCategoria_libreria().equalsIgnoreCase(categoria)) {
	        response.sendError(HttpServletResponse.SC_FORBIDDEN);
	        return;
	    }

	    Path rutaAbsoluta =
	            Paths.get("/index2/web-tracker-data" + archivo.getArchivo_libreria());
	   

	    File file = new File(rutaAbsoluta);

	    if (!file.exists()) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND);
	        return;
	    }

	    response.setContentType("application/octet-stream");
	    response.setHeader(
	        "Content-Disposition",
	        "attachment; filename=" +
	        archivo.getNombre_archivo_libreria()
	    );

	    try (InputStream in = new FileInputStream(file)) {
	        IOUtils.copy(in, response.getOutputStream());
	        response.flushBuffer();
	    }
	}*/

	/*
	@GetMapping("/descargar/{id}")
	public void descargarArchivo(@PathVariable("id") Long id, HttpServletResponse response) throws IOException, NotFoundException {
	Archivado archivo = archivadoRepository.findById(id).orElseThrow(() -> new NotFoundException());
	
	//String rutaAbsoluta = archivo.getArchivo_capa();
	//se agrega la ruta porque no esta jalando automaticamente 
	//String rutaAbsoluta = "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_libreria();
	String rutaAbsoluta = obtenerRutaAbsoluta(archivo) + archivo.getArchivo_libreria();
	
	File file = new File(rutaAbsoluta);
	InputStream inputStream = new FileInputStream(file);
	response.setContentType("application/octet-stream");
	response.setHeader("Content-Disposition", "attachment; filename=" + archivo.getNombre_archivo_libreria());
	IOUtils.copy(inputStream, response.getOutputStream());
	response.flushBuffer();
	}
	*/

		
	
	@RequestMapping(value = "/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarArchivo(@PathVariable("id") Long id)
	        throws NotFoundException, IOException {

	    Archivado archivo = archivadoRepository.findById(id)
	            .orElseThrow(NotFoundException::new);

	    Path rutaAbsoluta =
	            Paths.get("/index2/web-tracker-data" + archivo.getArchivo_libreria());

	    Files.deleteIfExists(rutaAbsoluta);

	    archivadoRepository.deleteById(id);

	    return "redirect:/archivador/listar-admin";
	}
	
	/*
	@RequestMapping(value= "/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarArchivo(@PathVariable("id") Long id) throws NotFoundException {
	Archivado archivo = archivadoRepository.findById(id).orElseThrow(() -> new NotFoundException());
	
	//String rutaAbsoluta = archivo.getArchivo_capa();
	//se agrega la ruta porque no esta jalando automaticamente 
	//String rutaAbsoluta = "D:/Proyectos_Opti/REPOSITORIOS_GIT/web-tracker/src/main/webapp" + archivo.getArchivo_capa();
	String rutaAbsoluta = obtenerRutaAbsoluta(archivo) + archivo.getArchivo_libreria();
	
	File file = new File(rutaAbsoluta);
	file.delete();
	archivadoRepository.deleteById(id);
	return "redirect:/archivador/listar-admin";
	}
	
	*/
	
	
	@GetMapping("/libreria/{id}/comentarios")
	@Transactional
	public String verComentarios(@PathVariable("id") Long id, Model model) {

	    Archivado archivo = archivadoRepository.findById(id).orElse(null);
	    model.addAttribute("archivo", archivo);

	    List<ArchivoComentario> comentarios =
	    		archivoComentarioRepository
	    	    .findComentariosPrincipales(id);
	    model.addAttribute("comentarios", comentarios);

	    return "libreria/libreria_comentarios";
	}

	@PostMapping("/libreria/{id}/comentario")
	public String guardarComentario(@PathVariable("id") Long id,
	                                @RequestParam("texto") String texto,
	                                @RequestParam(value = "parentId", required = false) Long parentId,
	                                java.security.Principal principal) {

	    Archivado archivo = archivadoRepository.findById(id).orElse(null);

	    ArchivoComentario comentario = new ArchivoComentario();
	    comentario.setArchivo(archivo);
	    comentario.setComentarioTexto(texto);
	    comentario.setUsuario(principal.getName());
	    comentario.setNombreCompleto(principal.getName());
	    comentario.setFechaCreacion(new Date());
	    comentario.setActivo(true);

	    if (parentId != null) {
	        ArchivoComentario parent = archivoComentarioRepository.findById(parentId).orElse(null);
	        comentario.setParent(parent);
	    }

	    archivoComentarioRepository.save(comentario);

	    //return "redirect:/libreria/" + id + "/comentarios";
	    return "redirect:/archivador/libreria/" + id + "/comentarios";
	}

	
	
	
	
	
	
	
	
	
}
