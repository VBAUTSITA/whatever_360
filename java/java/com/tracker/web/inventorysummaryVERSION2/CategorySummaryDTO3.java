package com.tracker.web.inventorysummaryVERSION2;



public class CategorySummaryDTO3 {

    private final String category;
    private final Long currentTotal;
    private final Double wowPercentage;
    //
    
    private final Long inventarioInmovil;

    

    public CategorySummaryDTO3(String category, Long currentTotal, Double wowPercentage,Long inventarioInmovil) {
        this.category = category;
        this.currentTotal = currentTotal;
        this.wowPercentage = wowPercentage;
        this.inventarioInmovil = inventarioInmovil;
    }

    public String getCategory() { return category; }
    public Long getCurrentTotal() { return currentTotal; }
    public Double getWowPercentage() { return wowPercentage; }
    
    public Long getInventarioInmovil() { return inventarioInmovil; }

}