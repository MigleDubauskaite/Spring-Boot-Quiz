package com.miempresa.quiz_app.controller;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.service.JuegoService;
import com.miempresa.quiz_app.service.PartidaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/juego")
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    @Autowired
    private PartidaService partidaService;

    @PostMapping("/start")
    public Partida start(@RequestBody Map<String, String> body) {
        String nombre = body.getOrDefault("nombre", "Anónimo");
        String categoria = body.get("categoria");
        String tipo = body.get("tipo");
        int cantidad = Integer.parseInt(body.getOrDefault("cantidad", "10"));

        Jugador jugador = juegoService.crearJugadorAnonimo(nombre);
        return juegoService.iniciarPartida(jugador, categoria, tipo, cantidad);
    }

    @PostMapping("/answer")
    public Partida answer(@RequestBody Map<String, String> body) {
        Long partidaId = Long.parseLong(body.get("partidaId"));
        boolean correcta = Boolean.parseBoolean(body.get("correcta"));

        Partida partida = partidaService.obtenerPorId(partidaId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        juegoService.registrarRespuesta(partida, correcta);
        return partida;
    }

    @GetMapping("/ranking")
    public List<Partida> ranking() {
        return partidaService.obtenerRankingTop10();
    }

    @GetMapping("/partidas/{jugadorId}")
    public List<Partida> partidasJugador(@PathVariable Long jugadorId) {
        return partidaService.obtenerPorJugador(jugadorId);
    }

    @GetMapping("/preguntas")
    public List<Pregunta> obtenerPreguntas(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "10") int cantidad) {
        return juegoService.generarPreguntas(categoria, tipo, cantidad);
    }
}
