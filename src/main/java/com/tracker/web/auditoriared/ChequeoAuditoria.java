package com.tracker.web.auditoriared;

/** Un tipo de hallazgo que ya calcula el motor. Agregar un chequeo = una clase @Component nueva. */
public interface ChequeoAuditoria {

    /** Valor del campo "type" que devuelve auditarRed. */
    String tipoMotor();

    /** Texto para la tabla y los filtros. */
    String etiqueta();

    HallazgoAuditoriaDTO convertir(ConflictoMotor conflicto,
            CeldaAuditoriaDTO celdaA, CeldaAuditoriaDTO celdaB);
}
