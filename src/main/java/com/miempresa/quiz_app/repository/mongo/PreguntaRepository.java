package com.miempresa.quiz_app.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;

import java.util.List;

@Repository
public interface PreguntaRepository extends MongoRepository<Pregunta, String> {

    List<Pregunta> findByCategoria(String categoria);
    List<Pregunta> findByTipo(Pregunta.TipoPregunta tipo);
}