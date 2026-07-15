package com.tracker.web.valorizacion;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.tracker.web.valorizacion.ValorizacionResult.Linea;

/**
 * =====================================================================
 *  OUTPUT WORKBOOK — replica of the current valorization template
 * =====================================================================
 *
 * The layout mirrors, cell by cell, the sheets "VALORIZACION BOOKRRU" /
 * "VALORIZACION EASY MACRO" of the MODELO workbook (same rows, columns,
 * colors and formulas), so the generated file looks like the ones the team
 * already sends:
 *
 *   column A  narrow margin
 *   B1        yellow disclaimer banner
 *   B3/B4     title "VALORIZACION - EMPRESA" and "PAP:" + id
 *   B6:E9     summary (ITEM / Cantidad / Costo unitario $ / Total $)
 *             with LIVE formulas: D7==L13, D8==L28, E9==SUM(E7:E8)
 *   B11:C22   the "fundamental items" of point 7 (tipo de solución, medio
 *             de Tx, dirección, pisos, área, tecnologías, sectores,
 *             antenas, BBU, farend) + the BBU-location warning
 *   B26:D34   green "Info sólo para RF" block (diseño, sectores 4G,
 *             departamento, coordinates BBU/RRU, proyección)
 *   G6:L13    EQUIPOS table (UNID x COSTO -> SUBTOTAL, K col formulas,
 *             merged L column holding the USD group subtotal)
 *   G16:L28   SERVICIOS table, same mechanics
 *
 * Everything money is written as REAL numbers with a #,##0.00 format and
 * the K/L/E columns are Excel FORMULAS, so the sheet stays alive: an RF
 * engineer can still tweak a quantity by hand and every total follows,
 * exactly like in the original template.
 *
 * Same POI ground rules as the excelreport sample module: styles created
 * once per workbook (inner Styles class), never inside loops.
 */
@Component
public class ValorizacionExcelBuilder {

    // Fixed anchor rows (0-based) — same coordinates as the MODELO sheet
    // (1-based in Excel: disclaimer 1, title 3, summary 6-9, details 11-22,
    //  RF info 26-34, equipos 6-13, servicios 16-28).
    private static final int ROW_DISCLAIMER = 0;
    private static final int ROW_TITULO = 2;
    private static final int ROW_PAP = 3;
    private static final int ROW_RESUMEN_HEADER = 5;
    private static final int ROW_DETALLE_INICIO = 10;
    private static final int ROW_RF_INFO = 25;
    private static final int ROW_EQUIPOS_HEADER = 5;
    private static final int ROW_EQUIPOS_PRIMERA = 6;   // items in rows 7..10 (Excel)
    private static final int ROW_EQUIPOS_TOTAL = 12;    // Excel row 13
    private static final int ROW_SERVICIOS_HEADER = 15; // Excel row 16
    private static final int ROW_SERVICIOS_PRIMERA = 16; // items 17..27 (Excel)
    private static final int ROW_SERVICIOS_TOTAL = 27;  // Excel row 28

    public void build(OutputStream out, ValorizacionResult r) throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet(r.getTipoSolucion().getNombreHoja());
            Styles st = new Styles(wb);

            // Column widths copied from the MODELO sheet (units: 1/256 char).
            int[] anchos = { 4, 33, 13, 26, 18, 5, 18, 49, 6, 18, 12, 15 };
            for (int c = 0; c < anchos.length; c++) {
                sheet.setColumnWidth(c, anchos[c] * 256);
            }

            writeCabecera(sheet, st, r);
            writeResumen(sheet, st);          // left summary (formulas into the tables)
            writeDetalle(sheet, st, r);       // the fundamental items block
            writeInfoRf(sheet, st, r);        // green RF-only block
            writeTabla(sheet, st, "EQUIPOS", r.getEquipos(),
                    ROW_EQUIPOS_HEADER, ROW_EQUIPOS_PRIMERA, ROW_EQUIPOS_TOTAL,
                    "TOTAL EQUIPOS");
            writeTabla(sheet, st, "SERVICIOS", r.getServicios(),
                    ROW_SERVICIOS_HEADER, ROW_SERVICIOS_PRIMERA, ROW_SERVICIOS_TOTAL,
                    "TOTAL SERVICIOS");

