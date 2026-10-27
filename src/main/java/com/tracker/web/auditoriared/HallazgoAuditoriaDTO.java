package com.tracker.web.auditoriared;

/** Un conflicto entre dos celdas, con todo lo que el mapa necesita para dibujarlo. */
public final class HallazgoAuditoriaDTO {
    private final String tipo;
    private final String etiqueta;
    private final String valor;
    private final double distanciaMetros;
    private final String motivo;
    private final CeldaAuditoriaDTO celdaA;
    private final CeldaAuditoriaDTO celdaB;

    public HallazgoAuditoriaDTO(String tipo, String etiqueta, String valor, double distanciaMetros,
            String motivo, CeldaAuditoriaDTO celdaA, CeldaAuditoriaDTO celdaB) {
        this.tipo = tipo;
        this.etiqueta = etiqueta;
        this.valor = valor;
        this.distanciaMetros = distanciaMetros;
        this.motivo = motivo;
        this.celdaA = celdaA;
        this.celdaB = celdaB;
    }

    public String getTipo() { return tipo; }
    public String getEtiqueta() { return etiqueta; }
    public String getValor() { return valor; }
    public double getDistanciaMetros() { return distanciaMetros; }
    public String getMotivo() { return motivo; }
    public String getEvidenciaFrecuencia() {
        return celdaA.getCanalRadio() != null && celdaA.getCanalRadio().equals(celdaB.getCanalRadio())
                ? "CANAL_COINCIDENTE" : "FRECUENCIA_NO_VERIFICADA";
    }
    public CeldaAuditoriaDTO getCeldaA() { return celdaA; }
    public CeldaAuditoriaDTO getCeldaB() { return celdaB; }
}
