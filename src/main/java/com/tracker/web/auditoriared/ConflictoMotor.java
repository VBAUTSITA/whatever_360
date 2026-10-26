package com.tracker.web.auditoriared;

/** Salida cruda de auditarRed; el adaptador no decide si el par entra al alcance. */
public final class ConflictoMotor {
    private final String tipo;
    private final String nombreA;
    private final String nombreB;
    private final double distanciaMetros;
    private final String valor;
    private final String motivo;

    public ConflictoMotor(String tipo, String nombreA, String nombreB,
            double distanciaMetros, String valor, String motivo) {
        this.tipo = tipo;
        this.nombreA = nombreA;
        this.nombreB = nombreB;
        this.distanciaMetros = distanciaMetros;
        this.valor = valor;
        this.motivo = motivo;
    }

    public String getTipo() { return tipo; }
    public String getNombreA() { return nombreA; }
    public String getNombreB() { return nombreB; }
    public double getDistanciaMetros() { return distanciaMetros; }
    public String getValor() { return valor; }
    public String getMotivo() { return motivo; }
}
