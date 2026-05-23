package com.tracker.web.inventorysummaryVERSION2;


public class mainCategorySummaryDTO3 {

    private final String mainCategory;
    private final Long currentTotal;
    private final Double wowPercentage;
  
    //nuevo
    private final Long inventarioInmovil;

    public mainCategorySummaryDTO3(String mainCategory, Long currentTotal, Double wowPercentage, Long inventarioInmovil) {
        this.mainCategory = mainCategory;
        this.currentTotal = currentTotal;
        this.wowPercentage = wowPercentage;
        this.inventarioInmovil = inventarioInmovil;
    }

    public String getMainCategory() { return mainCategory; }
    public Long getCurrentTotal() { return currentTotal; }
    public Double getWowPercentage() { return wowPercentage; }
    public Long getInventarioInmovil() { return inventarioInmovil; }

}


/*
public class CategorySummaryDTO3 {

    private final String category;
    private final Long currentTotal;
    private final Double wowPercentage;

    public CategorySummaryDTO3(String category, Long currentTotal, Double wowPercentage) {
        this.category = category;
        this.currentTotal = currentTotal;
        this.wowPercentage = wowPercentage;
    }

    public String getCategory() { return category; }
    public Long getCurrentTotal() { return currentTotal; }
    public Double getWowPercentage() { return wowPercentage; }
}*/