            wb.write(out);
        }
    }

    // =================================================================
    //  Banner + title
    // =================================================================
    private void writeCabecera(Sheet sheet, Styles st, ValorizacionResult r) {

        Row banner = row(sheet, ROW_DISCLAIMER);
        banner.setHeightInPoints(22f);
        // Same wording as the current template's yellow strip.
        set(banner, 1, "(*) ESTA VALORIZACIÓN NO INCLUYE LOS COSTOS DE PLANTA EXTERNA",
                st.disclaimer);

        Row titulo = row(sheet, ROW_TITULO);
        set(titulo, 1, "VALORIZACION - " + nz(r.getNombreEmpresa()), st.titulo);

        Row pap = row(sheet, ROW_PAP);
        set(pap, 1, "PAP:", st.titulo);
        if (r.getIdValorizacion() != null) {
            num(pap, 2, r.getIdValorizacion().doubleValue(), st.papId);
        }
    }

    // =================================================================
    //  Left summary B6:E9 — everything is a live formula into the tables
    // =================================================================
    private void writeResumen(Sheet sheet, Styles st) {

        Row h = row(sheet, ROW_RESUMEN_HEADER);
        set(h, 1, "ITEM", st.headerRojo);
        set(h, 2, "Cantidad", st.headerRojo);
        set(h, 3, "Costo unitario $", st.headerRojo);
        set(h, 4, "Total $", st.headerRojo);

        // Excel (1-based) rows of the two group totals in column L.
        int filaTotEquipos = ROW_EQUIPOS_TOTAL + 1;
        int filaTotServicios = ROW_SERVICIOS_TOTAL + 1;

        Row eq = row(sheet, ROW_RESUMEN_HEADER + 1);            // Excel row 7
        set(eq, 1, "EQUIPOS", st.resumenItem);
        num(eq, 2, 1, st.resumenNumero);
        formula(eq, 3, "L" + filaTotEquipos, st.resumenMoneda); // D7 = L13
        formula(eq, 4, "C7*D7", st.resumenMoneda);              // E7

        Row sv = row(sheet, ROW_RESUMEN_HEADER + 2);            // Excel row 8
        set(sv, 1, "SERVICIOS", st.resumenItem);
        num(sv, 2, 1, st.resumenNumero);
        formula(sv, 3, "L" + filaTotServicios, st.resumenMoneda); // D8 = L28
        formula(sv, 4, "C8*D8", st.resumenMoneda);                // E8

        Row tot = row(sheet, ROW_RESUMEN_HEADER + 3);           // Excel row 9
        set(tot, 3, "TOTAL $ (USD)", st.headerRojo);
        formula(tot, 4, "SUM(E7:E8)", st.granTotalRojo);        // E9
    }

    // =================================================================
    //  Fundamental items (agreement point 7) — B11..B22, values C..E merged
    // =================================================================
    private void writeDetalle(Sheet sheet, Styles st, ValorizacionResult r) {

        int f = ROW_DETALLE_INICIO;
        detalle(sheet, st, f++, "* TIPO DE SOLUCION", r.getTipoSolucion().getEtiqueta());
        detalle(sheet, st, f++, "* Medio de Tx", "FO");             // FO SIEMPRE
        detalle(sheet, st, f++, "* Dirección (indicar Distrito): ", nz(r.getDireccion()));
        detalle(sheet, st, f++, "* Cantidad de pisos",
                r.getCantidadPisos() != null ? String.valueOf(r.getCantidadPisos()) : "");
        detalle(sheet, st, f++, "* Pisos a detalle", nz(r.getPisosDetalle()));
        detalle(sheet, st, f++, "* Área Total (m2)",
                r.getAreaTotalM2() != null ? String.valueOf(r.getAreaTotalM2().intValue()) : "");
        detalle(sheet, st, f++, "*Tecnologías y frecuencias",
                r.getTipoSolucion().getTecnologias());
        detalle(sheet, st, f++, "*Cantidad de Sectores",
                r.getCantidadSectores() != null ? String.valueOf(r.getCantidadSectores()) : "");
        detalle(sheet, st, f++, "*Cantidad de Antenas", nz(r.getAntenasDescripcion()));
        detalle(sheet, st, f++, "*Cantidad de BBU", "No aplica");
        detalle(sheet, st, f++, "*Site de donde se extiende la RRU", nz(r.getFarend()));

        // The standing warning of the current template, verbatim.
        Row warn = row(sheet, f);
        warn.setHeightInPoints(24f);
        set(warn, 1, "*SE DEBE VERIFICAR LA UBICACIÓN FÍSICA DE LA BBU, DEBIDO A QUE EL "
                + "RECORRIDO DE FIBRA DEBE SER DE LA BBU A LA UBICACIÓN DEL CLIENTE.",
                st.advertencia);
    }

    private void detalle(Sheet sheet, Styles st, int fila, String etiqueta, String valor) {
        Row row = row(sheet, fila);
        set(row, 1, etiqueta, st.etiqueta);
        set(row, 2, valor, st.valor);
        set(row, 3, "", st.valor);
        set(row, 4, "", st.valor);
        // Values span C..E in the original sheet.
        sheet.addMergedRegion(new CellRangeAddress(fila, fila, 2, 4));
    }

    // =================================================================
    //  Green "Info sólo para RF" block — B26..D34
    // =================================================================
    private void writeInfoRf(Sheet sheet, Styles st, ValorizacionResult r) {

        int f = ROW_RF_INFO;
        set(row(sheet, f), 1, "* Info sólo para RF:", st.rfHeaderVerde);
        f += 2;                                                   // blank row 27

        Row disenio = row(sheet, f++);                            // Excel row 28
        set(disenio, 1, "* Tipo de diseño", st.etiqueta);
        set(disenio, 2, r.getTipoSolucion().getEtiqueta(), st.valor);

        Row sectores = row(sheet, f++);                           // Excel row 29
        set(sectores, 1, "* Cantidad de Sectores 4G", st.etiqueta);
        if (r.getCantidadSectores() != null) {
            num(sectores, 2, r.getCantidadSectores(), st.valorNumero);
        }
        f++;                                                      // blank row 30

        Row depto = row(sheet, f++);                              // Excel row 31
        set(depto, 1, "* Departamento", st.etiqueta);
        set(depto, 2, nz(r.getDepartamento()), st.valor);

        // BBU = the FAREND site; RRU ext = the client (PAP coordinates).
        Row bbu = row(sheet, f++);                                // Excel row 32
        set(bbu, 1, "* Base Band (Lat / Lon) BBU", st.etiqueta);
        coord(bbu, 2, r.getBbuLatitud(), st);
        coord(bbu, 3, r.getBbuLongitud(), st);

        Row rru = row(sheet, f++);                                // Excel row 33
        set(rru, 1, "* RRU ext (Lat / Lon)", st.etiqueta);
        coord(rru, 2, r.getRruLatitud(), st);
        coord(rru, 3, r.getRruLongitud(), st);

        Row proy = row(sheet, f);                                 // Excel row 34
        set(proy, 1, "Se proyecta implementar:", st.etiqueta);
        set(proy, 2, nz(r.getProyeccion()), st.valor);
    }

    private void coord(Row row, int col, Double valor, Styles st) {
        if (valor != null) {
            num(row, col, valor.doubleValue(), st.coordenada);
        }
    }

    // =================================================================
    //  EQUIPOS / SERVICIOS tables — G..L, live formulas
    // =================================================================
    private void writeTabla(Sheet sheet, Styles st, String grupo, java.util.List<Linea> lineas,
                            int filaHeader, int filaPrimera, int filaTotal, String tituloTotal) {

        Row h = row(sheet, filaHeader);
        set(h, 6, grupo, st.headerRojo);
        set(h, 7, "DESCRIPCIÓN", st.headerRojo);
        set(h, 8, "UNID", st.headerRojo);
        set(h, 9, "COSTO X\nUNIDAD", st.headerRojo);
        set(h, 10, "SUBTOTAL", st.headerRojo);
        set(h, 11, "SUBTOTAL (USD)", st.headerRojo);

        int fila = filaPrimera;
        for (Linea l : lineas) {
            Row row = row(sheet, fila);
            if (l.getDescripcion().indexOf('\n') >= 0) {
                row.setHeightInPoints(28f);       // two-line descriptions
            }
            set(row, 7, l.getDescripcion(), st.itemDescripcion);
            num(row, 8, l.getUnidades(), st.itemUnid);
            num(row, 9, l.getCostoUnitarioUsd().doubleValue(), st.itemMoneda);
            // SUBTOTAL = UNID * COSTO, as a live formula (K = I * J).
            int excelRow = fila + 1;
            formula(row, 10, "I" + excelRow + "*J" + excelRow, st.itemMoneda);
            fila++;
        }

        // Merged L column with the group's USD subtotal (like the original,
        // where L7:L12 holds =SUM(K7:K11)). Sum range = the item rows.
        if (fila == filaPrimera) {
            // No lines (should not happen for INDOOR/OUTDOOR): keep the
            // sheet valid with a plain zero instead of a broken SUM range.
            num(row(sheet, filaPrimera), 11, 0, st.grupoUsd);
            Row totVacio = row(sheet, filaTotal);
            set(totVacio, 6, tituloTotal, st.totalGris);
            set(totVacio, 10, "USD", st.totalGris);
            num(totVacio, 11, 0, st.totalGrisMoneda);
            return;
        }
        String rango = "K" + (filaPrimera + 1) + ":K" + fila;
        Row primera = sheet.getRow(filaPrimera);
        formula(primera, 11, "SUM(" + rango + ")", st.grupoUsd);
        for (int fx = filaPrimera + 1; fx < fila; fx++) {
            set(sheet.getRow(fx), 11, "", st.grupoUsd);   // style the merge body
        }
        if (fila - filaPrimera > 1) {
            sheet.addMergedRegion(new CellRangeAddress(filaPrimera, fila - 1, 11, 11));
        }

        // TOTAL row (G13 / G28 in the original).
        Row tot = row(sheet, filaTotal);
        set(tot, 6, tituloTotal, st.totalGris);
        set(tot, 7, "", st.totalGris);
        set(tot, 8, "", st.totalGris);
        set(tot, 9, "", st.totalGris);
        set(tot, 10, "USD", st.totalGris);
        formula(tot, 11, "SUM(" + rango + ")", st.totalGrisMoneda);
    }

    // ================= helpers =================

    /** getRow-or-create: several blocks write into the same physical rows. */
    private static Row row(Sheet sheet, int idx) {
        Row r = sheet.getRow(idx);
        return (r != null) ? r : sheet.createRow(idx);
    }

    private static void set(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private static void num(Row row, int col, double value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private static void formula(Row row, int col, String f, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellFormula(f);
        c.setCellStyle(style);
    }

    private static String nz(String v) { return v != null ? v : ""; }

    // =================================================================
    //  Styles — mirroring the MODELO's palette: yellow disclaimer,
    //  red table headers, red grand total, green RF block.
    // =================================================================
    private static final class Styles {

        final CellStyle disclaimer;
        final CellStyle titulo;
        final CellStyle papId;
        final CellStyle headerRojo;
        final CellStyle resumenItem;
        final CellStyle resumenNumero;
        final CellStyle resumenMoneda;
        final CellStyle granTotalRojo;
        final CellStyle etiqueta;
        final CellStyle valor;
        final CellStyle valorNumero;
        final CellStyle advertencia;
        final CellStyle rfHeaderVerde;
        final CellStyle coordenada;
        final CellStyle itemDescripcion;
        final CellStyle itemUnid;
        final CellStyle itemMoneda;
        final CellStyle grupoUsd;
        final CellStyle totalGris;
        final CellStyle totalGrisMoneda;

        Styles(Workbook wb) {

            DataFormat df = wb.createDataFormat();
            short fmtMoneda = df.getFormat("#,##0.00");
            short fmtEntero = df.getFormat("#,##0");
            short fmtCoord = df.getFormat("0.000000");

            Font bold16 = font(wb, 16, true);
            Font bold12 = font(wb, 12, true);
            Font bold11 = font(wb, 11, true);
            Font bold10 = font(wb, 10, true);
            Font normal10 = font(wb, 10, false);

            disclaimer = wb.createCellStyle();
            disclaimer.setFont(bold16);
            disclaimer.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            disclaimer.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            disclaimer.setVerticalAlignment(VerticalAlignment.CENTER);

            titulo = wb.createCellStyle();
            titulo.setFont(bold12);

            papId = wb.createCellStyle();
            papId.setFont(bold12);
            papId.setDataFormat(df.getFormat("0"));   // the id, no thousands sep

            headerRojo = boxed(wb);
            headerRojo.setFont(bold12);
            headerRojo.setFillForegroundColor(IndexedColors.RED.getIndex());
            headerRojo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerRojo.setAlignment(HorizontalAlignment.CENTER);
            headerRojo.setWrapText(true);             // "COSTO X\nUNIDAD"

            resumenItem = boxed(wb);
            resumenItem.setFont(bold11);

            resumenNumero = boxed(wb);
            resumenNumero.setFont(normal10);
            resumenNumero.setDataFormat(fmtEntero);
            resumenNumero.setAlignment(HorizontalAlignment.CENTER);

            resumenMoneda = boxed(wb);
            resumenMoneda.setFont(normal10);
            resumenMoneda.setDataFormat(fmtMoneda);
            resumenMoneda.setAlignment(HorizontalAlignment.RIGHT);

            granTotalRojo = boxed(wb);
            granTotalRojo.setFont(bold12);
            granTotalRojo.setDataFormat(fmtMoneda);
            granTotalRojo.setAlignment(HorizontalAlignment.RIGHT);
            granTotalRojo.setFillForegroundColor(IndexedColors.RED.getIndex());
            granTotalRojo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            etiqueta = wb.createCellStyle();
            etiqueta.setFont(bold10);
            etiqueta.setVerticalAlignment(VerticalAlignment.CENTER);

            valor = boxed(wb);
            valor.setFont(bold10);
            valor.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            valor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            valorNumero = boxed(wb);
            valorNumero.setFont(bold10);
            valorNumero.setDataFormat(fmtEntero);
            valorNumero.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            valorNumero.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            advertencia = wb.createCellStyle();
            advertencia.setFont(bold10);
            advertencia.setWrapText(true);
            advertencia.setVerticalAlignment(VerticalAlignment.TOP);

            rfHeaderVerde = wb.createCellStyle();
            rfHeaderVerde.setFont(bold10);
            rfHeaderVerde.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            rfHeaderVerde.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            coordenada = wb.createCellStyle();
            coordenada.setFont(normal10);
            coordenada.setDataFormat(fmtCoord);

            itemDescripcion = boxed(wb);
            itemDescripcion.setFont(normal10);
            itemDescripcion.setWrapText(true);

            itemUnid = boxed(wb);
            itemUnid.setFont(normal10);
            itemUnid.setDataFormat(fmtEntero);
            itemUnid.setAlignment(HorizontalAlignment.CENTER);

            itemMoneda = boxed(wb);
            itemMoneda.setFont(normal10);
            itemMoneda.setDataFormat(fmtMoneda);
            itemMoneda.setAlignment(HorizontalAlignment.RIGHT);

            grupoUsd = boxed(wb);
            grupoUsd.setFont(bold11);
            grupoUsd.setDataFormat(fmtMoneda);
            grupoUsd.setAlignment(HorizontalAlignment.RIGHT);
            grupoUsd.setVerticalAlignment(VerticalAlignment.CENTER);

            totalGris = boxed(wb);
            totalGris.setFont(bold10);
            totalGris.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalGris.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            totalGrisMoneda = boxed(wb);
            totalGrisMoneda.setFont(bold11);
            totalGrisMoneda.setDataFormat(fmtMoneda);
            totalGrisMoneda.setAlignment(HorizontalAlignment.RIGHT);
            totalGrisMoneda.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalGrisMoneda.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // NOTE ON COLORS: IndexedColors.RED / YELLOW / LIGHT_GREEN are the
            // closest built-in matches to the template's FF0000 / FFFF00 /
            // 92D050. For pixel-exact RGB, cast styles to XSSFCellStyle and
            // use setFillForegroundColor(new XSSFColor(...)) — xlsx only.
        }

        private static Font font(Workbook wb, int size, boolean bold) {
            Font f = wb.createFont();
            f.setFontHeightInPoints((short) size);
            f.setBold(bold);
            f.setFontName("Calibri");
            return f;
        }

        private static CellStyle boxed(Workbook wb) {
            CellStyle s = wb.createCellStyle();
            s.setBorderTop(BorderStyle.THIN);
            s.setBorderBottom(BorderStyle.THIN);
            s.setBorderLeft(BorderStyle.THIN);
            s.setBorderRight(BorderStyle.THIN);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            return s;
        }
    }
}
