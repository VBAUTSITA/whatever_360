package com.tracker.web.cuentas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.tracker.web.cuentas.model.Cuentas;
import com.tracker.web.cuentas.service.CuentasServiceImpl;

@Controller
@RequestMapping("/")
public class CuentasController {
		

	
	@Autowired
	private CuentasServiceImpl cuentasService;



	
	@RequestMapping(value = "/seguimiento/addaccount", method = RequestMethod.GET)
	public ModelAndView addCuentas() {

		ModelAndView model = new ModelAndView();

		Cuentas cuentas = new Cuentas();

		model.addObject("cuentasFormSave", cuentas);
		model.setViewName("addaccount");

		return model;

	}
	

	@RequestMapping(value = "/account", method = RequestMethod.POST)
	public ModelAndView account(@ModelAttribute("cuentasFormSave") Cuentas cuentas) {

			cuentasService.addCuentas(cuentas);

		return new ModelAndView("redirect:/created");

	}
	
	


}