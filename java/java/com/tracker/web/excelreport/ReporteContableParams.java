package com.tracker.web.excelreport;

import java.math.BigDecimal;

/**
 * Form-backing bean for the sample accounting-style Excel report.
 *
 * Every field here is editable from the small HTML page
 * (templates/excelreport/reporte_contable_form.html) BEFORE the Excel file
 * is generated. Spring MVC binds the POSTed form fields to this object by
 * name (see ReporteContableController#download using @ModelAttribute), so
 * adding a new editable field is just:
 *
 *   1. add a field + getter/setter here,
 *   2. add an <input name="..."> in the HTML form,
 *   3. consume it inside AccountantExcelBuilder.
 *
 * Defaults are set inline so the GET form arrives pre-filled and the report
 * can be generated with zero typing.
 */
public class ReporteContableParams {

    /** Company name printed in the report letterhead (row 2 of the sheet). */
    private String empresa = "WHATEVER 360 S.A.C.";

    /** Big title of the report (row 1 of the sheet, dark banner). */
    private String titulo = "ESTADO DE INVENTARIO VALORIZADO";

    /**
     * Week to report. This is the SAME filter the existing
     * inventorysummary module uses (InventarioInmovilRepository2.findBySemana),
     * so the Excel shows exactly the data of that module.
     */
    private Integer semana = 4;

    /** "Prepared by" line — classic requirement of accounting reports. */
    private String elaboradoPor = "Contabilidad / Planificacion";

    /**
     * Currency symbol used in every money cell ("S/" or "US$").
     * It is embedded into the Excel number format itself, e.g.
     *   "S/" #,##0.00;[Red]"S/" (#,##0.00)
     * so Excel treats the cell as a real number (sortable, summable)
     * while still displaying the currency.
     */
    private String moneda = "S/";

    /**
     * Reference unit cost used to "valorize" the stock:
     *   importe = stock (units) * costoUnitario.
     * The entity InventarioInmovil2 only stores units, so this editable
     * factor is what turns an operational count into an accounting figure.
     * BigDecimal because it is money: never use double for money math.
     */
    private BigDecimal costoUnitario = new BigDecimal("1.00");

    /** Free-text note printed at the bottom of the sheet (optional). */
    private String notas = "";

    // ===== getters / setters (required for Spring form binding) =====
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getSemana() { return semana; }
    public void setSemana(Integer semana) { this.semana = semana; }

    public String getElaboradoPor() { return elaboradoPor; }
    public void setElaboradoPor(String elaboradoPor) { this.elaboradoPor = elaboradoPor; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
