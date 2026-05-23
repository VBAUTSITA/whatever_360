package com.tracker.web.datafillbook;

import javax.persistence.*;

/**
 * Maps DF4G_BOOK_RRU — one row per physical RRU per DataField.
 *
 * TIPO = "BOOK"       → ADD RRU: ... RT=MRRU ...
 * TIPO = "EASY_MACRO" → ADD RRU: ... RT=AIRU ...
 *
 * When CASCADA = 1, the script also emits ADD RRUCHAIN before ADD RRU,
 * using HSN and HPN for the chain head port reference.
 */
@Entity
@Table(name = "DF4G_BOOK_RRU")
public class DF4GBookRru {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator  = "seq_book_rru")
    @SequenceGenerator(name           = "seq_book_rru",
                       sequenceName   = "SEQ_DF4G_BOOK_RRU",
                       allocationSize = 1)
    @Column(name = "ID_BOOK_RRU")
    private Long idBookRru;

    @Column(name = "ID_BOOK_GENERAL", nullable = false)
    private Long idBookGeneral;

    @Column(name = "ID_DATA_FIELD", nullable = false)
    private Long idDataField;

    /** "BOOK" or "EASY_MACRO" */
    @Column(name = "TIPO", nullable = false)
    private String tipo;

    @Column(name = "SRN", nullable = false)
    private Integer srn;

    @Column(name = "RCN", nullable = false)
    private Integer rcn;

    @Column(name = "PS")
    private Integer ps;

    @Column(name = "RN_CELLNAME")
    private String rnCellname;

    @Column(name = "RXNUM")
    private Integer rxnum;

    @Column(name = "TXNUM")
    private Integer txnum;

    /** 0 = no cascade, 1 = cascade (emit ADD RRUCHAIN before ADD RRU) */
    @Column(name = "CASCADA")
    private Integer cascada;

    @Column(name = "HSN")
    private Integer hsn;

    @Column(name = "HPN")
    private Integer hpn;

    // ── constructors ──────────────────────────────────────────────────────────

    public DF4GBookRru() {}

    public DF4GBookRru(Long idBookGeneral, Long idDataField, String tipo,
                        Integer srn, Integer rcn, Integer ps,
                        String rnCellname, Integer rxnum, Integer txnum,
                        Integer cascada, Integer hsn, Integer hpn) {
        this.idBookGeneral = idBookGeneral;
        this.idDataField   = idDataField;
        this.tipo          = tipo;
        this.srn           = srn;
        this.rcn           = rcn;
        this.ps            = ps;
        this.rnCellname    = rnCellname;
        this.rxnum         = rxnum;
        this.txnum         = txnum;
        this.cascada       = cascada;
        this.hsn           = hsn;
        this.hpn           = hpn;
    }

    // ── getters ───────────────────────────────────────────────────────────────

    public Long    getIdBookRru()     { return idBookRru; }
    public Long    getIdBookGeneral() { return idBookGeneral; }
    public Long    getIdDataField()   { return idDataField; }
    public String  getTipo()          { return tipo; }
    public Integer getSrn()           { return srn; }
    public Integer getRcn()           { return rcn; }
    public Integer getPs()            { return ps; }
    public String  getRnCellname()    { return rnCellname; }
    public Integer getRxnum()         { return rxnum; }
    public Integer getTxnum()         { return txnum; }
    public Integer getCascada()       { return cascada; }
    public Integer getHsn()           { return hsn; }
    public Integer getHpn()           { return hpn; }

    // ── setters ───────────────────────────────────────────────────────────────

    public void setIdBookGeneral(Long v)   { this.idBookGeneral = v; }
    public void setIdDataField(Long v)     { this.idDataField = v; }
    public void setTipo(String v)          { this.tipo = v; }
    public void setSrn(Integer v)          { this.srn = v; }
    public void setRcn(Integer v)          { this.rcn = v; }
    public void setPs(Integer v)           { this.ps = v; }
    public void setRnCellname(String v)    { this.rnCellname = v; }
    public void setRxnum(Integer v)        { this.rxnum = v; }
    public void setTxnum(Integer v)        { this.txnum = v; }
    public void setCascada(Integer v)      { this.cascada = v; }
    public void setHsn(Integer v)          { this.hsn = v; }
    public void setHpn(Integer v)          { this.hpn = v; }
}
