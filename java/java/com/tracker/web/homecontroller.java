package com.tracker.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class homecontroller {
	
	@GetMapping("/login2")
    public String login(@RequestParam(name="name", required=false, defaultValue="Puerto 8080") String name, Model model) {
        model.addAttribute("name", name);
        return "login2";
    }	
	
	@GetMapping("/home")
    public String home(@RequestParam(name="name2", required=false) String name, Model model) {
        model.addAttribute("name2", name);
        return "home";
    }
	
	@GetMapping("/seguimiento/redirigiendo")
    public String redirigiendo() {
        return "redirigiendo";
    }
	
	@GetMapping("/interferencia/redireccion")
    public String redireccion() {
        return "redireccion";
    }

	@GetMapping("/seguimiento/recuperarcuenta")
    public String recuperarcuenta(@RequestParam(name="name3", required=false) String name, Model model) {
        model.addAttribute("name3", name);
        return "recuperarcuenta";
    }
	
	@GetMapping("/created")
    public String created() {
        return "created";
    }
	

	
}