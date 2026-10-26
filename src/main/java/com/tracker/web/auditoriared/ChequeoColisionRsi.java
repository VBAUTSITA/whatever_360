package com.tracker.web.auditoriared;

import org.springframework.stereotype.Component;

@Component
public final class ChequeoColisionRsi extends ChequeoMotorBase {
    @Override public String tipoMotor() { return "RSI_COLLISION"; }
    @Override public String etiqueta() { return "Colision RSI"; }
}
