package com.tracker.web.inventario.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/inventario-admin")
public class InventarioStockController {

@Autowired
private InventarioStockService service;

@GetMapping
public String viewHomePage(Model model) {
List<InventarioStock> listInventarioStock = service.listAll();
model.addAttribute("inventarioStock", listInventarioStock);
return "inventory/inventario_index";
}


@PostMapping("/add")
public String addInventarioStock(@ModelAttribute InventarioStock inventarioStock, RedirectAttributes redirectAttributes) {
    // Eliminar la verificación de unicidad del campo 'orden'
    // if (service.existsByOrden(inventarioStock.getOrden())) {
    //     redirectAttributes.addFlashAttribute("errorInventarioStock", "El orden ingresado ya existe!");
    //     return "redirect:/baseline";
    // }

    inventarioStock.setFecha_hora(new Date());
    service.save(inventarioStock);
    redirectAttributes.addFlashAttribute("exitoInventarioStock", "Fue cargado exitosamente");
    return "redirect:/inventario-admin";
}



@PostMapping("/update")
@ResponseBody
public Map<String, Object> updateInventarioStock(@RequestBody InventarioStock inventarioStock) {
    Map<String, Object> response = new HashMap<>();
    
    // Eliminar la verificación de unicidad del campo 'orden'
    // if (service.existsByOrdenAndIdNot(inventarioStock.getOrden(), inventarioStock.getId())) {
    //     response.put("error", "El orden ingresado ya existe!");
    // } else {
    
    inventarioStock.setFecha_hora(new Date());
    service.save(inventarioStock);
    response.put("success", "Actualizado de forma exitosa!");
    
    // }
    
    return response;
}



@GetMapping("/edit/{id}")
public String showEditForm(@PathVariable("id") Long id, Model model) {
InventarioStock inventarioStock = service.get(id);
if (inventarioStock == null) {
throw new EntityNotFoundException("Inventario no encontrado");
}
model.addAttribute("inventarioStock", inventarioStock);
return "inventory/inventario_edit";
}


@GetMapping("/delete/{id}")
public String deleteInventarioStock(@PathVariable Long id) {
service.delete(id);
return "redirect:/inventario-admin";
}



@PostMapping("/loadCsv")
public String uploadCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
if (file.isEmpty()) {
redirectAttributes.addFlashAttribute("errorInventarioStock", "Por favor, seleccione un archivo para cargar.");
return "redirect:/inventario-admin";
}
try {
service.loadFromCsv(file);
redirectAttributes.addFlashAttribute("exitoInventarioStock", "Archivo cargado exitosamente.");
} catch (Exception e) {
redirectAttributes.addFlashAttribute("errorInventarioStock", "Error al cargar el archivo: " + e.getMessage());
}
return "redirect:/inventario-admin";
}

}