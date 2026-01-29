package com.miempresa.quiz_app.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;

@Controller
public class HomeController {

	@GetMapping({ "/", "/home" })
	public String home(Model model) {
		model.addAttribute("mensaje", "¡Bienvenido al Quiz de Programación!");
		return "/pregunta-home/home";
	}

	@GetMapping("/error500-test")
	public String provocarError() {
		throw new RuntimeException("Error de prueba 500");
	}

	@GetMapping("/jugar")
	public String jugar(@RequestParam(required = false) List<String> categorias,
			@RequestParam(required = false) List<Pregunta.TipoPregunta> tipos, Model model) {

		// Si no selecciona nada → mezcla
		if (categorias == null || categorias.isEmpty()) {
			categorias = null;
		}

		if (tipos == null || tipos.isEmpty()) {
			tipos = null;
		}

		model.addAttribute("categorias", categorias);
		model.addAttribute("tipos", tipos);

		return "redirect:http://localhost:5173/jugar";
	}

}