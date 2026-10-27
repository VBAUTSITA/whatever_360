package com.tracker.web.auditoriared.dashboard;

import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import com.tracker.web.auditoriared.AuditoriaOcupadaException;

@ControllerAdvice(assignableTypes = DashboardAuditoriaController.class)
public class ErroresDashboard {
    private static final Logger log = LoggerFactory.getLogger(ErroresDashboard.class);
    @ExceptionHandler(AccessDeniedException.class) @ResponseBody
    public ResponseEntity<Map<String, String>> acceso(AccessDeniedException e) { return error(HttpStatus.FORBIDDEN, e.getMessage()); }
    @ExceptionHandler(AuditoriaOcupadaException.class) @ResponseBody
    public ResponseEntity<Map<String, String>> ocupado(AuditoriaOcupadaException e) { return error(HttpStatus.CONFLICT, e.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class) @ResponseBody
    public ResponseEntity<Map<String, String>> invalido(IllegalArgumentException e) { return error(HttpStatus.BAD_REQUEST, e.getMessage()); }
    @ExceptionHandler({IllegalStateException.class, DataAccessException.class}) @ResponseBody
    public ResponseEntity<Map<String, String>> indisponible(RuntimeException e) {
        log.error("No se pudo consultar el panel de auditoria", e);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo completar la consulta. Revise el registro del servidor.");
    }
    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Collections.singletonMap("error", mensaje));
    }
}
