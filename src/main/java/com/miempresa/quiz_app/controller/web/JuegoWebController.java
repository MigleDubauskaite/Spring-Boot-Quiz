package com.miempresa.quiz_app.controller.web;

import com.miempresa.quiz_app.dto.PartidaResponse;
import com.miempresa.quiz_app.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class JuegoWebController {

	@Autowired
	private JuegoService juegoService;

	@PostMapping("/juego/start")
	public String iniciarPartida(@RequestParam(required = false) Long id, @RequestParam String nombre,
			@RequestParam(required = false) List<String> categorias, @RequestParam(required = false) List<String> tipos,
			@RequestParam(defaultValue = "10") int cantidad, RedirectAttributes redirectAttributes) {

		try {
			PartidaResponse partida = juegoService.iniciarPartida(id, nombre, categorias, tipos, cantidad);
			return "redirect:http://localhost:5173/juego?partidaId=" + partida.partidaId();
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", "Datos inválidos: " + e.getMessage());
			return "redirect:/";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error al iniciar partida");
			return "redirect:/";
		}
	}
}