package com.tracker.web.inventorysummary;

import com.tracker.web.inventorysummary.InventorySummaryService2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory")
public class InventorySummaryController2 {

    private final InventorySummaryService2 service;

    public InventorySummaryController2(InventorySummaryService2 service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public String summary(
            @RequestParam(defaultValue = "4") Integer currentWeek,
            @RequestParam(defaultValue = "3") Integer alarmWeek,
            Model model
    ) {

        model.addAttribute(
                "summaries",
                service.buildSummary(currentWeek, alarmWeek)
        );

        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("alarmWeek", alarmWeek);

        return "inventory/inventario_30_01";
    }
}
