package com.tracker.web.inventorysummary;

public class CategorySummaryDTO2 {

    private final String category;
    private final Long currentTotal;
    private final Double wowPercentage;

    public CategorySummaryDTO2(String category, Long currentTotal, Double wowPercentage) {
        this.category = category;
        this.currentTotal = currentTotal;
        this.wowPercentage = wowPercentage;
    }

    public String getCategory() { return category; }
    public Long getCurrentTotal() { return currentTotal; }
    public Double getWowPercentage() { return wowPercentage; }
}
