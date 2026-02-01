package com.miempresa.quiz_app.dto;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import java.util.List;

public record OpcionesQuizDTO(
    List<String> categorias,
    List<Pregunta.TipoPregunta> tipos,
    int cantidadMaxima
) {
	public OpcionesQuizDTO {
	    if (cantidadMaxima < 1) {
	        throw new IllegalArgumentException(
	            "La cantidad de preguntas debe ser mayor o igual a 1"
	        );
	    }
	}
}
