package com.tracker.web.datafillbook;

import javax.persistence.*;

/**
 * Maps DF4G_BOOK_VINCULO — the link between a logical cell and physical hardware.
 *
 * Each row produces one command in the integration script:
 *   ADD EUCELLSECTOREQM:LOCALCELLID=x,BASEBANDEQMID=y,SECTOREQMID=z;{MBTS}
 *
 * LOCALCELLID  — from DF4G_DATO_DETALLE joined to DF_SECTOR (Step 2 data)
 * BASEBANDEQMID — from DF4G_BOOK_TARJETA / DF4G_BOOK_GENERAL (which card)
 * SECTOREQMID   — from DF4G_BOOK_GENERAL (which antenna port group)
 */
@Entity
@Table(name = "DF4G_BOOK_VINCULO")
public class DF4GBookVinculo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator  = "seq_book_vinculo")
    @SequenceGenerator(name           = "seq_book_vinculo",
                       sequenceName   = "SEQ_DF4G_BOOK_VINCULO",
                       allocationSize = 1)
    @Column(name = "ID_VINCULO")
    private Long idVinculo;

    @Column(name = "ID_BOOK_GENERAL", nullable = false)
    private Long idBookGeneral;

    @Column(name = "ID_DATA_FIELD", nullable = false)
    private Long idDataField;

    @Column(name = "LOCALCELLID", nullable = false)
    private Integer localcellid;

    @Column(name = "BASEBANDEQMID", nullable = false)
    private Integer basebandeqmid;

    @Column(name = "SECTOREQMID", nullable = false)
    private Integer sectoreqmid;

    // ── constructors ──────────────────────────────────────────────────────────

    public DF4GBookVinculo() {}

    public DF4GBookVinculo(Long idBookGeneral, Long idDataField,
                            Integer localcellid,
                            Integer basebandeqmid,
                            Integer sectoreqmid) {
        this.idBookGeneral = idBookGeneral;
        this.idDataField   = idDataField;
        this.localcellid   = localcellid;
        this.basebandeqmid = basebandeqmid;
        this.sectoreqmid   = sectoreqmid;
    }

    // ── getters ───────────────────────────────────────────────────────────────

    public Long    getIdVinculo()     { return idVinculo; }
    public Long    getIdBookGeneral() { return idBookGeneral; }
    public Long    getIdDataField()   { return idDataField; }
    public Integer getLocalcellid()   { return localcellid; }
    public Integer getBasebandeqmid() { return basebandeqmid; }
    public Integer getSectoreqmid()   { return sectoreqmid; }

    // ── setters ───────────────────────────────────────────────────────────────

    public void setIdBookGeneral(Long v)    { this.idBookGeneral = v; }
    public void setIdDataField(Long v)      { this.idDataField = v; }
    public void setLocalcellid(Integer v)   { this.localcellid = v; }
    public void setBasebandeqmid(Integer v) { this.basebandeqmid = v; }
    public void setSectoreqmid(Integer v)   { this.sectoreqmid = v; }
}
