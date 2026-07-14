package com.tracker.web.excelreport;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.tracker.web.excelreport.ReporteContableData.LineaCategoria;

/**
 * =====================================================================
 *  FIXED-TEMPLATE EXCEL BUILDER — classic accounting-style report
 * =====================================================================
 *
 * The report layout is FIXED and lives in this class ("template as code"):
 * every run produces the same shape, only the data changes. Two content
 * columns (A = concept, B = amount) and three stacked tables:
 *
 *      +--------------------------------------------------+
 *      |            ESTADO DE INVENTARIO VALORIZADO       |  <- title banner
 *      |                WHATEVER 360 S.A.C.               |  <- letterhead
 *      | Periodo: Semana 4          Moneda: S/            |
 *      | Elaborado por: ...         Emitido: 14/07/2026   |
 *      |                                                  |
 *      | 1. RESUMEN VALORIZADO POR CATEGORIA              |  <- TABLE 1
 *      |    CATEGORIA            |            IMPORTE     |
 *      |    Antenas              |          12,340.00     |
 *      |    ...                  |               ...      |
 *      |    TOTAL VALORIZADO     |  =SUM(...) double rule |
 *      |                                                  |
 *      | 2. INDICADORES DE GESTION                        |  <- TABLE 2
 *      |    INDICADOR            |               VALOR    |
 *      |    Items (SKU)          |                 152    |
 *      |    ...                  |                 ...    |
 *      |                                                  |
 *      | 3. RESUMEN CONTABLE                              |  <- TABLE 3
 *      |    Stock total (unid.)  |              45,210    |
 *      |    Costo unitario       |                1.00    |
 *      |    VALOR TOTAL          |  =ref  double underline|  <- grand total
 *      |                                                  |
 *      | Notas: ...                                       |
 *      +--------------------------------------------------+
 *
 * DEPENDENCY (Java 8 compatible — see module README.md):
 *
 *   <dependency>
 *     <groupId>org.apache.poi</groupId>
 *     <artifactId>poi-ooxml</artifactId>
 *     <version>4.1.2</version>   <!-- or 5.x: same API, still Java 8 min -->
 *   </dependency>
 *
 * POI CONCEPTS DEMONSTRATED HERE (the "how"):
 *
 *  1. STYLE CACHING. CellStyle/Font objects live in the WORKBOOK, not in the
 *     cell. A workbook supports at most ~64,000 styles, so you must create
 *     each distinct style ONCE and reuse it for every cell that looks the
 *     same. That is what the inner {@link Styles} class does. Creating a
 *     style per cell inside a loop is the #1 classic POI bug (file bloats,
 *     then corrupts past the limit).
 *
 *  2. NUMBER FORMATS, not strings. Money is written as a real numeric cell
 *     with an "accounting" display format:  "S/" #,##0.00;[Red]"S/" (#,##0.00)
 *     (positive; negative-in-red-parentheses). The user can still sum/sort
 *     the column in Excel. Writing pre-formatted Strings ("S/ 1,234.00")
 *     is the #2 classic bug — it produces dead text cells.
 *
 *  3. COLORS. This class uses the built-in IndexedColors palette, which is
 *     identical across POI 4.x/5.x and across .xls/.xlsx. If you need exact
 *     corporate RGB, cast to XSSFCellStyle and call
 *     setFillForegroundColor(new XSSFColor(new byte[]{r,g,b})) — xlsx only.
 *
 *  4. STRUCTURE: merged regions for banners (CellRangeAddress), thin/medium
 *     borders for table grids, BorderStyle.DOUBLE + Font.U_DOUBLE_ACCOUNTING
 *     for the classic accountant "double rule" on totals, freeze panes,
 *     column widths in 1/256 of a character, print setup (A4, fit-to-width)
 *     and hidden gridlines so the sheet looks like a printed report.
 *
 *  5. LIVE FORMULAS. Totals are written as =SUM(...) ranges referencing the
 *     data rows, so if the user edits an amount in Excel the totals follow.
 *
 *  6. STREAMING. The workbook is written straight to the servlet
 *     OutputStream. For this report size XSSFWorkbook (in-memory) is right;
 *     if you ever emit >100k rows switch `new XSSFWorkbook()` for
 *     `new SXSSFWorkbook(100)` (same API) and call wb.dispose() at the end.
 *
 * This class is intentionally free of Spring/JPA/servlet imports (except the
 * @Component marker and OutputStream): give it data + params and any test
 * can call build() against a ByteArrayOutputStream.
 */
