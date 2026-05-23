
package com.tracker.web.inventarioSimpleSuma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




@Controller
@RequestMapping("/inventory")
public class InventarioInmovilController {

    private final InventarioInmovilRepository repo;

    public InventarioInmovilController(InventarioInmovilRepository repo) {
        this.repo = repo;
    }
/*
    @GetMapping("/inmovil")
    public String ver(@RequestParam(required = false) Integer semana,
                      @RequestParam(required = false) String categoria,
                      Model model) {

        List<InventarioInmovil> data;

        if (semana != null && categoria != null) {
            data = repo.findBySemanaAndCategoria(semana, categoria);
        } else if (semana != null) {
            data = repo.findBySemana(semana);
        } else {
            data = repo.findAll();
        }

        model.addAttribute("rows", data);
        return "simple_inventario/simple";
    }

*/    
    @GetMapping("/datos")
	@ResponseBody
	public Map<String, Object> getInventarioSimple(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw,
	@RequestParam(value = "semana", required = false, defaultValue = "1") Integer semana ) {
	System.out.println("Semana de búsqueda: " + semana);
	Pageable pageable = PageRequest.of(page, size);
	Page<InventarioInmovil> reportPage;
	if (semana == null ) {
	reportPage = repo.findAll(pageable);	
	} else {
		//reportPage = repo.findBySemana(semana);
		reportPage = repo.findBySemana(semana, pageable);
	}
	System.out.println("Resultados encontrados: " + reportPage.getTotalElements());

	Map<String, Object> response = new HashMap<>();
	response.put("draw", draw);
	response.put("recordsTotal", reportPage.getTotalElements());
	response.put("recordsFiltered", reportPage.getTotalElements());
	response.put("data", reportPage.getContent());

	return response;
	}
}