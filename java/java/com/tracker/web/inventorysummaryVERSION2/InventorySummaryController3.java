package com.tracker.web.inventorysummaryVERSION2;

import com.tracker.web.inventorysummaryVERSION2.InventorySummaryService3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/inventory")
public class InventorySummaryController3 {

    private final InventorySummaryService3 service;

    public InventorySummaryController3(InventorySummaryService3 service) {
        this.service = service;
    }

    /*
    @GetMapping("/summary_main")
    public String summary(
            @RequestParam(defaultValue = "4") Integer currentWeek,
            Model model
    ) {
        model.addAttribute("summaries", service.buildMainSummary(currentWeek));
        model.addAttribute("categoriesByMain", service.buildCategoryTree(currentWeek));
        model.addAttribute("currentWeek", currentWeek);

        return "inventory/main_categoria";
    }

    @GetMapping("/summary_main_2")
    public String summary2(
            @RequestParam(defaultValue = "4") Integer currentWeek,
            Model model
    ) {
        model.addAttribute("summaries", service.buildMainSummary(currentWeek));
        model.addAttribute("categoriesByMain", service.buildCategoryTree(currentWeek));
        model.addAttribute("currentWeek", currentWeek);

        return "inventory/inventario_06_02";
    }
    */
    
    @GetMapping("/summary_main_3")
    public String summary3(
            @RequestParam(defaultValue = "4") Integer currentWeek,
            @RequestParam(required = false) List<Integer> rotacion,
            Model model
    ) {

        if (rotacion == null || rotacion.isEmpty()) {
            rotacion = Collections.singletonList(0);
        }

        model.addAttribute("summaries",
                service.buildMainSummary(currentWeek, rotacion));

        model.addAttribute("categoriesByMain",
                service.buildCategoryTree(currentWeek, rotacion));

        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("rotacion", rotacion);

        int estancadoCount =
                service.obtenerInventarioEstancadoCount(currentWeek);

        model.addAttribute("inventarioEstancadoCount", estancadoCount);
        
        int sinStockCount =
                service.obtenerSinStockProlongadoCount(currentWeek);

        model.addAttribute("sinStockProlongadoCount", sinStockCount);

        int tendenciaNegativa =
                service.obtenerTendenciaNegativaCount(currentWeek);

        model.addAttribute("tendenciaNegativaCount", tendenciaNegativa);
        List<Object[]> top5 =
                service.obtenerTop5MayorConsumo(currentWeek);

        model.addAttribute("top5Consumo", top5);
        
        model.addAttribute("inventarioEstancadoList",
                service.obtenerInventarioEstancado(currentWeek));

        model.addAttribute("sinStockList",
                service.obtenerSinStockProlongado(currentWeek));

        model.addAttribute("tendenciaList",
                service.obtenerTendenciaNegativa(currentWeek));

        
        return "inventory/prueba_svg";
    }
    
    // === NUEVO: endpoint JSON para el pop-up ===
    @GetMapping("/details")
    @ResponseBody
    public List<ItemDetalleDTO3> obtenerDetalles(
            @RequestParam Integer semana,
            @RequestParam List<Integer> rotacion,
            @RequestParam("mainCategory") String mainCategory
    ) {
        return service.obtenerDetalleMainCategoria(semana, rotacion, mainCategory);
    }
    
    @GetMapping("/detalle_main")
    public String detalleMainCategory(
            @RequestParam Integer semana,
            @RequestParam List<Integer> rotacion,
            @RequestParam String mainCategory,
            Model model
    ) {
        // usarás este método del service (luego ajustamos si quieres)
        List<InventarioInmovil3> items =
                service.getItemsByMainCategory(semana, rotacion, mainCategory);

        model.addAttribute("items", items);
        model.addAttribute("mainCategory", mainCategory);

        return "inventory/detalle_main"; // nueva vista
    }
    
    
    @GetMapping("/detalle_indicador")
    public String detalleIndicador(
            @RequestParam Integer semana,
            @RequestParam String tipo,
            Model model
    ) {

        List<InventarioInmovil3> items = new ArrayList<>();

        switch (tipo) {
            case "estancado":
                items = service.obtenerInventarioEstancado(semana);
                break;

            case "sinstock":
                items = service.obtenerSinStockProlongado(semana);
                break;

            case "tendencia":
                items = service.obtenerTendenciaNegativa(semana);
                break;
        }

        model.addAttribute("items", items);
        model.addAttribute("tipo", tipo);

        return "inventory/detalle_indicador";
    }
    
}

    
    /*@GetMapping("/summary_main_3")
    public String summary3(
            @RequestParam(defaultValue = "4") Integer currentWeek,
            @RequestParam(required = false) List<Integer> rotacion,
            Model model
    ) {
        if (rotacion == null || rotacion.isEmpty()) {
            rotacion = Collections.singletonList(0); // default
        }

        model.addAttribute("summaries", service.buildMainSummary(currentWeek, rotacion));
        model.addAttribute("categoriesByMain", service.buildCategoryTree(currentWeek, rotacion));
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("rotacion", rotacion);

        return "inventory/prueba_svg";
    }*/
    



/*@Controller
@RequestMapping("/inventory")
public class InventorySummaryController3 {

    private final InventorySummaryService3 service;

    public InventorySummaryController3(InventorySummaryService3 service) {
        this.service = service;
    }

    @GetMapping("/summary_main")
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

        return "inventory/main_categoria";
    }
    
    
    @GetMapping("/summary_main_2")
    public String summary2(
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

        return "inventory/inventario_06_02";
    }
    
}
*/







