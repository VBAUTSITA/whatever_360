package com.tracker.web.inventorysummaryVERSION2;



public class ItemDetalleDTO3 {

    private String mainCategoria;
    private String categoria;
    private String sapCode;
    private Long stock;
    private Long alarmaInmovil;
    private Double wowPorcentaje;
    
    private String descripcionMaterial;

    public ItemDetalleDTO3(String mainCategoria, String categoria, String sapCode,
                           Long stock, Long alarmaInmovil, Double wowPorcentaje,String descripcionMaterial) {
        this.mainCategoria = mainCategoria;
        this.categoria = categoria;
        this.sapCode = sapCode;
        this.stock = stock;
        this.alarmaInmovil = alarmaInmovil;
        this.wowPorcentaje = wowPorcentaje;
        this.descripcionMaterial = descripcionMaterial;
    }

    public String getMainCategoria() { return mainCategoria; }
    public String getCategoria() { return categoria; }
    public String getSapCode() { return sapCode; }
    public Long getStock() { return stock; }
    public Long getAlarmaInmovil() { return alarmaInmovil; }
    public Double getWowPorcentaje() { return wowPorcentaje; }
    
    public String getDescripcionMaterial() {return descripcionMaterial;}
    // MAIN_CATEGORIA, CATEGORIA, SAP_CODE, STOCK, ALARMA_INMOVIL, WOW_PORCENTAJE
}
