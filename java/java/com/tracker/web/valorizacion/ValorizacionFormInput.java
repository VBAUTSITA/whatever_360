package com.tracker.web.valorizacion;

/**
 * Form-backing bean for the RF user's screen. Per the agreement (point 4)
 * the RF user only NEEDS to type the valorization id and the total area;
 * everything else is either auto-filled from the PAP, derived, or an
 * optional refinement (floors, department, EM/poste counts).
 */
public class ValorizacionFormInput {

    /** ID VALORIZACIÓN — the manual key into the PAP view. */
    private Long idValorizacion;

    /** INDOOR / OUTDOOR / INDOOR_MAS_OUTDOOR (dropdown, never free text). */
    private TipoSolucion tipoSolucion = TipoSolucion.INDOOR;

    /** ÁREA TOTAL DE LAS ZONAS A VALORIZAR (m2). */
    private Double areaTotalM2;

    private Integer cantidadPisos = 1;
    private String pisosDetalle = "";
    private String departamento = "LIMA";

    /** OUTDOOR only: Easy Macro antennas ("generalmente es 1 sola EM"). */
    private Integer antenasEm = 1;

    /** OUTDOOR only: postes for the SAQ license service ("suele ser 1"). */
    private Integer cantidadPostes = 1;

    // ===== getters / setters =====
    public Long getIdValorizacion() { return idValorizacion; }
    public void setIdValorizacion(Long idValorizacion) { this.idValorizacion = idValorizacion; }

    public TipoSolucion getTipoSolucion() { return tipoSolucion; }
    public void setTipoSolucion(TipoSolucion tipoSolucion) { this.tipoSolucion = tipoSolucion; }

    public Double getAreaTotalM2() { return areaTotalM2; }
    public void setAreaTotalM2(Double areaTotalM2) { this.areaTotalM2 = areaTotalM2; }

    public Integer getCantidadPisos() { return cantidadPisos; }
    public void setCantidadPisos(Integer cantidadPisos) { this.cantidadPisos = cantidadPisos; }

    public String getPisosDetalle() { return pisosDetalle; }
    public void setPisosDetalle(String pisosDetalle) { this.pisosDetalle = pisosDetalle; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public Integer getAntenasEm() { return antenasEm; }
    public void setAntenasEm(Integer antenasEm) { this.antenasEm = antenasEm; }

    public Integer getCantidadPostes() { return cantidadPostes; }
    public void setCantidadPostes(Integer cantidadPostes) { this.cantidadPostes = cantidadPostes; }
}
