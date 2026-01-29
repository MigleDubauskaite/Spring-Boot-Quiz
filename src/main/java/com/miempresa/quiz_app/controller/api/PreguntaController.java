package com.miempresa.quiz_app.controller.api;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.service.impl.PreguntaServiceImpl;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = "http://localhost:5173")
public class PreguntaController {

    private final PreguntaServiceImpl preguntaService;

    public PreguntaController(PreguntaServiceImpl service) {
        this.preguntaService = service;
    }
    
    // Obtener todas las preguntas
    @GetMapping
    public List<Pregunta> obtenerTodas() {
        return preguntaService.obtenerTodas();
    }

    // Obtener pregunta por ID
    @GetMapping("/{id}")
    public Optional<Pregunta> obtenerPorId(@PathVariable String id) {
        return preguntaService.obtenerPorId(id);
    }

    // Obtener preguntas por categoría
    @GetMapping("/categoria/{categoria}")
    public List<Pregunta> obtenerPorCategoria(@PathVariable String categoria) {
        return preguntaService.obtenerPorCategoria(categoria);
    }

    // Obtener preguntas por tipo
    @GetMapping("/tipo/{tipo}")
    public List<Pregunta> obtenerPorTipo(@PathVariable Pregunta.TipoPregunta tipo) {
        return preguntaService.obtenerPorTipo(tipo);
    }

    // Guardar una nueva pregunta
    @PostMapping
    public Pregunta guardarPregunta(@RequestBody Pregunta pregunta) {
        return preguntaService.guardarPregunta(pregunta);
    }
    
    @PutMapping("/{id}")
    public Pregunta actualizarPregunta(@PathVariable String id, @RequestBody Pregunta pregunta) {
        pregunta.setId(id);
        return preguntaService.guardarPregunta(pregunta);
    }

    // Eliminar una pregunta por ID
    @DeleteMapping("/{id}")
    public void eliminarPregunta(@PathVariable String id) {
        preguntaService.eliminarPregunta(id);
    }
}
