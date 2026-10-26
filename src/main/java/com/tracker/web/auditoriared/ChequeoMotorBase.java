package com.tracker.web.auditoriared;

abstract class ChequeoMotorBase implements ChequeoAuditoria {
    @Override
    public HallazgoAuditoriaDTO convertir(ConflictoMotor conflicto,
            CeldaAuditoriaDTO celdaA, CeldaAuditoriaDTO celdaB) {
        return new HallazgoAuditoriaDTO(tipoMotor(), etiqueta(), conflicto.getValor(),
                conflicto.getDistanciaMetros(), conflicto.getMotivo(), celdaA, celdaB);
    }
}
