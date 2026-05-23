package com.tracker.web.libreria.controller;

import com.tracker.web.libreria.controller.Archivado;
import com.tracker.web.libreria.controller.ArchivadoRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/archivador-massive")
public class LibreriaMassiveController {

    private static final String BASE_PATH =
            "/index2/web-tracker-data";

    @Autowired
    private ArchivadoRepository archivadoRepository;

    /* =========================================================
       UPLOAD OPTION A – standard multi-file upload
       ========================================================= */
    @PostMapping("/upload")
    @Transactional
    public String uploadMultiple(
            @RequestParam("archivos") MultipartFile[] archivos,
            @RequestParam("categoria") String categoria,
            @RequestParam("usuario") String usuario,
            @RequestParam("fecha") String fecha
    ) throws IOException {

        for (MultipartFile archivo : archivos) {

            if (archivo.isEmpty()) {
                continue;
            }

            String originalName = archivo.getOriginalFilename();
            String safeName = UUID.randomUUID().toString().substring(0, 8)
                    + "_" + originalName;

            String rutaRelativa =
                    "/uploads/archivador_cargas/"
                    + categoria
                    + "/"
                    + safeName;

            Path rutaAbsoluta = Paths.get(BASE_PATH + rutaRelativa);

            Files.createDirectories(rutaAbsoluta.getParent());

            Files.copy(
                    archivo.getInputStream(),
                    rutaAbsoluta,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Archivado a = new Archivado();
            a.setNombre_archivo_libreria(originalName);
            a.setArchivo_libreria(rutaRelativa);
            a.setDescripcion_archivo_libreria("CARGA MASIVA");
            a.setUsuario_libreria(usuario);
            a.setCategoria_libreria(categoria);
            a.setFecha_carga(fecha);

            archivadoRepository.save(a);
        }

        return "redirect:/archivador/listar";
    }

    /* =========================================================
       UPLOAD OPTION B – alternative (single endpoint per file)
       ========================================================= */
    @PostMapping("/upload-alt")
    @Transactional
    public void uploadSingleAlt(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("categoria") String categoria,
            @RequestParam("usuario") String usuario,
            @RequestParam("fecha") String fecha
    ) throws IOException {

        if (archivo.isEmpty()) {
            return;
        }

        String originalName = archivo.getOriginalFilename();
        String safeName = UUID.randomUUID().toString().substring(0, 8)
                + "_" + originalName;

        String rutaRelativa =
                "/uploads/archivador_cargas/"
                + categoria
                + "/"
                + safeName;

        Path rutaAbsoluta = Paths.get(BASE_PATH + rutaRelativa);

        Files.createDirectories(rutaAbsoluta.getParent());

        Files.copy(
                archivo.getInputStream(),
                rutaAbsoluta,
                StandardCopyOption.REPLACE_EXISTING
        );

        Archivado a = new Archivado();
        a.setNombre_archivo_libreria(originalName);
        a.setArchivo_libreria(rutaRelativa);
        a.setDescripcion_archivo_libreria("CARGA MASIVA ALT");
        a.setUsuario_libreria(usuario);
        a.setCategoria_libreria(categoria);
        a.setFecha_carga(fecha);

        archivadoRepository.save(a);
    }

    /* =========================================================
       DOWNLOAD OPTION A – by ID (safe)
       ========================================================= */
    @GetMapping("/download/{id}")
    public void downloadById(
            @PathVariable("id") Long id,
            HttpServletResponse response
    ) throws Exception {

        Archivado archivo = archivadoRepository.findById(id)
                .orElseThrow(Exception::new);

        Path rutaAbsoluta =
                Paths.get(BASE_PATH + archivo.getArchivo_libreria());

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

    /* =========================================================
       DOWNLOAD OPTION B – by relative path (fallback)
       ========================================================= */
    @GetMapping("/download-alt")
    public void downloadByPath(
            @RequestParam("path") String relativePath,
            @RequestParam("name") String filename,
            HttpServletResponse response
    ) throws IOException {

        Path rutaAbsoluta = Paths.get(BASE_PATH + relativePath);

        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + filename
        );

        try (InputStream in = Files.newInputStream(rutaAbsoluta);
             OutputStream out = response.getOutputStream()) {

            IOUtils.copy(in, out);
            out.flush();
        }
    }
}