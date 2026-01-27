package com.miempresa.quiz_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping({"/", "/home"})
	public String home(Model model) {
		model.addAttribute("mensaje", "¡Bienvenido al Quiz de Programación!");
		return "pregunta-home/home";
	}
	
	@GetMapping("/error500-test")
	public String provocarError() {
	    throw new RuntimeException("Error de prueba 500");
	}

}
