package com.tracker.web.auditoriared.dashboard;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.tracker.web.auditoriared.SeguridadAuditoria;

@Controller
@RequestMapping("/auditoria-red/dashboard")
public class DashboardAuditoriaController {
    private final DashboardAuditoriaService service;
    public DashboardAuditoriaController(DashboardAuditoriaService service) { this.service = service; }
    @GetMapping
    public String pagina(Model model) {
        SeguridadAuditoria.exigirLectura();
        model.addAttribute("radioMaxKm", service.getAuditoria().getRadioMaxKm());
        model.addAttribute("radioReusoKm", service.getAuditoria().getRadioReusoKm());
        return "auditoriared/dashboard";
    }
    @GetMapping("/api/filtros") @ResponseBody
    public List<Map<String, Object>> filtros() { SeguridadAuditoria.exigirLectura(); return service.definiciones(); }
    @PostMapping("/api/consultar") @ResponseBody
    public Map<String, Object> consultar(@RequestBody SolicitudDashboard solicitud) {
        SeguridadAuditoria.exigirLectura(); return service.consultar(solicitud);
    }
}
