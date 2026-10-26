package com.tracker.web.auditoriared;

import org.springframework.stereotype.Component;

@Component
public final class ChequeoConfusionMod3 extends ChequeoMotorBase {
    @Override public String tipoMotor() { return "PCI_MOD3"; }
    @Override public String etiqueta() { return "Confusion mod-3"; }
}
