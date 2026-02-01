package com.miempresa.quiz_app.controller.web;

import com.miempresa.quiz_app.dto.OpcionesQuizDTO;
import com.miempresa.quiz_app.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Autowired
	private JuegoService juegoService;

	@GetMapping("/")
	public String mostrarHome(Model model) {
		// Obtener opciones disponibles para mostrar en el formulario
		OpcionesQuizDTO opciones = juegoService.obtenerOpcionesDisponibles();

		model.addAttribute("categorias", opciones.categorias());
		model.addAttribute("tipos", opciones.tipos());
		model.addAttribute("cantidadMax", opciones.cantidadMaxima());

		return "home";
	}

	@GetMapping("/error500-test")
	public String provocarError() {
		throw new RuntimeException("Error de prueba 500");
	}
}