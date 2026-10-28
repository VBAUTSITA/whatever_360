package com.tracker.web.recursos.controlador;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tracker.web.recursos.model.Recursos;
import com.tracker.web.recursos.model.RecursosForm;
import com.tracker.web.recursos.repository.RecursosRepository;
import com.tracker.web.recursos.planner.PciRsiAsignacionService;
import com.tracker.web.recursos.planner.ResultadoAutoAsignacion;


@Controller
@RequestMapping("/recursos")
public class RecursosController {

    @Autowired
    private RecursosRepository recursosRepository;

    @Autowired
    private PciRsiAsignacionService pciRsiAsignacionService;

    // Vista principal
    @GetMapping("/list")
    public String list(Model model) {
      //  model.addAttribute("recursos", recursosRepository.findAll());
       // model.addAttribute("recursos", recursosRepository.findTop15ByOrderByIdDesc());
        model.addAttribute("recursos", recursosRepository.findAllByOrderByIdDesc());

        
        return "recursos/list";
    }

    // Formulario para nuevos registros
    

    /*
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("recursos", new ArrayList<Recursos>());
        return "recursos/add";
    }*/

    @GetMapping("/add")
    public String addForm(Model model) {
        RecursosForm form = new RecursosForm();
        for (int i = 0; i < 15; i++) {
            form.getRecursos().add(new Recursos()); // 15 instancias distintas
        }
        model.addAttribute("recursosForm", form);
        return "recursos/add";
    }

    @PostMapping("/saveAll")
    public String saveAll(@ModelAttribute("recursosForm") RecursosForm form) {

    	String today = java.time.LocalDate.now().toString();

        List<Recursos> validos = form.getRecursos().stream()
            .filter(r -> r.getSiteName() != null && !r.getSiteName().isEmpty())

            .peek(r -> r.setFechaActualizacion(today))   // ✅ ADD THIS LINE

            .collect(Collectors.toList());   // ✅ works in Java 8
        recursosRepository.saveAll(validos);
        return "redirect:/recursos/list";
    }


    // Formulario para asignar recursos
    /*
    @GetMapping("/asignar")
    public String assignForm(Model model) {
        model.addAttribute("recursos", recursosRepository.findAll());
        return "recursos/asignar";
    }

    @PostMapping("/asignar")
    public String assignResources(@ModelAttribute("recursos") List<Recursos> recursosList) {
        recursosRepository.saveAll(recursosList);
        return "redirect:/recursos/list";
    }*/
    @GetMapping("/asignar")
    public String assignForm(Model model) {
   	
    		RecursosForm form = new RecursosForm();
    		//form.setRecursos(recursosRepository.findAll());
    		//form.setRecursos(recursosRepository.findTop15ByOrderByIdDesc());
    		form.setRecursos(recursosRepository.findAllByOrderByIdDesc());

    		
    		
    		model.addAttribute("recursosForm", form);
    		
    		return "recursos/asignar";
    }
    
    /* OLD BLIND VERSION
    @PostMapping("/asignar")
    public String assignResources(@ModelAttribute("recursosForm") RecursosForm  form) {
    		recursosRepository.saveAll(form.getRecursos());
    		return "redirect:/recursos/list";
    		
    }*/
    
    @PostMapping("/asignar")
    public String assignResources(@ModelAttribute("recursosForm") RecursosForm form) {
    	String today = java.time.LocalDate.now().toString();

        for (Recursos enviado : form.getRecursos()) {
            if (enviado.getId() == null) continue;                 // ignora filas sin id
            Recursos db = recursosRepository.findById(enviado.getId()).orElse(null);
            if (db == null) continue;                              // id que no existe → ignora
            db.setPciPsc(enviado.getPciPsc());
            db.setRsi(enviado.getRsi());
            db.setMod(enviado.getMod());
            //db.setFechaActualizacion(enviado.getFechaActualizacion());
            //db.setFechaAsignacion(enviado.getFechaAsignacion());
            db.setFechaAsignacion(today);   // ✅ AUTO
            recursosRepository.save(db);
        }
        return "redirect:/recursos/list";
    }

    // Auto-asignacion de PCI/RSI (motor planner.js via Nashorn). NO guarda nada:
    // recalcula en memoria y vuelve a mostrar la misma pantalla de /asignar para
    // que el usuario revise/corrija antes de enviar el POST /asignar de siempre.
    @PostMapping("/autoAsignar")
    public String autoAsignar(@RequestParam("ids") List<Long> ids, Model model) {
        ResultadoAutoAsignacion resultado = pciRsiAsignacionService.autoAsignar(ids);

        RecursosForm form = new RecursosForm();
        form.setRecursos(resultado.getRecursos());

        model.addAttribute("recursosForm", form);
        model.addAttribute("autoComputadosIds", resultado.getAutoComputadosIds());
        model.addAttribute("omitidosAutoAsignacion", resultado.getOmitidos());
        model.addAttribute("totalExistentesRed", resultado.getTotalExistentes());
        model.addAttribute("conDatosTraficoRed", resultado.getConDatosTrafico());

        return "recursos/asignar";
    }

    @GetMapping("/modificar")
    public String modificarForm(Model model, Principal principal) {
        RecursosForm form = new RecursosForm();
        //form.setRecursos(recursosRepository.findByUsuarioIngreso(principal.getName()));
        
        form.setRecursos(recursosRepository.findByUsuarioIngresoOrderByIdDesc(principal.getName()));

        
        model.addAttribute("recursosForm", form);
        return "recursos/modificar";
    }
    
    @PostMapping("/modificar")
    public String modificarGuardar(@ModelAttribute("recursosForm") RecursosForm form, Principal principal) {
        for (Recursos enviado : form.getRecursos()) {
            if (enviado.getId() == null) continue;
            Recursos db = recursosRepository.findById(enviado.getId()).orElse(null);
            if (db == null) continue;
            // candado: solo modifica si el registro es del usuario logueado
            if (db.getUsuarioIngreso() == null || !db.getUsuarioIngreso().equals(principal.getName())) continue;

            db.setSemana(enviado.getSemana());
            db.setSiteName(enviado.getSiteName());
            db.setCellName(enviado.getCellName());
            db.setLongitud(enviado.getLongitud());
            db.setLatitud(enviado.getLatitud());
            db.setTecnologia(enviado.getTecnologia());
            db.setProyecto(enviado.getProyecto());
            db.setJefatura(enviado.getJefatura());
            db.setMod(enviado.getMod());
            db.setCarrier(enviado.getCarrier());
            db.setAzimuth(enviado.getAzimuth());
            db.setTxrxmode(enviado.getTxrxmode());
            db.setFreqband(enviado.getFreqband());
            db.setIndoor(enviado.getIndoor());
            recursosRepository.save(db);
        }
        return "redirect:/recursos/modificar";
    }

    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, Principal principal) {
        Recursos db = recursosRepository.findById(id).orElse(null);
        if (db != null && db.getUsuarioIngreso() != null
                && db.getUsuarioIngreso().equals(principal.getName())) {
            recursosRepository.deleteById(id);
        }
        return "redirect:/recursos/modificar";
    }


    
    
    
    
}