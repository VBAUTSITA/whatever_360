package com.tracker.web.inventorysummaryVERSION2;




public class TotalSummaryDTO3 {

    private final Long currentTotal;
    private final Double wowPercentage;
    //
    
    private final Long inventarioInmovil;

    

    public TotalSummaryDTO3( Long currentTotal, Double wowPercentage,Long inventarioInmovil) {
        this.currentTotal = currentTotal;
        this.wowPercentage = wowPercentage;
        this.inventarioInmovil = inventarioInmovil;
    }

    public Long getCurrentTotal() { return currentTotal; }
    public Double getWowPercentage() { return wowPercentage; }
    
    public Long getInventarioInmovil() { return inventarioInmovil; }

}