package com.miempresa.quiz_app.service;

import java.util.List;
import java.util.Optional;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;

public interface PreguntaService {

    List<Pregunta> obtenerTodas();

    Optional<Pregunta> obtenerPorId(String id);

    List<Pregunta> obtenerPorCategoria(String categoria);

    List<Pregunta> obtenerPorTipo(Pregunta.TipoPregunta tipo);

    Pregunta guardarPregunta(Pregunta pregunta);

    void eliminarPregunta(String id);
}
