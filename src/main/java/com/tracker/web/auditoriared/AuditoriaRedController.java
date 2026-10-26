package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auditoria-red")
public class AuditoriaRedController {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaRedController.class);

    private final AuditoriaRedService service;
    private final LectorSitioPlanificado lectorSitio;

    public AuditoriaRedController(AuditoriaRedService service, LectorSitioPlanificado lectorSitio) {
        this.service = service;
        this.lectorSitio = lectorSitio;
    }

    /** /sitio es la misma pagina: con ?ids=..&pci=.. (enlace de /recursos/asignar) el JS pasa a modo sitio. */
    @GetMapping({"", "/sitio"})
    public String pagina(Model model) {
        model.addAttribute("radioMaxKm", service.getRadioMaxKm());
        model.addAttribute("radioReusoKm", service.getRadioReusoKm());
        return "auditoriared/index";
    }

    @GetMapping("/api/sitios")
    @ResponseBody
    public List<SitioDTO> buscarSitios(@RequestParam("q") String texto) {
        return service.buscarSitios(texto);
    }

    @PostMapping("/api/ejecutar")
    @ResponseBody
    public ResultadoAuditoria ejecutar(@RequestBody SolicitudAuditoria solicitud) {
        return service.auditar(solicitud);
    }

    @GetMapping("/api/sitio")
    @ResponseBody
    public ResultadoAuditoria auditarSitio(@RequestParam("ids") List<Long> ids, @RequestParam("pci") List<Integer> pcis) {
        List<String> avisos = new ArrayList<>();
        return service.auditarSitio(lectorSitio.leer(ids, pcis, avisos), avisos);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> sinPermiso(AccessDeniedException e) {
        return error(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AuditoriaOcupadaException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> ocupada(AuditoriaOcupadaException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> solicitudInvalida(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> motorNoDisponible(IllegalStateException e) {
        log.error("Fallo el motor de auditoria de red", e);
        return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> baseDeDatos(DataAccessException e) {
        log.error("Fallo la lectura de la red existente para la auditoria", e);
        return error(HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo leer la red existente en la base de datos. Intente de nuevo.");
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Collections.singletonMap("error", mensaje));
    }
}
