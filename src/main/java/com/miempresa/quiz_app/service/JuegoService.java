package com.miempresa.quiz_app.service;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;

import java.util.List;

public interface JuegoService {

    Jugador crearJugadorAnonimo(String nombre);

    List<Pregunta> generarPreguntas(List<String> categorias, 
			List<Pregunta.TipoPregunta> tipos, int cantidad);

    Partida iniciarPartida(Jugador jugador, List<String> categorias, 
			List<Pregunta.TipoPregunta> tipos, int cantidad);

    void registrarRespuesta(Partida partida, boolean correcta);
}