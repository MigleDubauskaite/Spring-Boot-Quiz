package com.miempresa.quiz_app.service;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.repository.mysql.JugadorRepository;
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
    public List<Pregunta> generarPreguntas(String categoria, String tipo, int cantidad) {
        List<Pregunta> todas = preguntaRepo.findAll();

        List<Pregunta> filtradas = todas.stream()
                .filter(p -> categoria == null || p.getCategoria().equalsIgnoreCase(categoria))
                .filter(p -> tipo == null || p.getTipo().name().equalsIgnoreCase(tipo))
                .toList();

        Collections.shuffle(filtradas);

        return filtradas.stream().limit(cantidad).toList();
    }

    @Override
    public Partida iniciarPartida(Jugador jugador, String categoria, String tipo, int cantidad) {
        List<Pregunta> preguntas = generarPreguntas(categoria, tipo, cantidad);
        Partida partida = new Partida();
        partida.setJugador(jugador);
        partida.setTotalPreguntas(preguntas.size());
        partida.setCategoria(categoria);
        partida.setTipoPregunta(tipo);
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
