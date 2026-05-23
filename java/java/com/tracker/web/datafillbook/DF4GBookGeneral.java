package com.tracker.web.datafillbook;

import javax.persistence.*;

/**
 * Maps DF4G_BOOK_GENERAL — the Step 2.5 base record.
 * One record per DataField. Holds the legacy single-RRU / single-sector fields
 * plus metadata used across all script sections.
 *
 * Written by the Spring portal (creation form) or originally by the PHP portal.
 */
@Entity
@Table(name = "DF4G_BOOK_GENERAL")
public class DF4GBookGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator  = "seq_book_general")
    @SequenceGenerator(name           = "seq_book_general",
                       sequenceName   = "SEQ_DF4G_BOOK_GENERAL",
                       allocationSize = 1)
    @Column(name = "ID_BOOK_GENERAL")
    private Long idBookGeneral;

    @Column(name = "ID_DATA_FIELD", nullable = false)
    private Long idDataField;

    // ── Common fields ─────────────────────────────────────────────────────────

    @Column(name = "CODIGO")
    private String codigo;             // site_code

    @Column(name = "SITE_ADDRESS_BOOK")
    private String siteAddressBook;    // user-supplied address label for RN= field

    // ── TARJETA (board) ───────────────────────────────────────────────────────

    @Column(name = "TARJETA")
    private Integer tarjeta;           // 1 if legacy single-tarjeta record

    @Column(name = "SLOT")
    private Integer slot;

    @Column(name = "BBWS")
    private String bbws;               // e.g. "LTE_FDD-1"

    @Column(name = "BASEBANDEQMID")
    private Integer basebandeqmid;

    // ── RRU chain ─────────────────────────────────────────────────────────────

    @Column(name = "RCN")
    private Integer rcn;

    @Column(name = "HSN")
    private Integer hsn;

    @Column(name = "HPN")
    private Integer hpn;

    @Column(name = "CASCADA")
    private Integer cascada;           // 0=no, 1=yes → emit ADD RRUCHAIN

    @Column(name = "SRN")
    private Integer srn;

    @Column(name = "PS")
    private Integer ps;

    // ── Derived (set by Oracle procedure CARGA_DATOS_BOOK_RRU) ───────────────

    @Column(name = "RN_CELLNAME")
    private String rnCellname;         // substr(cellname,1,10) + '_' + siteAddressBook

    @Column(name = "RXNUM")
    private Integer rxnum;

    @Column(name = "TXNUM")
    private Integer txnum;

    // ── Sector ────────────────────────────────────────────────────────────────

    @Column(name = "SECTOR_ID")
    private Integer sectorId;

    @Column(name = "LOCATIONNAME")
    private String locationname;       // e.g. "LIL02066"

    @Column(name = "ANTNUM")
    private Integer antnum;            // from DF_BOOK_ANTNUM

    @Column(name = "SECTOREQMID")
    private Integer sectoreqmid;

    // ── Constructors ──────────────────────────────────────────────────────────

    public DF4GBookGeneral() {}

    /** Constructor for creating a new Book General from the Spring portal. */
    public DF4GBookGeneral(Long idDataField, String codigo,
                            String siteAddressBook, Integer slot, String bbws,
                            Integer basebandeqmid, Integer rcn,
                            Integer hsn, Integer hpn, Integer cascada,
                            Integer srn, Integer ps,
                            Integer sectorId, Integer sectoreqmid) {
        this.idDataField    = idDataField;
        this.codigo         = codigo;
        this.siteAddressBook = siteAddressBook;
        this.tarjeta        = 1;   // legacy marker
        this.slot           = slot;
        this.bbws           = bbws;
        this.basebandeqmid  = basebandeqmid;
        this.rcn            = rcn;
        this.hsn            = hsn;
        this.hpn            = hpn;
        this.cascada        = cascada;
        this.srn            = srn;
        this.ps             = ps;
        this.sectorId       = sectorId;
        this.sectoreqmid    = sectoreqmid;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long    getIdBookGeneral()   { return idBookGeneral; }
    public Long    getIdDataField()     { return idDataField; }
    public String  getCodigo()          { return codigo; }
    public String  getSiteAddressBook() { return siteAddressBook; }
    public Integer getTarjeta()         { return tarjeta; }
    public Integer getSlot()            { return slot; }
    public String  getBbws()            { return bbws; }
    public Integer getBasebandeqmid()   { return basebandeqmid; }
    public Integer getRcn()             { return rcn; }
    public Integer getHsn()             { return hsn; }
    public Integer getHpn()             { return hpn; }
    public Integer getCascada()         { return cascada; }
    public Integer getSrn()             { return srn; }
    public Integer getPs()              { return ps; }
    public String  getRnCellname()      { return rnCellname; }
    public Integer getRxnum()           { return rxnum; }
    public Integer getTxnum()           { return txnum; }
    public Integer getSectorId()        { return sectorId; }
    public String  getLocationname()    { return locationname; }
    public Integer getAntnum()          { return antnum; }
    public Integer getSectoreqmid()     { return sectoreqmid; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setIdDataField(Long v)      { this.idDataField = v; }
    public void setCodigo(String v)         { this.codigo = v; }
    public void setSiteAddressBook(String v){ this.siteAddressBook = v; }
    public void setTarjeta(Integer v)       { this.tarjeta = v; }
    public void setSlot(Integer v)          { this.slot = v; }
    public void setBbws(String v)           { this.bbws = v; }
    public void setBasebandeqmid(Integer v) { this.basebandeqmid = v; }
    public void setRcn(Integer v)           { this.rcn = v; }
    public void setHsn(Integer v)           { this.hsn = v; }
    public void setHpn(Integer v)           { this.hpn = v; }
    public void setCascada(Integer v)       { this.cascada = v; }
    public void setSrn(Integer v)           { this.srn = v; }
    public void setPs(Integer v)            { this.ps = v; }
    public void setRnCellname(String v)     { this.rnCellname = v; }
    public void setRxnum(Integer v)         { this.rxnum = v; }
    public void setTxnum(Integer v)         { this.txnum = v; }
    public void setSectorId(Integer v)      { this.sectorId = v; }
    public void setLocationname(String v)   { this.locationname = v; }
    public void setAntnum(Integer v)        { this.antnum = v; }
    public void setSectoreqmid(Integer v)   { this.sectoreqmid = v; }
}
