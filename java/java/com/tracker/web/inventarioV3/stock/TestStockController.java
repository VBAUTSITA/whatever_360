	package com.tracker.web.inventarioV3.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tracker.web.inventario.stock.InventarioStockRepository;
import com.tracker.web.inventarioV3.stock.TestStock;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
/*No es valido su uso ni sus import, cambiemos a algo un poco mas simple quizas 
 @NotNull @Positive
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
*/
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PostRemove;

//TestStockController

@Controller
@RequestMapping("/inventariov3")
public class TestStockController {

  @Autowired
	private TestStockRepository TestStockRepository;
  
	private TestStockService service;

  public TestStockController(TestStockService service) {
      this.service = service;
  }
  
  @GetMapping("/reporte_test")
  public String showKpi4gDashboard() {
      return "inventory/inventario_test"; // Thymeleaf buscará en templates/dash_kpi/dash_kpi_4g.html
  }		
  
	@GetMapping("/data/stock_test")
	@ResponseBody
	public Map<String, Object> getTestStock(
	@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
	@RequestParam(value = "size", required = false, defaultValue = "30") Integer size,
	
	@RequestParam(value = "length", required = false, defaultValue = "20") Integer length,
	
	@RequestParam(value = "search", required = false) String searchTerm,  // Cambiado aquí
	@RequestParam(value = "draw", required = false, defaultValue = "1") Integer draw) {

	System.out.println("Término de búsqueda: " + searchTerm);

	Pageable pageable = PageRequest.of(page, size);
	Page<TestStock> reportPage;
	

	if (searchTerm == null || searchTerm.isEmpty()) {
	    reportPage = TestStockRepository.findAll(pageable);         // ✔ instancia vía service
	} else {
	    reportPage = TestStockRepository.search(searchTerm, pageable); // ✔ instancia vía service
	}

	
	if (searchTerm == null || searchTerm.isEmpty()) {
	//reportPage = service.search(searchTerm, pageable);
	reportPage = TestStockRepository.findAll(pageable);
	} else {
	reportPage = TestStockRepository.search(searchTerm, pageable);
	}

	System.out.println("Resultados encontrados: " + reportPage.getTotalElements());

	Map<String, Object> response = new HashMap<>();
	response.put("draw", draw);
	response.put("recordsTotal", reportPage.getTotalElements());
	response.put("recordsFiltered", reportPage.getTotalElements());
	response.put("data", reportPage.getContent());

	return response;
	}
  
//Pantalla de búsqueda
  @GetMapping("/buscar")
  public String buscarForm() {
      return "inventory/inventario_v3_buscar";
  }

  // Procesa el ID ingresado
  @PostMapping("/buscar")
  public String buscarSubmit(@RequestParam("id") Long id) {

      // Aquí puedes validar si existe (opcional)
      if (!service.existsById(id)) {
          // Devuelve a la pantalla de búsqueda con un mensaje
          return "redirect:/inventariov3/buscar?error=No%20existe%20el%20ID";
      }

      // Redirige a la vista de edición
      return "redirect:/inventariov3/edit/" + id;
  }

  // Vista de edición existente


	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		//var item = service.findById(id);
	    TestStock item = service.findById(id);
	    model.addAttribute("item", item);
	
	    return "inventory/inventario_v3_edit";
	}


  // Guardar los cambios (igual a lo que ya tenías)
  @PostMapping("/edit")
  public String saveEdit(@RequestParam Long id,
                         @RequestParam Integer stock_real) {

      service.updateStockReal(id, stock_real);
      return "redirect:/inventariov3/reporte_test";
  }


  
  /*CAMBIOS JUEVES 22
  // -----------------------------
  // SHOW INVENTORY (READ)
  // -----------------------------
  @GetMapping
  public String list(Model model) {
      model.addAttribute("items", service.findAll());
      return "inventario_v3_list";
  }

  // -----------------------------
  // EDIT PAGE
  // -----------------------------
  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable Long id, Model model) {
      TestStock item = service.findById(id);
      model.addAttribute("item", item);
      return "inventory/inventario_v3_edit";
  }
  
  @PostMapping("/edit")
  public String saveEdit(
          @RequestParam Long id,
          @RequestParam int stock_real) {

      service.updateStock(id, stock_real);
      return "redirect:/inventario/reporte";
  }
  */
  
  // -----------------------------
  // SAVE EDIT (WRITE)
  // -----------------------------
  //miercoles 21 anterior
  /*@PostMapping("/edit")
  public String saveEdit(
          @RequestParam Long id,
          @RequestParam Integer stock) {

      service.updateStock(id, stock);
      return "redirect:/inventario";
  }*/
}