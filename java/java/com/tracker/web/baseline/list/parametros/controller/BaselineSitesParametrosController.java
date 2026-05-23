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
public class BaselineSitesParametrosController {

	@Autowired
	private BaselineSitesParametrosRepository baselineSitesParametrosRepository;
	
	@GetMapping("/baseline/parametros")
	public String listParametros(Model model) {
	List<Object[]> parametrosCounts = baselineSitesParametrosRepository.countByParametros();
	model.addAttribute("parametrosCounts", parametrosCounts);
	return "baseline/parametro";
	}
	
	@GetMapping("/baseline/parametros/{parametros}")
	public String getByParametros(@PathVariable String parametros, Model model) {
	List<BaselineSitesParametros> baselineSitesParametros = baselineSitesParametrosRepository.findByParametros(parametros);
	if (!baselineSitesParametros.isEmpty()) {
	model.addAttribute("baselineSitesParametros", baselineSitesParametros); //llama en la hoja html
	model.addAttribute("parametros", parametros);
	return "baseline/parametro_detail";
	} else {
	//return "404";
	model.addAttribute("baselineSitesParametros", baselineSitesParametros); //llama en la hoja html
	model.addAttribute("parametros", parametros);
	return "baseline/parametro_detail";
	}
	}
}