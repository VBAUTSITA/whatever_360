# excelreport — sample accounting-style Excel builder (Java 8 / Apache POI)

Working demo of an "advanced" formatted Excel export on Java 8, wired into the
existing app end-to-end:

```
Browser form  ──POST──►  ReporteContableController  ──►  ReporteContableService
 (edit fields)                    │                        (reuses the EXISTING
                                  │                         inventorysummary
                                  ▼                         entity + repo + service)
                        AccountantExcelBuilder  ──►  .xlsx streamed as download
                        (fixed template as code)
```

## Files

| File | Role |
|---|---|
| `ReporteContableController.java` | `GET /excelreport/contable` (form) + `POST /excelreport/contable/download` (xlsx) |
| `ReporteContableParams.java` | Form-backing bean — every user-editable field |
| `ReporteContableService.java` | Loads/aggregates data from the existing `InventarioInmovil2` entity via `InventarioInmovilRepository2` + `InventorySummaryService2`, valorizes stock into money |
| `ReporteContableData.java` | Plain DTO handed to the builder (keeps POI code Spring-free/testable) |
| `AccountantExcelBuilder.java` | **The heart of the sample.** Fixed 2-column layout, 3 tables, all POI styling techniques commented step by step |
| `templates/excelreport/reporte_contable_form.html` | Small Thymeleaf page to edit fields before generating |

## Dependency (add to `pom.xml`)

```xml
<!-- Apache POI for .xlsx. Both lines below are Java 8 compatible:
     4.1.2 = classic/battle-tested with Spring Boot 2.x
     5.x   = current line, minimum Java is still 8 (POI 6 will need 11+) -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>4.1.2</version>
</dependency>
```

No other changes needed:

- **Security**: `WebSecurityConfig` ends with `anyRequest().authenticated()`,
  so the two new URLs already require login.
- **Component scan**: the package sits under `com.tracker.web`, which the
  `@SpringBootApplication` class already scans.
- **Source layout**: this repo mirrors sources as `java/java/...` and
  `templates/templates/...`; in the real project these map to
  `src/main/java/...` and `src/main/resources/templates/...`.

## What the generated workbook demonstrates

- Style/font **caching** (one `CellStyle` per look, never per cell — 64k limit)
- **Accounting number format** with the currency symbol baked in:
  `"S/" #,##0.00;[Red]"S/" (#,##0.00)` — negatives red and in parentheses
- Real numeric cells (sortable/summable), never pre-formatted strings
- Merged title/letterhead banners, colored section bars, boxed table grids,
  zebra striping, `BorderStyle.DOUBLE` rules and `Font.U_DOUBLE_ACCOUNTING`
  on the grand total
- Live `=SUM(...)` formula for the table total
- Freeze pane, hidden gridlines, A4 print setup with fit-to-width
- Streaming straight to `HttpServletResponse` (no temp files), with a note on
  switching to `SXSSFWorkbook` for very large exports

## Try it

1. Add the POI dependency, rebuild, log in.
2. Open `/excelreport/contable`, adjust title/week/currency/unit cost.
3. Click **Generar Excel** → `reporte_contable_semana_N.xlsx` downloads.
