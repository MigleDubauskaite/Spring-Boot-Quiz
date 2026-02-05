package com.miempresa.quiz_app.controller.api;

import com.miempresa.quiz_app.dto.JuegoRequest;
import com.miempresa.quiz_app.dto.PartidaResponse;
import com.miempresa.quiz_app.dto.RespuestaRequest;
import com.miempresa.quiz_app.dto.RespuestaResultadoDTO;
import com.miempresa.quiz_app.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/juego")
@CrossOrigin(origins = "http://localhost:5173")
public class JuegoRestController {

    @Autowired
    private JuegoService juegoService;

    // 1. Iniciar nueva partida (desde React Login)
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPartida(@RequestBody JuegoRequest request) {
        try {
            PartidaResponse partida = juegoService.iniciarPartida(
                request.jugadorId(),
                request.nombre(),
                request.categorias(),
                request.tipos(),
                request.cantidad()
            );

            return ResponseEntity.ok(partida);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al iniciar la partida: " + e.getMessage());
        }
    }

    // 2. Obtener partida existente (por si React necesita recargar)
    @GetMapping("/partida/{id}")
    public ResponseEntity<?> obtenerPartida(@PathVariable Long id) {
        try {
            PartidaResponse partida = juegoService.obtenerPartidaConPreguntas(id);
            return ResponseEntity.ok(partida);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Partida no encontrada con ID: " + id);
                
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al recuperar la partida");
        }
    }

    // 3. Registrar respuesta del usuario
    @PostMapping("/answer")
    public ResponseEntity<?> responder(@RequestBody RespuestaRequest request) {
        try {
            RespuestaResultadoDTO resultado = juegoService.registrarRespuesta(
                request.partidaId(),
                request.preguntaId(),
                request.respuestasUsuario()
            );
            
            return ResponseEntity.ok(resultado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Datos inválidos: " + e.getMessage());
                
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al procesar la respuesta");
        }
    }
}