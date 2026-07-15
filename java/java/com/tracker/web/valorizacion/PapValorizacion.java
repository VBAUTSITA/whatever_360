package com.tracker.web.valorizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * READ-ONLY mapping over the EXISTING valorization view/table of the PAP.
 *
 * The PAP side is a BLACK BOX by agreement: we do not own its logic, we
 * only consume the fields it exposes. That is why the entity is
 * {@link Immutable} (Hibernate will never try to write it) and why the
 * table/column names below are GENERIC PLACEHOLDERS — point them at the
 * real view when it is provisioned:
 *
 *   @Table(name = "PAP")            <- real view/table name here
 *   IDVALORIZACION                  <- the id typed by the RF user
 *   NOMBRE_EMPRESA                  <- "NOMBRE EMPRESA"
 *   DIRECCION_VALORIZACION          <- "DIRECCION DE VALORIZACION"
 *   LATITUD / LONGITUD              <- coordinates entered by the person
 *                                      who filed the PAP request; used to
 *                                      find the nearest FAREND
 *
 * Only these five fields matter to the platform (agreement point 1);
 * whatever else the view carries is ignored on purpose.
 */
@Entity
@Immutable
@Table(name = "PAP")
public class PapValorizacion {

    @Id
    @Column(name = "IDVALORIZACION")
    private Long idValorizacion;

    @Column(name = "NOMBRE_EMPRESA")
    private String nombreEmpresa;

    @Column(name = "DIRECCION_VALORIZACION")
    private String direccionValorizacion;

    @Column(name = "LATITUD")
    private Double latitud;

    @Column(name = "LONGITUD")
    private Double longitud;

    // ===== getters (read-only entity: no setters on purpose) =====
    public Long getIdValorizacion() { return idValorizacion; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getDireccionValorizacion() { return direccionValorizacion; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
}
