package com.tracker.web.valorizacion;

/**
 * The three solution types agreed for the valorization platform. Keeping
 * them as an enum (instead of free text) is what lets the UI render them as
 * a dropdown / single-choice control, "para minimizar errores" as agreed.
 *
 * Each value carries the business facts tied to the type:
 *  - etiqueta       -> what the Excel prints in "* TIPO DE SOLUCION"
 *  - tecnologias    -> fixed per type (agreement point 3)
 *  - nombreHoja     -> the price tab name in the MODELO workbook this
 *                      output mirrors (agreement point 2)
 */
public enum TipoSolucion {

    /** Solución BOOKRRU — pestaña "VALORIZACION BOOKRRU". */
    INDOOR("INDOOR", "4G (2600)", "VALORIZACION BOOKRRU"),

    /** Solución POSTE/NAT (Easy Macro) — pestaña "VALORIZACION EASY MACRO". */
    OUTDOOR("OUTDOOR", "4G (1900/2600)", "VALORIZACION EASY MACRO"),

    /**
     * Solución POSTE/NAT + BOOKRRU — "TEMPLATE COMBINADO".
     * Phase 2: the entity/table exists (FORM_VALORIZACION_OUTDOOR_PLUSINDOOR)
     * but the Excel builder for the combined workbook is not wired yet —
     * only INDOOR and OUTDOOR were requested first.
     */
    INDOOR_MAS_OUTDOOR("INDOOR + OUTDOOR", "4G (1900/2600)", "VALORIZACION COMBINADA");

    private final String etiqueta;
    private final String tecnologias;
    private final String nombreHoja;

    TipoSolucion(String etiqueta, String tecnologias, String nombreHoja) {
        this.etiqueta = etiqueta;
        this.tecnologias = tecnologias;
        this.nombreHoja = nombreHoja;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getTecnologias() { return tecnologias; }
    public String getNombreHoja() { return nombreHoja; }
}
