package com.tracker.web.auditoriared;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public final class CatalogoChequeos {
    private final Map<String, ChequeoAuditoria> chequeos;

    public CatalogoChequeos(List<ChequeoAuditoria> registrados) {
        Map<String, ChequeoAuditoria> mapa = new LinkedHashMap<>();
        for (ChequeoAuditoria chequeo : registrados) {
            ChequeoAuditoria previo = mapa.put(chequeo.tipoMotor(), chequeo);
            if (previo != null) {
                throw new IllegalStateException("Chequeo duplicado: " + chequeo.tipoMotor());
            }
        }
        this.chequeos = Collections.unmodifiableMap(mapa);
    }

    public ChequeoAuditoria obtener(String tipoMotor) { return chequeos.get(tipoMotor); }

    /** Un tipo nuevo del motor sin clase propia se muestra igual, con su nombre crudo: nunca se descarta. */
    public HallazgoAuditoriaDTO convertir(ConflictoMotor conflicto,
            CeldaAuditoriaDTO celdaA, CeldaAuditoriaDTO celdaB) {
        ChequeoAuditoria chequeo = chequeos.get(conflicto.getTipo());
        if (chequeo != null) return chequeo.convertir(conflicto, celdaA, celdaB);
        return new HallazgoAuditoriaDTO(conflicto.getTipo(), conflicto.getTipo(), conflicto.getValor(),
                conflicto.getDistanciaMetros(), conflicto.getMotivo(), celdaA, celdaB);
    }
}
