package com.tracker.web.datafillbook;

import javax.persistence.*;

/**
 * READ-ONLY mapping of DF4G_DATO_DETALLE joined to DF_SECTOR.
 *
 * Used exclusively to populate the LOCALCELLID dropdown in the
 * Vínculo section of the Book RRU form (Part 3).
 *
 * This entity is never written to by the Spring app — all writes
 * to DF4G_DATO_DETALLE are done by the existing PHP portal (Step 2).
 *
 * The @Immutable annotation is not used here to keep it simple,
 * but no save() calls should ever be made on this entity.
 *
 * Note: LOCALCELLID comes from DF_SECTOR which is joined inside
 * the Oracle view or via a native query in the repository.
 */
@Entity
@Table(name = "DF4G_DATO_DETALLE")
public class DF4GDatoDetalle {

    @Id
    @Column(name = "ID_DETALLE")
    private Long idDetalle;

    @Column(name = "ID_DATA_FIELD")
    private Long idDataField;

    @Column(name = "ID_SECTOR")
    private Long idSector;

    @Column(name = "ID_CARRIER")
    private Long idCarrier;

    @Column(name = "ELIMINADO")
    private Integer eliminado;   // 0 = active, 1 = deleted

    // getters — no setters, read-only
    public Long    getIdDetalle()   { return idDetalle; }
    public Long    getIdDataField() { return idDataField; }
    public Long    getIdSector()    { return idSector; }
    public Long    getIdCarrier()   { return idCarrier; }
    public Integer getEliminado()   { return eliminado; }
}
