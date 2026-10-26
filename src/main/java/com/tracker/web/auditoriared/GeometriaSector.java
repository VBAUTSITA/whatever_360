package com.tracker.web.auditoriared;

/** Sector tal como lo dibuja el motor en el paso 6. */
public final class GeometriaSector {
    private final double azimuthEfectivo;
    private final double hbeam;
    private final double radioM;
    private final boolean sinTa;

    public GeometriaSector(double azimuthEfectivo, double hbeam, double radioM, boolean sinTa) {
        this.azimuthEfectivo = azimuthEfectivo;
        this.hbeam = hbeam;
        this.radioM = radioM;
        this.sinTa = sinTa;
    }

    public double getAzimuthEfectivo() { return azimuthEfectivo; }
    public double getHbeam() { return hbeam; }
    public double getRadioM() { return radioM; }
    public boolean isSinTa() { return sinTa; }
}
