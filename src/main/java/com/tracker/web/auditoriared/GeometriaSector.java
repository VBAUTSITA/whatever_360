package com.tracker.web.auditoriared;

/** Sector tal como lo dibuja el motor en el paso 6. */
public final class GeometriaSector {
    private final double azimuthEfectivo;
    private final double hbeam;
    private final Double radioM;
    private final boolean sinTa;
    private final String fuenteRadio;
    private final int vecinasRespaldo;

    public GeometriaSector(double azimuthEfectivo, double hbeam, double radioM, boolean sinTa) {
        this(azimuthEfectivo, hbeam, radioM, sinTa, sinTa ? "RESPALDO_VECINAS" : "TA_P80", 0);
    }

    public GeometriaSector(double azimuthEfectivo, double hbeam, Double radioM, boolean sinTa, String fuenteRadio, int vecinasRespaldo) {
        this.azimuthEfectivo = azimuthEfectivo;
        this.hbeam = hbeam;
        this.radioM = radioM;
        this.sinTa = sinTa;
        this.fuenteRadio = fuenteRadio;
        this.vecinasRespaldo = vecinasRespaldo;
    }

    public double getAzimuthEfectivo() { return azimuthEfectivo; }
    public double getHbeam() { return hbeam; }
    public Double getRadioM() { return radioM; }
    public boolean isSinTa() { return sinTa; }
    public String getFuenteRadio() { return fuenteRadio; }
    public int getVecinasRespaldo() { return vecinasRespaldo; }
}
