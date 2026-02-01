package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.dto.*;
import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;
import com.miempresa.quiz_app.repository.mysql.JugadorRepository;
import com.miempresa.quiz_app.repository.mysql.PartidaRepository;
import com.miempresa.quiz_app.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JuegoServiceImpl implements JuegoService {

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private PreguntaRepository preguntaRepo;
    @Autowired private JugadorRepository jugadorRepo;
    @Autowired private PartidaRepository partidaRepo;

    @Override
    public OpcionesQuizDTO obtenerOpcionesDisponibles() {
        List<String> categorias = mongoTemplate.findDistinct("categoria", Pregunta.class, String.class);
        List<Pregunta.TipoPregunta> tipos = Arrays.asList(Pregunta.TipoPregunta.values());
        return new OpcionesQuizDTO(categorias, tipos, 20);
    }

    @Override
    public PartidaResponse iniciarPartida(Long jugadorId, String nombre, List<String> cats, List<Pregunta.TipoPregunta> tipos, int cant) {
        // 1. Lógica de Jugador: Buscar por ID -> Buscar por Nombre -> Crear nuevo
        Jugador jugador = gestionarJugador(jugadorId, nombre);

        // 2. Obtener Preguntas filtradas y mezcladas
        List<Pregunta> seleccionadas = obtenerPreguntasFiltradas(cats, tipos, cant);

        // 3. Crear y guardar Partida
        Partida partida = new Partida();
        partida.setJugador(jugador);
        partida.setTotalPreguntas(seleccionadas.size());
        partida.setCategorias(cats);
        partida.setTipos(tipos);
        partida.setPreguntaIds(seleccionadas.stream().map(Pregunta::getId).toList());
        partida = partidaRepo.save(partida);

        return new PartidaResponse(
                partida.getId(), 
                jugador.getId(), 
                jugador.getNombre(), 
                0,                          // <-- Aciertos iniciales
                partida.getTotalPreguntas(), 
                seleccionadas.stream().map(PreguntaDTO::new).toList()
            );
    }

    @Override
    public PartidaResponse obtenerPartidaConPreguntas(Long partidaId) {
        Partida partida = partidaRepo.findById(partidaId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));
        
        List<Pregunta> preguntas = (List<Pregunta>) preguntaRepo.findAllById(partida.getPreguntaIds());
        
        return new PartidaResponse(
            partida.getId(), 
            partida.getJugador().getId(), 
            partida.getJugador().getNombre(),
            partida.getAciertos(), 
            partida.getTotalPreguntas(), 
            preguntas.stream().map(PreguntaDTO::new).toList()
        );
    }

    @Override
    public RespuestaResultadoDTO registrarRespuesta(Long partidaId, String preguntaId, List<String> respuestasUsuario) {
        Partida partida = partidaRepo.findById(partidaId).orElseThrow();
        Pregunta pregunta = preguntaRepo.findById(preguntaId).orElseThrow();

        // Comparación de listas: deben tener mismo tamaño y mismos elementos
        boolean esCorrecta = respuestasUsuario != null && pregunta.getRespuestasCorrectas().size() == respuestasUsuario.size() 
                             && respuestasUsuario.containsAll(pregunta.getRespuestasCorrectas());

        if (esCorrecta) {
            partida.setAciertos(partida.getAciertos() + 1);
            partida.setPuntos(partida.getPuntos() + 10); // Puntos fijos
            partidaRepo.save(partida);
        }

        return new RespuestaResultadoDTO(
            esCorrecta, 
            pregunta.getRespuestasCorrectas(), 
            esCorrecta ? 10 : 0, 
            partida.getPuntos(), 
            partida.getAciertos(), 
            partida.getTotalPreguntas(), 
            false
        );
    }

    // --- Métodos Privados de Apoyo ---

    private Jugador gestionarJugador(Long id, String nombre) {
        if (id != null) {
            return jugadorRepo.findById(id).orElseGet(() -> 
                jugadorRepo.findByNombre(nombre).orElseGet(() -> crearNuevoJugador(nombre))
            );
        }
        return jugadorRepo.findByNombre(nombre).orElseGet(() -> crearNuevoJugador(nombre));
    }

    private Jugador crearNuevoJugador(String nombre) {
        Jugador nuevo = new Jugador();
        nuevo.setNombre(nombre);
        return jugadorRepo.save(nuevo);
    }

    private List<Pregunta> obtenerPreguntasFiltradas(List<String> cats, List<Pregunta.TipoPregunta> tipos, int cant) {
        List<Pregunta> pool = preguntaRepo.findAll().stream()
                .filter(p -> cats == null || cats.isEmpty() || cats.contains(p.getCategoria()))
                .filter(p -> tipos == null || tipos.isEmpty() || tipos.contains(p.getTipo()))
                .collect(java.util.stream.Collectors.toList());
        
        Collections.shuffle(pool);
        return pool.stream().limit(cant).toList();
    }
}