package com.tracker.web.interferencia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tracker.web.interferencia.model.Interferencia;
import com.tracker.web.interferencia.service.InterferenciaServiceImpl;

@Controller
@RequestMapping("/interferencia")
public class InterferenciaController {
		
	
	
	@Autowired
	private InterferenciaServiceImpl interferenciaService;
	
	
	@GetMapping("/main")
    public String home(@RequestParam(name="name2", required=false) String name, Model model) {
        model.addAttribute("name2", name);
        return "interferencia_main";
    }
	

	@RequestMapping(value = "/list" , method = RequestMethod.GET)
	public ModelAndView getAllInterferencia() {

		ModelAndView model = new ModelAndView();

		List<Interferencia> list = interferenciaService.getAllInterferencias();

		model.addObject("interferencia_list", list);
		model.setViewName("interferencia/interferencia_list");

		return model;
	}
	
	
	@RequestMapping(value = "/list/hist" , method = RequestMethod.GET)
	public ModelAndView getAllInterferenciaHist() {

		ModelAndView model = new ModelAndView();

		List<Interferencia> list_hist = interferenciaService.getAllInterferenciaHist();

		model.addObject("interferencia_list_hist", list_hist);
		model.setViewName("interferencia/interferencia_list_hist");

		return model;
	}
	
	
	/*historico*/
	@RequestMapping(value = "/hist/{id}", method = RequestMethod.GET)
	public ModelAndView listInterferenciaHistId(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		List<Interferencia> list_hist_id = interferenciaService.findInterferenciaHistById(id);
		
		model.addObject("interferencia_list_hist_id", list_hist_id);
		model.setViewName("interferencia/interferencia_list_hist_id");

		return model;

	}
	/**/
	
	/*LEGAL*/
	@RequestMapping(value = "/legal" , method = RequestMethod.GET)
	public ModelAndView getAllLegalInterferencia() {

		ModelAndView model = new ModelAndView();

		List<Interferencia> legal = interferenciaService.getAllLegalInterferencias();

		model.addObject("interferencia_legal", legal);
		model.setViewName("interferencia/interferencia_legal");

		return model;
	}
	
	/**/
	

	@RequestMapping(value = "/hist/id/{id}", method = RequestMethod.GET)
	public ModelAndView InterferenciaHistIdSolo2G(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		Interferencia interferencia = interferenciaService.findInterferenciaHistByIdSolo(id);

	
		model.addObject("interferencia_hist_id", interferencia);
		model.setViewName("interferencia/interferencia_hist_id");

		return model;

	}

	
	
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public ModelAndView updateInterferencia(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		Interferencia interferencia = interferenciaService.findInterferenciaById(id);

		model.addObject("interferenciaFormUpdate", interferencia);
		model.setViewName("interferencia/interferencia_form_update");

		return model;

	}
	

	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ModelAndView addInterferencia() {

		ModelAndView model = new ModelAndView();

		Interferencia interferencia = new Interferencia();

		model.addObject("interferenciaFormSave", interferencia);
		model.setViewName("interferencia/interferencia_form_save");

		return model;

	}
	

	
	@RequestMapping(value = "/guarda", method = RequestMethod.POST)
	public ModelAndView guarda(@ModelAttribute("interferenciaFormSave") Interferencia interferencia) {

		interferenciaService.addInterferencia(interferencia);

		return new ModelAndView("redirect:/interferencia/list");

	}
	
	
	@RequestMapping(value = "/actualiza", method = RequestMethod.POST)
	public ModelAndView actualiza(@ModelAttribute("interferenciaFormUpdate") Interferencia interferencia) {

		interferenciaService.updateInterferencia(interferencia);			

		return new ModelAndView("redirect:/interferencia/redireccion");
		

	}


	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteInterferencia(@PathVariable("id") int id) {

		interferenciaService.deleteInterferencia(id);

		return new ModelAndView("redirect:/interferencia/list");
	}

}