package com.tracker.web.auditoriared;

public class AuditoriaOcupadaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuditoriaOcupadaException() {
        super("Ya hay una auditoria en ejecucion. Espere a que termine e intente de nuevo.");
    }
}
