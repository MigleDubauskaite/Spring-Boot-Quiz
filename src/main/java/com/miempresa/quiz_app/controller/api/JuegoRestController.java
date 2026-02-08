package com.miempresa.quiz_app.controller.api;

import com.miempresa.quiz_app.dto.*;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.service.JuegoService;
import com.miempresa.quiz_app.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/juego")
@CrossOrigin(origins = "http://localhost:5173")
public class JuegoRestController {

	private final JuegoService juegoService;
    private final UsuarioService usuarioService;

    public JuegoRestController(JuegoService juegoService, UsuarioService usuarioService) {
        this.juegoService = juegoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPartida(@RequestBody JuegoRequest request) {
        try {
            Jugador jugador;
            
            // Intentamos el login primero
            try {
                jugador = usuarioService.login(request.nombre(), request.password());
            } catch (IllegalArgumentException e) {
                // Si el error es "El usuario no existe", lo registramos automáticamente
                if (e.getMessage().contains("no existe")) {
                    jugador = usuarioService.registrar(request.nombre(), request.password());
                } else {
                    // Si el error es "Contraseña incorrecta", relanzamos el error
                    throw e;
                }
            }

            // Una vez tenemos al jugador (ya sea por login o registro nuevo)
            PartidaResponse partida = juegoService.iniciarPartida(
                jugador, 
                request.categorias(),
                request.tipos(),
                request.cantidad()
            );

            return ResponseEntity.ok(partida);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/partida/{id}")
    public ResponseEntity<?> obtenerPartida(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(juegoService.obtenerPartidaConPreguntas(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al recuperar la partida.");
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<?> responder(@RequestBody RespuestaRequest request) {
        try {
            return ResponseEntity.ok(juegoService.registrarRespuesta(
                request.partidaId(), request.preguntaId(), request.respuestasUsuario()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la respuesta.");
        }
    }
}