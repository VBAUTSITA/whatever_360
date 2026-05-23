package com.tracker.web.baseline.maestro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/baseline")
public class FormatoUserBaselineController {

	@Autowired
	private FormatoUserBaselineRepository formatoUserBaselineRepository;
	
	@GetMapping("/formato")
	public String listarBaselineUser(Model model) {
		model.addAttribute("formatouserbaseline", formatoUserBaselineRepository.findAll());
		return "baseline/maestro_baseline_usuarios";
	}
	

}