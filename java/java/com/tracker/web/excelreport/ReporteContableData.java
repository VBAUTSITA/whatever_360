package com.tracker.web.excelreport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable snapshot of everything the Excel builder needs to render the
 * report. The builder (AccountantExcelBuilder) receives ONLY this object +
 * the user params — it never touches Spring, JPA or the database, which
 * keeps the POI code testable in a plain JUnit test with fake data.
 *
 * Built by ReporteContableService from the EXISTING entity
 * com.tracker.web.inventorysummary.InventarioInmovil2.
 */
public class ReporteContableData {

    /**
     * One line of Table 1 ("Resumen valorizado por categoria"):
     * the category, its total stock in units, its money value
     * (stock * costoUnitario) and the week-over-week variation.
     */
    public static class LineaCategoria {
        private final String categoria;
        private final long stockUnidades;
        private final BigDecimal importe;
        private final double wowPorcentaje; // e.g. -3.25 means -3.25 %

        public LineaCategoria(String categoria, long stockUnidades,
                              BigDecimal importe, double wowPorcentaje) {
            this.categoria = categoria;
            this.stockUnidades = stockUnidades;
            this.importe = importe;
            this.wowPorcentaje = wowPorcentaje;
        }

        public String getCategoria() { return categoria; }
        public long getStockUnidades() { return stockUnidades; }
        public BigDecimal getImporte() { return importe; }
        public double getWowPorcentaje() { return wowPorcentaje; }
    }

    private final List<LineaCategoria> lineas = new ArrayList<LineaCategoria>();

    // ---- global figures for Table 2 (indicators) and Table 3 (totals) ----
    private long totalStockUnidades;
    private BigDecimal totalImporte = BigDecimal.ZERO;
    private int totalSkus;              // number of item rows (SAP codes) in the week
    private double wowPromedio;         // simple average of the category WOW %

    public List<LineaCategoria> getLineas() { return lineas; }

    public long getTotalStockUnidades() { return totalStockUnidades; }
    public void setTotalStockUnidades(long totalStockUnidades) { this.totalStockUnidades = totalStockUnidades; }

    public BigDecimal getTotalImporte() { return totalImporte; }
    public void setTotalImporte(BigDecimal totalImporte) { this.totalImporte = totalImporte; }

    public int getTotalSkus() { return totalSkus; }
    public void setTotalSkus(int totalSkus) { this.totalSkus = totalSkus; }

    public double getWowPromedio() { return wowPromedio; }
    public void setWowPromedio(double wowPromedio) { this.wowPromedio = wowPromedio; }
}
