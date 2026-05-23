package com.tracker.web.datafillbook;

/**
 * Projection DTO built from the native query result of
 * {@code DF4GDatoDetalleRepository.findCellDataForIntegracionScript}.
 *
 * No entity class is created for DF_BW, DF_TIPO_LTE, DF_CPRICOMPRESION,
 * DF_TXRX_MODE — they are tiny lookup tables joined inline (Option B).
 */
public class CellScriptRowDto {

    private final String  cellname;
    private final Integer lteBand;
    private final Integer dlearfcn;
    private final String  abreviaturaConfig;   // "CELL_BW_N75" etc.
    private final Integer cellId;
    private final Integer localcellid;
    private final Integer pci;
    private final Integer prach;
    private final String  tipoLteDesc;         // "FDD" or "TDD"
    private final String  cpriDesc;            // "NORMAL_COMPRESSION" etc.
    private final String  codTxrxmode;         // "4T4R", "2T2R" etc.
    private final Double  rsPower;
    private final Integer idCarrier;

    public CellScriptRowDto(Object[] row) {
        this.cellname          = str(row[0]);
        this.lteBand           = num(row[1]);
        this.dlearfcn          = num(row[2]);
        this.abreviaturaConfig = str(row[3]);
        this.cellId            = num(row[4]);
        this.localcellid       = num(row[5]);
        this.pci               = num(row[6]);
        this.prach             = num(row[7]);
        this.tipoLteDesc       = str(row[8]);
        this.cpriDesc          = str(row[9]);
        this.codTxrxmode       = str(row[10]);
        this.rsPower           = row[11] != null ? ((Number) row[11]).doubleValue() : null;
        this.idCarrier         = num(row[12]);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String  str(Object o) { return o != null ? o.toString() : ""; }
    private static Integer num(Object o) { return o != null ? ((Number) o).intValue() : null; }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String  getCellname()          { return cellname; }
    public Integer getLteBand()           { return lteBand; }
    public Integer getDlearfcn()          { return dlearfcn; }
    public String  getAbreviaturaConfig() { return abreviaturaConfig; }
    public Integer getCellId()            { return cellId; }
    public Integer getLocalcellid()       { return localcellid; }
    public Integer getPci()               { return pci; }
    public Integer getPrach()             { return prach; }
    public String  getTipoLteDesc()       { return tipoLteDesc; }
    public String  getCpriDesc()          { return cpriDesc; }
    public String  getCodTxrxmode()       { return codTxrxmode; }
    public Double  getRsPower()           { return rsPower; }
    public Integer getIdCarrier()         { return idCarrier; }

    /**
     * Returns the CRSPORTNUM suffix from cod_txrxmode.
     * "4T4R" → "4",  "2T2R" → "2",  "1T1R" → "1"
     * Falls back to "4" if unparseable.
     */
    public String getCrsPortNum() {
        if (codTxrxmode != null && codTxrxmode.length() >= 3) {
            return String.valueOf(codTxrxmode.charAt(2));
        }
        return "4";
    }

    /**
     * Returns rs_power × 10 as integer for MOD PDSCHCFG REFERENCESIGNALPWR.
     */
    public Integer getRsPowerX10() {
        if (rsPower == null) return null;
        return (int) Math.round(rsPower * 10);
    }
}