@Component
public class AccountantExcelBuilder {

    /** Content columns: A = concept, B = amount. */
    private static final int COL_CONCEPTO = 0;
    private static final int COL_IMPORTE = 1;

    /**
     * Builds the workbook and writes it to {@code out}.
     * The caller owns the stream (the servlet container closes it).
     */
    public void build(OutputStream out, ReporteContableParams params, ReporteContableData data)
            throws IOException {

        // try-with-resources: the workbook itself must always be closed to
        // free its temp resources, even if writing fails halfway.
        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Reporte Contable");

            // ---- page cosmetics: look like paper, print like paper -------
            sheet.setDisplayGridlines(false);          // report look, not grid look
            sheet.setFitToPage(true);                  // shrink to one page wide
            PrintSetup ps = sheet.getPrintSetup();
            ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
            ps.setFitWidth((short) 1);
            ps.setFitHeight((short) 0);                // as many pages tall as needed

            // Column widths are in 1/256ths of a character width.
            sheet.setColumnWidth(COL_CONCEPTO, 46 * 256);
            sheet.setColumnWidth(COL_IMPORTE, 22 * 256);

            // All reusable styles, created exactly once (see concept #1).
            Styles st = new Styles(wb, params.getMoneda());

            int r = 0; // running row cursor — every block appends and advances

            r = writeLetterhead(sheet, st, params, r);
            r = writeTablaResumen(sheet, st, data, r);      // TABLE 1
            r = writeTablaIndicadores(sheet, st, data, r);  // TABLE 2
            r = writeTablaTotales(sheet, st, params, data, r); // TABLE 3
            writeFooter(sheet, st, params, r);

            // Freeze everything above the first data row of Table 1
            // (title + letterhead + table-1 headers = rows 0..6), so the
            // headers stay visible while scrolling a long category list.
            sheet.createFreezePane(0, 7);

            wb.write(out);
        }
    }

    // =================================================================
    // Letterhead: title banner, company, period / currency / author / date
    // =================================================================
    private int writeLetterhead(Sheet sheet, Styles st, ReporteContableParams params, int r) {

        // --- Row 0: big title on a dark banner, merged across A:B ---------
        Row title = sheet.createRow(r);
        title.setHeightInPoints(28f);
        text(title, COL_CONCEPTO, safe(params.getTitulo(), "REPORTE CONTABLE"), st.titulo);
        text(title, COL_IMPORTE, "", st.titulo); // style BOTH cells of a merge:
        // merging keeps only the top-left VALUE, but each covered cell keeps
        // its own borders/fill, so an unstyled covered cell leaves a "hole".
        sheet.addMergedRegion(new CellRangeAddress(r, r, COL_CONCEPTO, COL_IMPORTE));
        r++;

        // --- Row 1: company letterhead, merged ----------------------------
        Row emp = sheet.createRow(r);
        emp.setHeightInPoints(18f);
        text(emp, COL_CONCEPTO, safe(params.getEmpresa(), ""), st.empresa);
        text(emp, COL_IMPORTE, "", st.empresa);
        sheet.addMergedRegion(new CellRangeAddress(r, r, COL_CONCEPTO, COL_IMPORTE));
        r++;

        // --- Rows 2-3: metadata in two columns -----------------------------
        Row meta1 = sheet.createRow(r++);
        text(meta1, COL_CONCEPTO, "Periodo: Semana " + params.getSemana(), st.meta);
        text(meta1, COL_IMPORTE, "Moneda: " + params.getMoneda(), st.metaDerecha);

        Row meta2 = sheet.createRow(r++);
        text(meta2, COL_CONCEPTO, "Elaborado por: " + safe(params.getElaboradoPor(), "-"), st.meta);
        text(meta2, COL_IMPORTE,
                "Emitido: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
                st.metaDerecha);

        return r + 1; // one blank spacer row
    }

    // =================================================================
    // TABLE 1 — "Resumen valorizado por categoria" (the ledger body)
    // =================================================================
    private int writeTablaResumen(Sheet sheet, Styles st, ReporteContableData data, int r) {

        r = sectionBar(sheet, st, "1. RESUMEN VALORIZADO POR CATEGORIA", r);

        // Column header row of the table (grey, bold, boxed).
        Row head = sheet.createRow(r++);
        text(head, COL_CONCEPTO, "CATEGORIA", st.cabeceraTabla);
        text(head, COL_IMPORTE, "IMPORTE", st.cabeceraTablaDerecha);

        int firstDataRow = r; // remember the range for the =SUM() formula

        if (data.getLineas().isEmpty()) {
            // Graceful empty state instead of a broken =SUM(B8:B7) formula.
            Row empty = sheet.createRow(r++);
            text(empty, COL_CONCEPTO, "— Sin datos para la semana seleccionada —", st.cuerpoTexto);
            number(empty, COL_IMPORTE, 0d, st.cuerpoMoneda);
        } else {
            boolean zebra = false;
            for (LineaCategoria linea : data.getLineas()) {
                Row row = sheet.createRow(r++);
                // Zebra striping = two cached styles, alternated. Never
                // create styles inside this loop (concept #1).
                text(row, COL_CONCEPTO, linea.getCategoria(),
                        zebra ? st.cuerpoTextoZebra : st.cuerpoTexto);
                number(row, COL_IMPORTE, linea.getImporte().doubleValue(),
                        zebra ? st.cuerpoMonedaZebra : st.cuerpoMoneda);
                zebra = !zebra;
            }
        }
        int lastDataRow = r - 1;

        // Total row: label + LIVE =SUM() formula over the data rows.
        // CellRangeAddress rows are 0-based; A1-notation is 1-based, hence +1.
        Row total = sheet.createRow(r++);
        total.setHeightInPoints(16f);
        text(total, COL_CONCEPTO, "TOTAL VALORIZADO", st.totalTexto);
        String rango = "B" + (firstDataRow + 1) + ":B" + (lastDataRow + 1);
        formula(total, COL_IMPORTE, "SUM(" + rango + ")", st.totalMoneda);

        return r + 1; // spacer
    }

    // =================================================================
    // TABLE 2 — "Indicadores de gestion" (plain figures, no currency)
    // =================================================================
    private int writeTablaIndicadores(Sheet sheet, Styles st, ReporteContableData data, int r) {

        r = sectionBar(sheet, st, "2. INDICADORES DE GESTION", r);

        Row head = sheet.createRow(r++);
        text(head, COL_CONCEPTO, "INDICADOR", st.cabeceraTabla);
        text(head, COL_IMPORTE, "VALOR", st.cabeceraTablaDerecha);

        Row r1 = sheet.createRow(r++);
        text(r1, COL_CONCEPTO, "Items registrados (SKU)", st.cuerpoTexto);
        number(r1, COL_IMPORTE, data.getTotalSkus(), st.cuerpoEntero);

        Row r2 = sheet.createRow(r++);
        text(r2, COL_CONCEPTO, "Stock total (unidades)", st.cuerpoTextoZebra);
        number(r2, COL_IMPORTE, data.getTotalStockUnidades(), st.cuerpoEnteroZebra);

        Row r3 = sheet.createRow(r++);
        text(r3, COL_CONCEPTO, "Categorias con movimiento", st.cuerpoTexto);
        number(r3, COL_IMPORTE, data.getLineas().size(), st.cuerpoEntero);

        // WOW arrives as "percent points" (e.g. -3.25 == -3.25 %), so we keep
        // the raw number and append a literal % via the format  #,##0.00"%".
        // If your source stored a FRACTION (0.0325) you would instead write
        // the raw value with the real percent format "0.00%".
        Row r4 = sheet.createRow(r++);
        text(r4, COL_CONCEPTO, "Variacion promedio WOW", st.cuerpoTextoZebra);
        number(r4, COL_IMPORTE, data.getWowPromedio(), st.cuerpoPorcentajeZebra);

        return r + 1; // spacer
    }

    // =================================================================
    // TABLE 3 — "Resumen contable" (grand total with double rule)
    // =================================================================
    private int writeTablaTotales(Sheet sheet, Styles st, ReporteContableParams params,
                                  ReporteContableData data, int r) {

        r = sectionBar(sheet, st, "3. RESUMEN CONTABLE", r);

        Row r1 = sheet.createRow(r++);
        text(r1, COL_CONCEPTO, "Stock total (unidades)", st.cuerpoTexto);
        number(r1, COL_IMPORTE, data.getTotalStockUnidades(), st.cuerpoEntero);

        Row r2 = sheet.createRow(r++);
        text(r2, COL_CONCEPTO, "Costo unitario aplicado", st.cuerpoTextoZebra);
        number(r2, COL_IMPORTE,
                params.getCostoUnitario() != null ? params.getCostoUnitario().doubleValue() : 1d,
                st.cuerpoMonedaZebra);

        // The grand total: bold, highlighted, DOUBLE top border and
        // double-accounting underlined font — the classic "final answer"
        // formatting of an accountant's statement. Written as a plain number
        // (already computed in BigDecimal) rather than a formula, so it stays
        // correct even if someone deletes Table 1.
        Row gt = sheet.createRow(r++);
        gt.setHeightInPoints(18f);
        text(gt, COL_CONCEPTO, "VALOR TOTAL DEL INVENTARIO", st.granTotalTexto);
        number(gt, COL_IMPORTE, data.getTotalImporte().doubleValue(), st.granTotalMoneda);

        return r + 1; // spacer
    }

    // =================================================================
    // Footer: optional free-text note + generation stamp
    // =================================================================
    private void writeFooter(Sheet sheet, Styles st, ReporteContableParams params, int r) {

        if (params.getNotas() != null && !params.getNotas().trim().isEmpty()) {
            Row nota = sheet.createRow(r);
            nota.setHeightInPoints(28f);
            text(nota, COL_CONCEPTO, "Notas: " + params.getNotas().trim(), st.nota);
            text(nota, COL_IMPORTE, "", st.nota);
            sheet.addMergedRegion(new CellRangeAddress(r, r, COL_CONCEPTO, COL_IMPORTE));
            r += 2;
        }

        Row stamp = sheet.createRow(r);
        text(stamp, COL_CONCEPTO,
                "Generado automaticamente por WHATEVER 360 — modulo excelreport (Apache POI).",
                st.pieDePagina);
        text(stamp, COL_IMPORTE, "", st.pieDePagina);
        sheet.addMergedRegion(new CellRangeAddress(r, r, COL_CONCEPTO, COL_IMPORTE));
    }

    /** Dark section bar ("1. RESUMEN ...") merged across both columns. */
    private int sectionBar(Sheet sheet, Styles st, String texto, int r) {
        Row bar = sheet.createRow(r);
        bar.setHeightInPoints(17f);
        text(bar, COL_CONCEPTO, texto, st.barraSeccion);
        text(bar, COL_IMPORTE, "", st.barraSeccion);
        sheet.addMergedRegion(new CellRangeAddress(r, r, COL_CONCEPTO, COL_IMPORTE));
        return r + 1;
    }

    // ================= tiny cell helpers (keep the layout code readable) ==

    private static void text(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private static void number(Row row, int col, double value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);     // REAL numeric cell (concept #2)
        c.setCellStyle(style);
    }

    private static void formula(Row row, int col, String formulaA1, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellFormula(formulaA1); // no leading '=' in the POI API
        c.setCellStyle(style);
    }

    private static String safe(String v, String fallback) {
        return (v == null || v.trim().isEmpty()) ? fallback : v.trim();
    }

    // =================================================================
    //  All workbook styles, created ONCE per workbook (concept #1).
    //  Public final fields keep call sites short and obvious.
    // =================================================================
    private static final class Styles {

        // -- letterhead --
        final CellStyle titulo;
        final CellStyle empresa;
        final CellStyle meta;
        final CellStyle metaDerecha;

        // -- table chrome --
        final CellStyle barraSeccion;
        final CellStyle cabeceraTabla;
        final CellStyle cabeceraTablaDerecha;

        // -- table body (plain + zebra variants) --
        final CellStyle cuerpoTexto;
        final CellStyle cuerpoTextoZebra;
        final CellStyle cuerpoMoneda;
        final CellStyle cuerpoMonedaZebra;
        final CellStyle cuerpoEntero;
        final CellStyle cuerpoEnteroZebra;
        final CellStyle cuerpoPorcentajeZebra;

        // -- totals --
        final CellStyle totalTexto;
        final CellStyle totalMoneda;
        final CellStyle granTotalTexto;
        final CellStyle granTotalMoneda;

        // -- footer --
        final CellStyle nota;
        final CellStyle pieDePagina;

        Styles(Workbook wb, String simboloMoneda) {

            DataFormat df = wb.createDataFormat();

            // Accounting money format with the user's currency symbol baked
            // in as a LITERAL (quoted) prefix. Negative numbers show red and
            // in parentheses — the accountant's convention:
            //     "S/" #,##0.00;[Red]"S/" (#,##0.00)
            String simbolo = (simboloMoneda == null || simboloMoneda.trim().isEmpty())
                    ? "S/" : simboloMoneda.trim();
            short fmtMoneda = df.getFormat(
                    "\"" + simbolo + "\" #,##0.00;[Red]\"" + simbolo + "\" (#,##0.00)");
            short fmtEntero = df.getFormat("#,##0");
            short fmtPorcentaje = df.getFormat("#,##0.00\"%\"");

            // ---------- fonts (also workbook-scoped, also cached) ----------
            Font fTitulo = wb.createFont();
            fTitulo.setBold(true);
            fTitulo.setFontHeightInPoints((short) 16);
            fTitulo.setColor(IndexedColors.WHITE.getIndex());
            fTitulo.setFontName("Calibri");

            Font fEmpresa = wb.createFont();
            fEmpresa.setBold(true);
            fEmpresa.setFontHeightInPoints((short) 12);
            fEmpresa.setFontName("Calibri");

            Font fMeta = wb.createFont();
            fMeta.setFontHeightInPoints((short) 10);
            fMeta.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            fMeta.setFontName("Calibri");

            Font fBarra = wb.createFont();
            fBarra.setBold(true);
            fBarra.setFontHeightInPoints((short) 11);
            fBarra.setColor(IndexedColors.WHITE.getIndex());
            fBarra.setFontName("Calibri");

            Font fCabecera = wb.createFont();
            fCabecera.setBold(true);
            fCabecera.setFontHeightInPoints((short) 10);
            fCabecera.setFontName("Calibri");

            Font fCuerpo = wb.createFont();
            fCuerpo.setFontHeightInPoints((short) 10);
            fCuerpo.setFontName("Calibri");

            Font fTotal = wb.createFont();
            fTotal.setBold(true);
            fTotal.setFontHeightInPoints((short) 10);
            fTotal.setFontName("Calibri");

            // Double-accounting underline: the traditional "==== final ===="
            // mark under a statement's bottom line.
            Font fGranTotal = wb.createFont();
            fGranTotal.setBold(true);
            fGranTotal.setFontHeightInPoints((short) 11);
            fGranTotal.setUnderline(Font.U_DOUBLE_ACCOUNTING);
            fGranTotal.setFontName("Calibri");

            Font fNota = wb.createFont();
            fNota.setItalic(true);
            fNota.setFontHeightInPoints((short) 9);
            fNota.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            fNota.setFontName("Calibri");

            // ---------- letterhead styles ----------
            titulo = wb.createCellStyle();
            titulo.setFont(fTitulo);
            // A solid fill needs BOTH the color and the SOLID_FOREGROUND
            // pattern — setting only the color silently paints nothing.
            titulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titulo.setAlignment(HorizontalAlignment.CENTER);
            titulo.setVerticalAlignment(VerticalAlignment.CENTER);

            empresa = wb.createCellStyle();
            empresa.setFont(fEmpresa);
            empresa.setAlignment(HorizontalAlignment.CENTER);
            empresa.setVerticalAlignment(VerticalAlignment.CENTER);
            empresa.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            empresa.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            meta = wb.createCellStyle();
            meta.setFont(fMeta);

            metaDerecha = wb.createCellStyle();
            metaDerecha.setFont(fMeta);
            metaDerecha.setAlignment(HorizontalAlignment.RIGHT);

            // ---------- section bar ----------
            barraSeccion = wb.createCellStyle();
            barraSeccion.setFont(fBarra);
            barraSeccion.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
            barraSeccion.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            barraSeccion.setVerticalAlignment(VerticalAlignment.CENTER);
            barraSeccion.setIndention((short) 1);

            // ---------- table header cells ----------
            cabeceraTabla = boxed(wb);
            cabeceraTabla.setFont(fCabecera);
            cabeceraTabla.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cabeceraTabla.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cabeceraTablaDerecha = boxed(wb);
            cabeceraTablaDerecha.setFont(fCabecera);
            cabeceraTablaDerecha.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cabeceraTablaDerecha.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cabeceraTablaDerecha.setAlignment(HorizontalAlignment.RIGHT);

            // ---------- body cells: plain and zebra variants ----------
            cuerpoTexto = boxed(wb);
            cuerpoTexto.setFont(fCuerpo);

            cuerpoTextoZebra = boxed(wb);
            cuerpoTextoZebra.setFont(fCuerpo);
            zebra(cuerpoTextoZebra);

            cuerpoMoneda = boxed(wb);
            cuerpoMoneda.setFont(fCuerpo);
            cuerpoMoneda.setDataFormat(fmtMoneda);
            cuerpoMoneda.setAlignment(HorizontalAlignment.RIGHT);

            cuerpoMonedaZebra = boxed(wb);
            cuerpoMonedaZebra.setFont(fCuerpo);
            cuerpoMonedaZebra.setDataFormat(fmtMoneda);
            cuerpoMonedaZebra.setAlignment(HorizontalAlignment.RIGHT);
            zebra(cuerpoMonedaZebra);

            cuerpoEntero = boxed(wb);
            cuerpoEntero.setFont(fCuerpo);
            cuerpoEntero.setDataFormat(fmtEntero);
            cuerpoEntero.setAlignment(HorizontalAlignment.RIGHT);

            cuerpoEnteroZebra = boxed(wb);
            cuerpoEnteroZebra.setFont(fCuerpo);
            cuerpoEnteroZebra.setDataFormat(fmtEntero);
            cuerpoEnteroZebra.setAlignment(HorizontalAlignment.RIGHT);
            zebra(cuerpoEnteroZebra);

            cuerpoPorcentajeZebra = boxed(wb);
            cuerpoPorcentajeZebra.setFont(fCuerpo);
            cuerpoPorcentajeZebra.setDataFormat(fmtPorcentaje);
            cuerpoPorcentajeZebra.setAlignment(HorizontalAlignment.RIGHT);
            zebra(cuerpoPorcentajeZebra);

            // ---------- totals ----------
            totalTexto = boxed(wb);
            totalTexto.setFont(fTotal);
            totalTexto.setBorderTop(BorderStyle.DOUBLE); // classic double rule
            totalTexto.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalTexto.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            totalMoneda = boxed(wb);
            totalMoneda.setFont(fTotal);
            totalMoneda.setBorderTop(BorderStyle.DOUBLE);
            totalMoneda.setDataFormat(fmtMoneda);
            totalMoneda.setAlignment(HorizontalAlignment.RIGHT);
            totalMoneda.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalMoneda.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            granTotalTexto = boxed(wb);
            granTotalTexto.setFont(fGranTotal);
            granTotalTexto.setBorderTop(BorderStyle.DOUBLE);
            granTotalTexto.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            granTotalTexto.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            granTotalTexto.setVerticalAlignment(VerticalAlignment.CENTER);

            granTotalMoneda = boxed(wb);
            granTotalMoneda.setFont(fGranTotal);
            granTotalMoneda.setBorderTop(BorderStyle.DOUBLE);
            granTotalMoneda.setDataFormat(fmtMoneda);
            granTotalMoneda.setAlignment(HorizontalAlignment.RIGHT);
            granTotalMoneda.setVerticalAlignment(VerticalAlignment.CENTER);
            granTotalMoneda.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            granTotalMoneda.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ---------- footer ----------
            nota = wb.createCellStyle();
            nota.setFont(fNota);
            nota.setWrapText(true);                    // long notes wrap
            nota.setVerticalAlignment(VerticalAlignment.TOP);

            pieDePagina = wb.createCellStyle();
            pieDePagina.setFont(fNota);
        }

        /** New style with a thin grey box on all four sides (table grid). */
        private static CellStyle boxed(Workbook wb) {
            CellStyle s = wb.createCellStyle();
            s.setBorderTop(BorderStyle.THIN);
            s.setBorderBottom(BorderStyle.THIN);
            s.setBorderLeft(BorderStyle.THIN);
            s.setBorderRight(BorderStyle.THIN);
            short grey = IndexedColors.GREY_40_PERCENT.getIndex();
            s.setTopBorderColor(grey);
            s.setBottomBorderColor(grey);
            s.setLeftBorderColor(grey);
            s.setRightBorderColor(grey);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            return s;
        }

        /** Applies the pale zebra fill used on alternate body rows. */
        private static void zebra(CellStyle s) {
            s.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
    }
}
