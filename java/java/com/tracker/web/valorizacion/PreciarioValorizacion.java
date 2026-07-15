package com.tracker.web.valorizacion;

import java.math.BigDecimal;

/**
 * =====================================================================
 *  PRECIARIO — single source of truth for every unit price (USD)
 * =====================================================================
 *
 * All values were taken verbatim from the workbook
 * "MODELO - PROCESO Y PLANTILLA VALORIZACIONES" (tabs VALORIZACION BOOKRRU,
 * VALORIZACION EASY MACRO and PLANOS). When finance updates the price list,
 * THIS is the only file to touch. If prices later move to a database table,
 * replace these constants with a lookup service — the calculator and the
 * Excel builder only consume BigDecimals, so nothing else changes.
 *
 * NOTE ON CURRENCY: in the source workbook the "COSTO X UNIDAD" columns are
 * already USD (e.g. BOOKRRU 1440 + material 225.37 + 2 celdas x 327.50 =
 * 2,320.37 shown as "SUBTOTAL (USD)"). Everything here is USD.
 */
public final class PreciarioValorizacion {

    private PreciarioValorizacion() { /* constants only, no instances */ }

    // =================================================================
    //  EQUIPOS — INDOOR (per BOOK RRU: 1 install material, 2 celdas)
    // =================================================================
    public static final String DESC_BOOKRRU = "BOOKRRU5336E (RRU+ANTENA)";
    public static final BigDecimal PRECIO_BOOKRRU = new BigDecimal("1440.00");

    public static final String DESC_BOOKRRU_MATERIAL =
            "BooKRRU Installation Material per BookRRU\n(2 TRANSCEIVER BiDi 34060544-004 10G 10K HW)";
    public static final BigDecimal PRECIO_BOOKRRU_MATERIAL = new BigDecimal("225.37");

    /** Cells (celdas) each BOOK RRU lights up. */
    public static final int CELDAS_POR_BOOKRRU = 2;

    // =================================================================
    //  EQUIPOS — OUTDOOR (per Easy Macro: 1 install material, 3 celdas)
    // =================================================================
    public static final String DESC_EASYMACRO = "EASY MACRO (AAU5942 PCS +B7)";
    public static final BigDecimal PRECIO_EASYMACRO = new BigDecimal("4290.56");

    public static final String DESC_EASYMACRO_MATERIAL =
            "EasyMacro AAU Installation Material per AAU\n(TRANSCEIVER BiDi 34060544-004 10G 10K HW)";
    public static final BigDecimal PRECIO_EASYMACRO_MATERIAL = new BigDecimal("225.37");

    /** Cells (celdas) each Easy Macro lights up. */
    public static final int CELDAS_POR_EASYMACRO = 3;

    // =================================================================
    //  EQUIPOS — shared items (both solution types)
    // =================================================================
    /** Always priced, always 0 units in the current model ("generalmente no aplica"). */
    public static final String DESC_UBBP = "UBBPg2 - LTE- Initial 240Mhz AAB";
    public static final BigDecimal PRECIO_UBBP = new BigDecimal("1310.00");

    public static final String DESC_CELDA = "Costo promedio por celda (UBBPg2 4G + AAB)";
    public static final BigDecimal PRECIO_CELDA = new BigDecimal("327.50");

    // =================================================================
    //  SERVICIOS — INDOOR
    // =================================================================
    public static final String DESC_PLANOS =
            "Elaboración de planos en autocad (PARA DISEÑO INDOOR)";

    public static final String DESC_IMPLEMENTACION_BOOKRRU =
            "Implementacion BOOKRRU (Incluye energia)";
    public static final BigDecimal PRECIO_IMPLEMENTACION_BOOKRRU = new BigDecimal("434.12");

    /** "se considera como un único item por ahora" — 1 per valorization. */
    public static final String DESC_TRANSPORTE =
            "Transporte e instalaciones elécricas adicionales";
    public static final BigDecimal PRECIO_TRANSPORTE = new BigDecimal("400.00");

    // =================================================================
    //  SERVICIOS — OUTDOOR (per Easy Macro, except SAQ which is per poste)
    // =================================================================
    public static final String DESC_IMPLEMENTACION_TI = "Servicio Implementación (TI)";
    public static final BigDecimal PRECIO_IMPLEMENTACION_TI = new BigDecimal("3333.33");

    public static final String DESC_ENERGIA = "Energia";
    public static final BigDecimal PRECIO_ENERGIA = new BigDecimal("2666.67");

    public static final String DESC_INTEGRACION_4G = "Integración 4G";
    public static final BigDecimal PRECIO_INTEGRACION_4G = new BigDecimal("973.46");

    /** Per poste — "suele ser 1". */
    public static final String DESC_SAQ = "Gestion Licencias (SAQ)";
    public static final BigDecimal PRECIO_SAQ = new BigDecimal("2400.00");

    // =================================================================
    //  PRECIARIO ELABORACION PLANOS EN AUTOCAD (tab "PLANOS")
    //  USD values of the "MAS 20% PARA VALORIZAR" row — that is the row
    //  used in the valorization ("Estos precios están incluidos en el
    //  excel adjuntado": 428.17 appears in the BOOKRRU tab).
    // =================================================================
    private static final double[] PLANOS_LIMITE_M2 = { 600, 1200, 5000, 10000, 25000, 50000 };
    private static final BigDecimal[] PLANOS_PRECIO_USD = {
            new BigDecimal("428.17"),   // area < 600
            new BigDecimal("466.59"),   // 600  <= area < 1200
            new BigDecimal("505.01"),   // 1200 <= area < 5000
            new BigDecimal("543.43"),   // 5000 <= area < 10000
            new BigDecimal("581.86"),   // 10000 <= area < 25000
            new BigDecimal("620.28"),   // 25000 <= area < 50000
    };

    /**
     * Price of the AutoCAD plan service for a given total area, per the
     * PLANOS bracket table. Areas beyond the last bracket fall into the
     * biggest one (the source table simply ends at 50,000 m2).
     */
    public static BigDecimal precioPlanosPorArea(double areaM2) {
        for (int i = 0; i < PLANOS_LIMITE_M2.length; i++) {
            if (areaM2 < PLANOS_LIMITE_M2[i]) {
                return PLANOS_PRECIO_USD[i];
            }
        }
        return PLANOS_PRECIO_USD[PLANOS_PRECIO_USD.length - 1];
    }

    // =================================================================
    //  DIMENSIONING RULES
    // =================================================================
    /**
     * Square meters covered by one BOOK RRU, used to derive
     * "#ANTENAS = ceil(area / M2_POR_BOOKRRU)".
     *
     * CAREFUL: the agreement mentions BOTH 300 m2 (point 5, "la división
     * entre 300m2 que cubre cada bookrru") AND 250 m2 (point 7, the output
     * formula "AREA TOTAL/250, redondear al número siguiente"). We follow
     * the OUTPUT formula (250) because that is what the deliverable Excel
     * must show — flip this constant to 300 if the other reading wins.
     */
    public static final double M2_POR_BOOKRRU = 250.0;

    /** ceil(area / M2_POR_BOOKRRU), minimum 1 — the "#ANTENAS" rule. */
    public static int antenasIndoorPorArea(double areaM2) {
        int n = (int) Math.ceil(areaM2 / M2_POR_BOOKRRU);
        return Math.max(1, n);
    }
}
