package com.tracker.web.dashboard.kpi.r01;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashKpiViewController {

    @GetMapping("/dashboard/kpi4g")
    public String showKpi4gDashboard() {
        return "dash_kpi/dash_kpi_4g"; // Thymeleaf buscará en templates/dash_kpi/dash_kpi_4g.html
    }
}
