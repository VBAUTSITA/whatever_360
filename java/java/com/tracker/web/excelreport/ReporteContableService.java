package com.tracker.web.excelreport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tracker.web.excelreport.ReporteContableData.LineaCategoria;
import com.tracker.web.inventorysummary.CategorySummaryDTO2;
import com.tracker.web.inventorysummary.InventarioInmovil2;
import com.tracker.web.inventorysummary.InventarioInmovilRepository2;
import com.tracker.web.inventorysummary.InventorySummaryService2;

/**
 * Bridges the EXISTING inventorysummary module and the Excel builder.
 *
 * Nothing here is new data access: we reuse
 *   - InventorySummaryService2#buildSummary(...)  -> totals per category
 *   - InventarioInmovilRepository2#findBySemana() -> raw entity rows
 * exactly as the /inventory/summary Thymeleaf screen already does. The only
 * added logic is "valorization": turning stock units into money using the
 * user-editable unit cost (params.costoUnitario), because an accounting
 * report speaks money, not units.
 */
@Service
public class ReporteContableService {

    private final InventorySummaryService2 summaryService;
    private final InventarioInmovilRepository2 inventarioRepo;

    public ReporteContableService(InventorySummaryService2 summaryService,
                                  InventarioInmovilRepository2 inventarioRepo) {
        this.summaryService = summaryService;
        this.inventarioRepo = inventarioRepo;
    }

    /**
     * Collects and aggregates everything the workbook needs.
     *
     * @param semana        week filter (same semantics as /inventory/summary)
     * @param costoUnitario unit cost used to valorize stock; null-safe.
     */
    public ReporteContableData buildData(Integer semana, BigDecimal costoUnitario) {

        // Defensive defaults so a hand-crafted URL can't NPE the report.
        int week = (semana != null) ? semana.intValue() : 4;
        BigDecimal costo = (costoUnitario != null && costoUnitario.signum() >= 0)
                ? costoUnitario
                : BigDecimal.ONE;

        ReporteContableData data = new ReporteContableData();

        // 1) Category aggregation — delegated to the existing service.
        //    (Its second argument "alarmWeek" is accepted but not used by the
        //    current implementation; we pass week-1 to mirror the existing
        //    controller's calling convention.)
        List<CategorySummaryDTO2> resumen = summaryService.buildSummary(week, week - 1);

        long totalStock = 0L;
        BigDecimal totalImporte = BigDecimal.ZERO;
        double wowAcumulado = 0.0;

        for (CategorySummaryDTO2 cat : resumen) {
            long stock = (cat.getCurrentTotal() != null) ? cat.getCurrentTotal().longValue() : 0L;
            double wow = (cat.getWowPercentage() != null) ? cat.getWowPercentage().doubleValue() : 0.0;

            // Money math in BigDecimal, rounded HALF_UP to 2 decimals —
            // the rounding accountants expect. Never accumulate doubles.
            BigDecimal importe = costo
                    .multiply(BigDecimal.valueOf(stock))
                    .setScale(2, RoundingMode.HALF_UP);

            data.getLineas().add(new LineaCategoria(cat.getCategory(), stock, importe, wow));

            totalStock += stock;
            totalImporte = totalImporte.add(importe);
            wowAcumulado += wow;
        }

        // Accounting reports are conventionally sorted by magnitude,
        // biggest amounts first.
        data.getLineas().sort(new Comparator<LineaCategoria>() {
            @Override
            public int compare(LineaCategoria a, LineaCategoria b) {
                return b.getImporte().compareTo(a.getImporte());
            }
        });

        data.setTotalStockUnidades(totalStock);
        data.setTotalImporte(totalImporte);
        data.setWowPromedio(resumen.isEmpty() ? 0.0 : wowAcumulado / resumen.size());

        // 2) SKU count — one extra (cheap) query against the same entity, to
        //    feed the "Indicadores" table. Shows the report can mix data from
        //    the aggregated service AND the raw repository.
        List<InventarioInmovil2> filas = inventarioRepo.findBySemana(week);
        data.setTotalSkus(filas.size());

        return data;
    }
}
