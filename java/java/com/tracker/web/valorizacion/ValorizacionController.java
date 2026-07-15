package com.tracker.web.valorizacion;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * HTTP surface of the valorization platform:
 *
 *   GET  /valorizacion/nueva
 *        The RF user's form. Solution type is a DROPDOWN (never free
 *        text, per the agreement), PAP data auto-fills via the endpoint
 *        below, and only id + area are truly required.
 *
 *   GET  /valorizacion/api/pap/{id}      (JSON)
 *        Auto-fill: called by the page the moment the user types the ID —
 *        "al ingresar manualmente el ID de valorización, debería jalar el
 *        nombre de la empresa y la dirección, así como las coordenadas"
 *        plus the derived nearest FAREND. The PAP stays a black box: we
 *        just relay the fields it exposes.
 *
 *   POST /valorizacion/generar
 *        Computes the valorization, SAVES the snapshot into the FORM_
 *        table of the selected type, and streams the Excel replica of the
 *        current template. On a bad id it returns to the form with the
 *        error message instead.
 *
 * Security: covered by WebSecurityConfig's anyRequest().authenticated().
 */
@Controller
@RequestMapping("/valorizacion")
public class ValorizacionController {

    private final ValorizacionService service;
    private final ValorizacionExcelBuilder builder;

    public ValorizacionController(ValorizacionService service,
                                  ValorizacionExcelBuilder builder) {
        this.service = service;
        this.builder = builder;
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("input", new ValorizacionFormInput());
        return "valorizacion/valorizacion_form";
    }

    /** JSON for the form's auto-fill; {"encontrado":false} when unknown. */
    @GetMapping("/api/pap/{id}")
    @ResponseBody
    public Map<String, Object> pap(@PathVariable("id") Long id) {
        return service.buscarPap(id);
    }

    @PostMapping("/generar")
    public String generar(@ModelAttribute ValorizacionFormInput input,
                          Model model,
                          HttpServletResponse response) throws IOException {

        ValorizacionResult resultado;
        try {
            resultado = service.calcular(input);
        } catch (IllegalArgumentException e) {
            // Unknown PAP id or not-yet-enabled combined type: back to the
            // form with the message, keeping what the user already typed.
            model.addAttribute("input", input);
            model.addAttribute("errorValorizacion", e.getMessage());
            return "valorizacion/valorizacion_form";
        }

        // Snapshot FIRST (the record must exist even if the user cancels
        // the download), then stream the workbook.
        service.registrar(input, resultado);

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"VALORIZACION_PAP_" + resultado.getIdValorizacion()
                        + "_" + input.getTipoSolucion().name() + ".xlsx\"");
        builder.build(response.getOutputStream(), resultado);
        return null; // response already written — no view to render
    }
}
