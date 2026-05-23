package com.tracker.web.datafillbook;

/**
 * Simple DTO used to pass LOCALCELLID data to the Vínculo section dropdown.
 * Built from the native query result in DF4GDatoDetalleRepository.
 */
public class LocalcellidDto {

    private final Integer localcellid;
    private final String  carrier;
    private final String  sector;

    public LocalcellidDto(Integer localcellid, String carrier, String sector) {
        this.localcellid = localcellid;
        this.carrier     = carrier;
        this.sector      = sector;
    }

    public Integer getLocalcellid() { return localcellid; }
    public String  getCarrier()     { return carrier; }
    public String  getSector()      { return sector; }

    /** Label shown in the UI dropdown: "C1 - S4 (LCID: 14)" */
    public String getLabel() {
        return carrier + " - " + sector + " (LCID: " + localcellid + ")";
    }
}
