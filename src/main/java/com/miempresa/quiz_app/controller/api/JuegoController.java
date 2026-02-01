package com.miempresa.quiz_app.controller.api;

import com.miempresa.quiz_app.dto.PartidaResponse;
import com.miempresa.quiz_app.dto.RespuestaRequest;
import com.miempresa.quiz_app.dto.RespuestaResultadoDTO;
import com.miempresa.quiz_app.service.JuegoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/juego")
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    // 1. Obtener partida con preguntas (React lo usa al cargar)
    @GetMapping("/partida/{id}")
    public ResponseEntity<PartidaResponse> obtenerPartida(@PathVariable Long id) {
        try {
            PartidaResponse partida = juegoService.obtenerPartidaConPreguntas(id);
            
            if (partida == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 2. Registrar respuesta del usuario
    @PostMapping("/answer")
    public ResponseEntity<RespuestaResultadoDTO> responder(@RequestBody RespuestaRequest request) {
        try {
            RespuestaResultadoDTO resultado = juegoService.registrarRespuesta(
                request.partidaId(),
                request.preguntaId(),
                request.respuestasUsuario()
            );
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}