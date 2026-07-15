package com.tracker.web.valorizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Snapshot table for the COMBINED solution (POSTE/NAT + BOOKRRU).
 *
 * PHASE 2 — table and entity are ready so the schema doesn't change later,
 * but the combined Excel builder is not wired yet (INDOOR and OUTDOOR were
 * requested first). Per the agreement: "Para indoor + outdoor: plantearemos
 * un input para ingresar tanto el área total como las antenas EM", hence
 * the two extra columns: the indoor area drives the BOOK RRU count while
 * the EM count is typed directly.
 *
 * The inherited AREA_TOTAL_M2 keeps the TOTAL area of the valorization;
 * AREA_INDOOR_M2 is the portion covered by BOOK RRUs.
 */
@Entity
@Table(name = "FORM_VALORIZACION_OUTDOOR_PLUSINDOOR")
public class FormValorizacionOutdoorPlusIndoor extends FormValorizacionBase {

    /** Indoor portion of the area -> ceil(/250) BOOK RRUs. */
    @Column(name = "AREA_INDOOR_M2")
    private Double areaIndoorM2;

    /** Easy Macro antennas typed by the RF user. */
    @Column(name = "ANTENAS_EM")
    private Integer antenasEm = 1;

    /** Postes for SAQ licensing — usually 1. */
    @Column(name = "CANTIDAD_POSTES")
    private Integer cantidadPostes = 1;

    public Double getAreaIndoorM2() { return areaIndoorM2; }
    public void setAreaIndoorM2(Double areaIndoorM2) { this.areaIndoorM2 = areaIndoorM2; }

    public Integer getAntenasEm() { return antenasEm; }
    public void setAntenasEm(Integer antenasEm) { this.antenasEm = antenasEm; }

    public Integer getCantidadPostes() { return cantidadPostes; }
    public void setCantidadPostes(Integer cantidadPostes) { this.cantidadPostes = cantidadPostes; }
}
