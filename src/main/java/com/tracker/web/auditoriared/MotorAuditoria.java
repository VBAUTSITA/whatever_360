package com.tracker.web.auditoriared;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Acceso al motor de auditoria. Hoy es el JS de produccion via Nashorn (AdaptadorMotorAuditoria). */
public interface MotorAuditoria {

    ResultadoMotor auditar(String tecnologia, List<CeldaAuditoriaDTO> celdas,
            int radioReusoMetros, int tope);

    /**
     * Sector de cada celda pedida, calculado con las reglas del propio motor. Recibe todas las
     * celdas porque el radio de una celda sin TA depende de sus vecinas.
     */
    Map<String, GeometriaSector> geometria(String tecnologia, List<CeldaAuditoriaDTO> celdas,
            Collection<String> nombres);

    /** Archivo y huella del motor, para dejar constancia de que version produjo el resultado. */
    String version(String tecnologia);
}
