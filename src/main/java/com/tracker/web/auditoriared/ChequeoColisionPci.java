package com.tracker.web.auditoriared;

import org.springframework.stereotype.Component;

@Component
public final class ChequeoColisionPci extends ChequeoMotorBase {
    @Override public String tipoMotor() { return "PCI_COLLISION"; }
    @Override public String etiqueta() { return "Colision PCI"; }
}
