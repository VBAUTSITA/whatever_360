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
public class Baseline5g4gSitesClasemoController {

	@Autowired
	private Baseline5g4gSitesClasemoRepository baselineSitesClasemoRepository;
	
	@GetMapping("/baseline-graph-5g-nsa/clase")
	public String listParametros(Model model) {
	List<Object[]> claseCounts = baselineSitesClasemoRepository.countByClase();
	model.addAttribute("claseCounts", claseCounts);
	return "baseline5g4gdetail/clase";
	}
	
	@GetMapping("/baseline-graph-5g-nsa/clase/{clase}")
	public String getByParametros(@PathVariable String clase, Model model) {
	List<Baseline5g4gSitesClasemo> baselineSitesClasemo = baselineSitesClasemoRepository.findByClase(clase);
	if (!baselineSitesClasemo.isEmpty()) {
	model.addAttribute("baselineSitesClasemo", baselineSitesClasemo); //llama en la hoja html
	model.addAttribute("clase", clase);
	return "baseline5g4gdetail/clase_detail";
	} else {
	//return "404"; 
	model.addAttribute("baselineSitesClasemo", baselineSitesClasemo); //llama en la hoja html
	model.addAttribute("clase", clase);
	return "baseline5g4gdetail/clase_detail";
	}
	}
}