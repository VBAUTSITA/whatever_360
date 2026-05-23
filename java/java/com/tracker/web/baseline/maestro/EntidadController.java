package com.tracker.web.baseline.maestro;

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
@RequestMapping("/baseline-admin")
public class EntidadController {

@Autowired
private EntidadService service;

@GetMapping
public String viewHomePage(Model model) {
List<Entidad> listEntidad = service.listAll();
model.addAttribute("baseline", listEntidad);
return "baseline/maestro_baseline";
}

/*
@GetMapping("/formato")
public String viewHomePage2(Model model) {
List<Entidad> listEntidad = service.listAll();
model.addAttribute("baseline", listEntidad);
return "baseline/maestro_baseline_usuarios";
}
*/

@PostMapping("/add")
public String addEntidad(@ModelAttribute Entidad entidad, RedirectAttributes redirectAttributes) {
if (service.existsByOrden(entidad.getOrden())) {
redirectAttributes.addFlashAttribute("errorbaseline", "El orden ingresado ya existe!");
return "redirect:/baseline";
}
entidad.setFechaActualizada(new Date());
service.save(entidad);
redirectAttributes.addFlashAttribute("exitobaseline", "Fue cargado exitosamente");
return "redirect:/baseline-admin";
}

@PostMapping("/update")
@ResponseBody
public Map<String, Object> updateEntidad(@RequestBody Entidad entidad) {
Map<String, Object> response = new HashMap<>();
if (service.existsByOrdenAndIdNot(entidad.getOrden(), entidad.getId())) {
response.put("error", "El orden ingresado ya existe!");
} else {
entidad.setFechaActualizada(new Date());
service.save(entidad);
response.put("success", "Actualizado de forma exitosa!");
}
return response;
}

/*
@GetMapping("/edit/{id}")
public String showEditForm(@PathVariable("id") Long id, Model model) {
Entidad entidad = service.get(id);
model.addAttribute("entidad", entidad);
return "baseline/maestro_baseline_edit";
}*/

@GetMapping("/edit/{id}")
public String showEditForm(@PathVariable("id") Long id, Model model) {
Entidad entidad = service.get(id);
if (entidad == null) {
throw new EntityNotFoundException("Parametro no encontrado");
}
model.addAttribute("entidad", entidad);
return "baseline/maestro_baseline_edit";
}


@GetMapping("/delete/{id}")
public String deleteEntidad(@PathVariable Long id) {
service.delete(id);
return "redirect:/baseline-admin";
}

@GetMapping("/existsByOrden")
@ResponseBody
public Map<String, Boolean> existsByOrden(@RequestParam String orden) {
boolean exists = service.existsByOrden(orden);
Map<String, Boolean> response = new HashMap<>();
response.put("exists", exists);
return response;
}

/*
@PostMapping("/loadCsv")
public String loadCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
service.loadFromCsv(file);
redirectAttributes.addFlashAttribute("exitobaseline", "Datos cargados exitosamente desde el CSV");
return "redirect:/baseline";
}
*/

@PostMapping("/loadCsv")
public String uploadCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
if (file.isEmpty()) {
redirectAttributes.addFlashAttribute("errorbaseline", "Por favor, seleccione un archivo para cargar.");
return "redirect:/baseline-admin";
}
try {
service.loadFromCsv(file);
redirectAttributes.addFlashAttribute("exitobaseline", "Archivo cargado exitosamente.");
} catch (Exception e) {
redirectAttributes.addFlashAttribute("errorbaseline", "Error al cargar el archivo: " + e.getMessage());
}
return "redirect:/baseline-admin";
}

}