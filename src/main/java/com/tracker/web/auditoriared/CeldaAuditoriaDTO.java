package com.tracker.web.auditoriared;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Celda existente: datos para el mapa y fila de entrada del motor. */
public class CeldaAuditoriaDTO {

    static final String[] COLUMNAS_TA_4G = {
            "ta_0_234_mts_porc", "ta_234_546_mts_porc", "ta_546_1014_mts_porc",
            "ta_1014_1950_mts_porc", "ta_1950_3510_mts_porc", "ta_3510_6630_mts_porc",
            "ta_6630_14430_mts_porc", "ta_mayor_a_14430_mts_porc" };
    static final String[] COLUMNAS_TA_5G = {
            "ta_0_117_mts_porc", "ta_117_507_mts_porc", "ta_507_975_mts_porc",
            "ta_975_1775_mts_porc", "ta_1775_3315_mts_porc", "ta_3315_7215_mts_porc",
            "ta_7215_15015_mts_porc", "ta_mayor_a_15015_mts_porc" };

    private String nombre;
    private String tecnologia;
    private String carrier;
    private String banda;
    private String canalRadio;
    private String fuenteFrecuencia = "CARRIER_BANDA_SIN_CANAL";
    private String estadoTa;
    private String fuenteTa = "NO_IDENTIFICADA";
    private String fuenteRadio;
    private Integer vecinasRespaldo;
    private double latitud;
    private double longitud;
    private double azimuth;
    private int pci;
    private String txrxmode;
    private double[] ta = new double[8];
    private boolean dentroArea;
    private boolean conHallazgo;
    /** Celda de /recursos/asignar con el PCI propuesto (todavia sin guardar), no de la red existente. */
    private boolean planificada;
    // Sector del paso 6 del motor; null si no se pudo calcular (el mapa dibuja uno generico).
    private Double azimuthEfectivo;
    private Double hbeam;
    private Double radioM;
    private Boolean sinTa;

    public CeldaAuditoriaDTO() { }

    public CeldaAuditoriaDTO(String nombre, String tecnologia, String carrier, String banda,
            double latitud, double longitud, double azimuth, int pci, String txrxmode,
            double[] ta) {
        this.nombre = nombre;
        this.tecnologia = tecnologia;
        this.carrier = carrier;
        this.banda = banda;
        this.latitud = latitud;
        this.longitud = longitud;
        this.azimuth = azimuth;
        this.pci = pci;
        this.txrxmode = txrxmode;
        this.ta = ta == null ? new double[8] : ta.clone();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTecnologia() { return tecnologia; }
    public void setTecnologia(String tecnologia) { this.tecnologia = tecnologia; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getBanda() { return banda; }
    public void setBanda(String banda) { this.banda = banda; }
    public String getCanalRadio() { return canalRadio; }
    public void setCanalRadio(String canal) {
        this.canalRadio = canal == null || canal.trim().isEmpty() ? null : canal.trim().toUpperCase(java.util.Locale.ROOT);
        this.fuenteFrecuencia = this.canalRadio == null ? "CARRIER_BANDA_SIN_CANAL" : "CANAL_EXPLICITO";
    }
    public String getFuenteFrecuencia() { return fuenteFrecuencia; }
    public void setFuenteFrecuencia(String fuente) { this.fuenteFrecuencia = fuente; }
    public String getEstadoTa() {
        if (estadoTa != null) return estadoTa;
        if (ta == null || ta.length != 8) return "INVALIDO";
        double suma = 0;
        for (double v : ta) { if (!Double.isFinite(v) || v < 0) return "INVALIDO"; suma += v; }
        if (suma == 0) return "AUSENTE";
        if (suma >= .99 && suma <= 1.01) return "VALIDO_FRACCION";
        if (suma >= 99 && suma <= 101) return "VALIDO_PORCENTAJE";
        return "INVALIDO";
    }
    public void setEstadoTa(String estado) { this.estadoTa = estado; }
    public String getFuenteTa() { return fuenteTa; }
    public void setFuenteTa(String fuente) { this.fuenteTa = fuente; }
    public String getFuenteRadio() { return fuenteRadio; }
    public Integer getVecinasRespaldo() { return vecinasRespaldo; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public double getAzimuth() { return azimuth; }
    public void setAzimuth(double azimuth) { this.azimuth = azimuth; }
    public int getPci() { return pci; }
    public void setPci(int pci) { this.pci = pci; }
    public boolean isDentroArea() { return dentroArea; }
    public void setDentroArea(boolean dentroArea) { this.dentroArea = dentroArea; }
    public boolean isConHallazgo() { return conHallazgo; }
    public void setConHallazgo(boolean conHallazgo) { this.conHallazgo = conHallazgo; }
    public boolean isPlanificada() { return planificada; }
    public void setPlanificada(boolean planificada) { this.planificada = planificada; }
    /** PSS = PCI mod 3; es lo que colorea el mapa. */
    public int getMod() { return ((pci % 3) + 3) % 3; }
    public Double getAzimuthEfectivo() { return azimuthEfectivo; }
    public Double getHbeam() { return hbeam; }
    public Double getRadioM() { return radioM; }
    public Boolean getSinTa() { return sinTa; }

    void aplicarGeometria(GeometriaSector g) {
        this.azimuthEfectivo = g.getAzimuthEfectivo();
        this.hbeam = g.getHbeam();
        this.radioM = g.getRadioM();
        this.sinTa = g.isSinTa();
        this.fuenteRadio = g.getFuenteRadio();
        this.vecinasRespaldo = g.getVecinasRespaldo();
    }
    @JsonIgnore public String getTxrxmode() { return txrxmode; }
    public void setTxrxmode(String txrxmode) { this.txrxmode = txrxmode; }
    @JsonIgnore public double[] getTa() { return ta.clone(); }
    public void setTa(double[] ta) { this.ta = ta == null ? new double[8] : ta.clone(); }

    CeldaAuditoriaDTO copia() {
        CeldaAuditoriaDTO c = new CeldaAuditoriaDTO(nombre, tecnologia, carrier, banda, latitud,
                longitud, azimuth, pci, txrxmode, ta);
        c.dentroArea = dentroArea; c.conHallazgo = conHallazgo; c.planificada = planificada;
        c.azimuthEfectivo = azimuthEfectivo; c.hbeam = hbeam; c.radioM = radioM; c.sinTa = sinTa;
        c.canalRadio = canalRadio; c.fuenteFrecuencia = fuenteFrecuencia;
        c.estadoTa = estadoTa; c.fuenteTa = fuenteTa; c.fuenteRadio = fuenteRadio; c.vecinasRespaldo = vecinasRespaldo;
        return c;
    }

    /** Fila con los nombres de campo que lee PoolCarrier._push del motor JS. */
    Map<String, Object> paraMotor() {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("Cellname", nombre);
        fila.put("Carrier", carrier);
        fila.put("Phycellid", pci);
        fila.put("Freqband", banda);
        fila.put("Txrxmode", txrxmode);
        fila.put("Latitud", latitud);
        fila.put("Longitud", longitud);
        fila.put("Azimuth", azimuth);
        fila.put("_estado_ta", getEstadoTa());
        fila.put("_canal_radio", canalRadio);
        String[] cols = "5G".equals(tecnologia) ? COLUMNAS_TA_5G : COLUMNAS_TA_4G;
        double suma = 0;
        if (getEstadoTa().startsWith("VALIDO")) for (double v : ta) suma += v;
        for (int i = 0; i < cols.length; i++) fila.put(cols[i], suma > 0 && i < ta.length ? ta[i] / suma : 0);
        return fila;
    }
}
