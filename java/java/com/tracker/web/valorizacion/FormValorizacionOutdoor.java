package com.tracker.web.valorizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Snapshot table for OUTDOOR (solución POSTE/NAT — Easy Macro)
 * valorizations. Outdoor is "generalmente 1 sola EM", so the antenna count
 * is entered by the RF user (default 1) instead of derived from the area,
 * and SAQ license management is billed per poste ("suele ser 1").
 */
@Entity
@Table(name = "FORM_VALORIZACION_OUTDOOR")
public class FormValorizacionOutdoor extends FormValorizacionBase {

    /** Postes for the Gestión Licencias (SAQ) service — usually 1. */
    @Column(name = "CANTIDAD_POSTES")
    private Integer cantidadPostes = 1;

    public Integer getCantidadPostes() { return cantidadPostes; }
    public void setCantidadPostes(Integer cantidadPostes) { this.cantidadPostes = cantidadPostes; }
}
