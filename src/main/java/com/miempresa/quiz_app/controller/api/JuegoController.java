package com.miempresa.quiz_app.controller.api;

import com.miempresa.quiz_app.dto.JuegoRequest;
import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.service.JuegoService;
import com.miempresa.quiz_app.service.PartidaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/juego")
public class JuegoController {

	@Autowired
	private JuegoService juegoService;

	@Autowired
	private PartidaService partidaService;

	// ▶️ Iniciar partida
	@PostMapping("/start")
	public Partida start(@RequestBody JuegoRequest request) {
		String nombre = request.getNombre() != null ? request.getNombre() : "Anónimo";
		Jugador jugador = juegoService.crearJugadorAnonimo(nombre);

		return juegoService.iniciarPartida(jugador, request.getCategorias(), request.getTipos(), request.getCantidad());
	}

	// ▶️ Registrar respuesta
	@PostMapping("/answer")
	public Partida answer(@RequestParam Long partidaId, @RequestParam boolean correcta) {

		Partida partida = partidaService.obtenerPorId(partidaId)
				.orElseThrow(() -> new RuntimeException("Partida no encontrada"));

		juegoService.registrarRespuesta(partida, correcta);
		return partida;
	}

	// ▶️ Ranking Top 10
	@GetMapping("/ranking")
	public List<Partida> ranking() {
		return partidaService.obtenerRankingTop10();
	}

	// ▶️ Partidas por jugador
	@GetMapping("/partidas/{jugadorId}")
	public List<Partida> partidasJugador(@PathVariable Long jugadorId) {
		return partidaService.obtenerPorJugador(jugadorId);
	}

	// ▶️ Obtener preguntas filtradas
	@GetMapping("/preguntas")
	public List<Pregunta> obtenerPreguntas(@RequestParam(required = false) List<String> categorias,
			@RequestParam(required = false) List<Pregunta.TipoPregunta> tipos,
			@RequestParam(defaultValue = "10") int cantidad) {
		return juegoService.generarPreguntas(categorias, tipos, cantidad);
	}
}
