package com.tracker.web.auditoriared;

/** Geometric planning evidence, not measured RF interference or a probability. */
public final class InteraccionVecinaDTO {
    private final CeldaAuditoriaDTO celda;
    private final double distanciaMetros;
    private final Double areaInterseccionM2;
    private final Double interseccionNormalizadaM;
    private final boolean mismoPci;
    private final boolean mismoMod3;
    private final boolean mismoRsi;
    private final boolean sinTaObjetivo;
    private final boolean sinTaVecina;

    public InteraccionVecinaDTO(CeldaAuditoriaDTO celda, double distanciaMetros, Double areaInterseccionM2,
            Double interseccionNormalizadaM, boolean mismoPci, boolean mismoMod3, boolean mismoRsi,
            boolean sinTaObjetivo, boolean sinTaVecina) {
        this.celda = celda; this.distanciaMetros = distanciaMetros;
        this.areaInterseccionM2 = areaInterseccionM2; this.interseccionNormalizadaM = interseccionNormalizadaM;
        this.mismoPci = mismoPci; this.mismoMod3 = mismoMod3; this.mismoRsi = mismoRsi;
        this.sinTaObjetivo = sinTaObjetivo; this.sinTaVecina = sinTaVecina;
    }
    public CeldaAuditoriaDTO getCelda() { return celda; }
    public double getDistanciaMetros() { return distanciaMetros; }
    public Double getAreaInterseccionM2() { return areaInterseccionM2; }
    public Double getInterseccionNormalizadaM() { return interseccionNormalizadaM; }
    public boolean isMismoPci() { return mismoPci; }
    public boolean isMismoMod3() { return mismoMod3; }
    public boolean isMismoRsi() { return mismoRsi; }
    public boolean isSinTaObjetivo() { return sinTaObjetivo; }
    public boolean isSinTaVecina() { return sinTaVecina; }
}
