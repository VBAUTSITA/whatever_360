package com.tracker.web.valorizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Catalog of candidate FAREND sites (the LIxxxx sites whose BBU the new
 * RRU/Easy Macro extends from). With the PAP coordinates we pick the
 * geographically nearest row of this table (see
 * ValorizacionService#farendMasCercano).
 *
 * GENERIC PLACEHOLDER TABLE: the app already has site tables with
 * coordinates (e.g. the mapa module's markers). Either point this mapping
 * at one of those tables, or create FAREND_SITES with the DDL in the
 * module README. The service only needs codigo + latitud + longitud.
 *
 * "SE DEBE VERIFICAR LA UBICACIÓN FÍSICA DE LA BBU, DEBIDO A QUE EL
 * RECORRIDO DE FIBRA DEBE SER DE LA BBU A LA UBICACIÓN DEL CLIENTE" —
 * nearest-by-air-distance is a first candidate, not a final engineering
 * decision; the generated Excel repeats that warning.
 */
@Entity
@Immutable
@Table(name = "FAREND_SITES")
public class FarendSite {

    /** Site code as it must print in the Excel, e.g. "LI0029_LOS_FLAMENCOS". */
    @Id
    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "LATITUD")
    private Double latitud;

    @Column(name = "LONGITUD")
    private Double longitud;

    public String getCodigo() { return codigo; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
}
