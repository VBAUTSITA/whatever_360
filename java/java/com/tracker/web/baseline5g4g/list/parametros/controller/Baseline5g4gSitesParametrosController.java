package com.tracker.web.baseline5g4g.list.parametros.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
//@RequestMapping("/")
public class Baseline5g4gSitesParametrosController {

	@Autowired
	private Baseline5g4gSitesParametrosRepository baselineSitesParametrosRepository;
	
	@GetMapping("/baseline-graph-5g-nsa/parametros")
	public String listParametros(Model model) {
	List<Object[]> parametrosCounts = baselineSitesParametrosRepository.countByParametros();
	model.addAttribute("parametrosCounts", parametrosCounts);
	return "baseline/parametro";
	}
	
	@GetMapping("/baseline-graph-5g-nsa/parametros/{parametros}")
	public String getByParametros(@PathVariable String parametros, Model model) {
	List<Baseline5g4gSitesParametros> baselineSitesParametros = baselineSitesParametrosRepository.findByParametros(parametros);
	if (!baselineSitesParametros.isEmpty()) {
	model.addAttribute("baselineSitesParametros", baselineSitesParametros); //llama en la hoja html
	model.addAttribute("parametros", parametros);
	return "baseline5g4gdetail/parametro_detail";
	} else {
	//return "404";
	model.addAttribute("baselineSitesParametros", baselineSitesParametros); //llama en la hoja html
	model.addAttribute("parametros", parametros);
	return "baseline5g4gdetail/parametro_detail";
	}
	}
}