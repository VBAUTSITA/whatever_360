package com.tracker.web.datafillbook;

import javax.persistence.*;

/**
 * READ-ONLY mapping of DF4G_DATO_GENERAL.
 * Fields mapped are those needed by the Spring portal:
 *   - Display (list page, form header)
 *   - Script generation (INTEGRACION, BASELINE, ADYACENCIAS, OTROS)
 *
 * Never written to by the Spring app — all writes are in the SMART schema.
 */
@Entity
@Table(name = "DF4G_DATO_GENERAL")
public class DF4GDatoGeneral {

    @Id
    @Column(name = "ID_DATA_FIELD")
    private Long idDataField;

    @Column(name = "SITE_CODE")
    private String siteCode;

    @Column(name = "SITE_NAME")
    private String siteName;

    @Column(name = "SITE_4G")
    private String site4g;         // eNodeB name e.g. "LIL02066_AEROPUERTO"

    @Column(name = "MBTS")
    private String mbts;           // e.g. "MBTS_LI0206_AEROPUERTO" — used in all {MBTS} suffixes

    @Column(name = "TAC")
    private Integer tac;

    @Column(name = "ENODEB_ID")
    private Long enodebId;

    @Column(name = "ID_TIPO_LTE")
    private Integer idTipoLte;     // FK → DF_TIPO_LTE (1=FDD, 2=TDD)

    @Column(name = "ID_TIPO_SITE")
    private Integer idTipoSite;

    @Column(name = "MME_PRIMARIO")
    private String mmePrimario;

    @Column(name = "MME_SECUNDARIO")
    private String mmeSecundario;

    @Column(name = "UBIGEO_DISTRITO")
    private String ubigeoDistrito;

    @Column(name = "GENERADO")
    private Integer generado;      // 0 = pending, 1 = generated

    @Column(name = "ELIMINADO")
    private Integer eliminado;     // 0 = active, 1 = deleted

    @Column(name = "RNC_NAME")
    private String rncName;

    @Column(name = "NODOB")
    private String nodob;

    // ── getters (no setters — read-only) ─────────────────────────────────────

    public Long    getIdDataField()     { return idDataField; }
    public String  getSiteCode()        { return siteCode; }
    public String  getSiteName()        { return siteName; }
    public String  getSite4g()          { return site4g; }
    public String  getMbts()            { return mbts; }
    public Integer getTac()             { return tac; }
    public Long    getEnodebId()        { return enodebId; }
    public Integer getIdTipoLte()       { return idTipoLte; }
    public Integer getIdTipoSite()      { return idTipoSite; }
    public String  getMmePrimario()     { return mmePrimario; }
    public String  getMmeSecundario()   { return mmeSecundario; }
    public String  getUbigeoDistrito()  { return ubigeoDistrito; }
    public Integer getGenerado()        { return generado; }
    public Integer getEliminado()       { return eliminado; }
    public String  getRncName()         { return rncName; }
    public String  getNodob()           { return nodob; }
}
