package com.tracker.web.baseline.list.parametros.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
//@RequestMapping("/")
public class BaselineSitesClasemoController {

	@Autowired
	private BaselineSitesClasemoRepository baselineSitesClasemoRepository;
	
	@GetMapping("/baseline/clase")
	public String listParametros(Model model) {
	List<Object[]> claseCounts = baselineSitesClasemoRepository.countByClase();
	model.addAttribute("claseCounts", claseCounts);
	return "baseline/clase";
	}
	
	@GetMapping("/baseline/clase/{clase}")
	public String getByParametros(@PathVariable String clase, Model model) {
	List<BaselineSitesClasemo> baselineSitesClasemo = baselineSitesClasemoRepository.findByClase(clase);
	if (!baselineSitesClasemo.isEmpty()) {
	model.addAttribute("baselineSitesClasemo", baselineSitesClasemo); //llama en la hoja html
	model.addAttribute("clase", clase);
	return "baseline/clase_detail";
	} else {
	//return "404"; 
	model.addAttribute("baselineSitesClasemo", baselineSitesClasemo); //llama en la hoja html
	model.addAttribute("clase", clase);
	return "baseline/clase_detail";
	}
	}
}