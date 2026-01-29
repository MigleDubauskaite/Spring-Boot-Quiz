package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.repository.mysql.JugadorRepository;
import com.miempresa.quiz_app.service.JuegoService;
import com.miempresa.quiz_app.service.PartidaService;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JuegoServiceImpl implements JuegoService {

    @Autowired
    private JugadorRepository jugadorRepo;

    @Autowired
    private PreguntaRepository preguntaRepo;

    @Autowired
    private PartidaService partidaService;

    @Override
    public Jugador crearJugadorAnonimo(String nombre) {
        return jugadorRepo.findByNombre(nombre)
                .orElseGet(() -> jugadorRepo.save(new Jugador(nombre)));
    }

    @Override
    public List<Pregunta> generarPreguntas(List<String> categorias, 
    				List<Pregunta.TipoPregunta> tipos, int cantidad) {
        List<Pregunta> todas = preguntaRepo.findAll();

        List<Pregunta> filtradas = todas.stream()
        	    .filter(p -> categorias == null 
        	            || categorias.stream()
        	                         .map(String::toLowerCase)
        	                         .anyMatch(c -> c.equals(p.getCategoria().toLowerCase())))
        	    .filter(p -> tipos == null
        	            || tipos.contains(p.getTipo()))  // Asegúrate de mandar enums desde React
        	    .toList();


        Collections.shuffle(filtradas);

        return filtradas.stream().limit(cantidad).toList();
    }

    @Override
    public Partida iniciarPartida(Jugador jugador, List<String> categorias, 
			List<Pregunta.TipoPregunta> tipos, int cantidad) {
        List<Pregunta> preguntas = generarPreguntas(categorias, tipos, cantidad);
        Partida partida = new Partida();
        partida.setJugador(jugador);
        partida.setTotalPreguntas(preguntas.size());
        partida.setCategorias(categorias);
        partida.setTipos(tipos);
        return partidaService.guardar(partida);
    }

    @Override
    public void registrarRespuesta(Partida partida, boolean correcta) {
        if (correcta) {
            partida.setAciertos(partida.getAciertos() + 1);
        }
        partidaService.actualizar(partida);
    }
}