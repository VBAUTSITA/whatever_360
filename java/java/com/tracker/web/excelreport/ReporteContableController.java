package com.tracker.web.excelreport;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple controller wiring the whole sample together:
 *
 *   GET  /excelreport/contable
 *        Shows the small HTML form (Thymeleaf view
 *        templates/excelreport/reporte_contable_form.html) pre-filled with
 *        default values. The user edits title, company, week, currency,
 *        unit cost, author and notes BEFORE the file is generated.
 *
 *   POST /excelreport/contable/download
 *        Binds the form into ReporteContableParams, loads the data through
 *        ReporteContableService (which reuses the EXISTING
 *        inventorysummary entity/repository/service) and streams the
 *        generated .xlsx straight to the browser as a download — same
 *        streaming pattern the baseline module already uses for CSV
 *        (see Baselinedata5g4gController), only the content type changes.
 *
 * Security note: this app's WebSecurityConfig ends with
 * anyRequest().authenticated(), so both URLs automatically require a
 * logged-in user — no security changes are needed for this module.
 */
@Controller
@RequestMapping("/excelreport")
public class ReporteContableController {

    private final ReporteContableService service;
    private final AccountantExcelBuilder builder;

    public ReporteContableController(ReporteContableService service,
                                     AccountantExcelBuilder builder) {
        this.service = service;
        this.builder = builder;
    }

    /** Step 1: the editable form. */
    @GetMapping("/contable")
    public String form(Model model) {
        // A fresh params object carries all the default values; the template
        // simply prints them into the inputs.
        model.addAttribute("params", new ReporteContableParams());
        return "excelreport/reporte_contable_form";
    }

    /** Step 2: generate and stream the workbook. */
    @PostMapping("/contable/download")
    public void download(@ModelAttribute ReporteContableParams params,
                         HttpServletResponse response) throws IOException {

        // 1) Data: existing entity -> aggregated + valorized DTO.
        ReporteContableData data = service.buildData(params.getSemana(), params.getCostoUnitario());

        // 2) HTTP headers BEFORE writing any byte to the stream.
        //    This MIME type is the official one for .xlsx files.
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        int week = params.getSemana() != null ? params.getSemana().intValue() : 0;
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte_contable_semana_" + week + ".xlsx\"");

        // 3) Fixed-template workbook rendered directly onto the response
        //    stream — no temp file on disk.
        builder.build(response.getOutputStream(), params, data);
    }
}
