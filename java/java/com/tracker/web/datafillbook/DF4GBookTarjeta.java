package com.tracker.web.datafillbook;

import javax.persistence.*;

/**
 * Maps DF4G_BOOK_TARJETA — one row per baseband card (BBU slot).
 * Multiple tarjetas per DataField are supported.
 * Each produces: ADD BRD + ADD BASEBANDEQM in the INTEGRACION script.
 */
@Entity
@Table(name = "DF4G_BOOK_TARJETA")
public class DF4GBookTarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator  = "seq_book_tarjeta")
    @SequenceGenerator(name           = "seq_book_tarjeta",
                       sequenceName   = "SEQ_DF4G_BOOK_TARJETA",
                       allocationSize = 1)
    @Column(name = "ID_BOOK_TARJETA")
    private Long idBookTarjeta;

    @Column(name = "ID_BOOK_GENERAL", nullable = false)
    private Long idBookGeneral;

    @Column(name = "ID_DATA_FIELD", nullable = false)
    private Long idDataField;

    @Column(name = "SLOT", nullable = false)
    private Integer slot;

    @Column(name = "BBWS", nullable = false)
    private String bbws;   // e.g. "LTE_FDD-1", "LTE_FDD-2"

    // ── Constructors ─────────────────────────────────────────────────────────

    public DF4GBookTarjeta() {}

    public DF4GBookTarjeta(Long idBookGeneral, Long idDataField,
                            Integer slot, String bbws) {
        this.idBookGeneral = idBookGeneral;
        this.idDataField   = idDataField;
        this.slot          = slot;
        this.bbws          = bbws;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long    getIdBookTarjeta() { return idBookTarjeta; }
    public Long    getIdBookGeneral() { return idBookGeneral; }
    public Long    getIdDataField()   { return idDataField; }
    public Integer getSlot()          { return slot; }
    public String  getBbws()          { return bbws; }

    public void setIdBookGeneral(Long v)  { this.idBookGeneral = v; }
    public void setIdDataField(Long v)    { this.idDataField = v; }
    public void setSlot(Integer v)        { this.slot = v; }
    public void setBbws(String v)         { this.bbws = v; }
}
