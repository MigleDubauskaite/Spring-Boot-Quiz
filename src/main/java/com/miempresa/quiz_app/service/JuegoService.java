package com.miempresa.quiz_app.service;

import com.miempresa.quiz_app.dto.*;
import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import java.util.List;

public interface JuegoService {
    OpcionesQuizDTO obtenerOpcionesDisponibles();
    
    PartidaResponse iniciarPartida(Long jugadorId, String nombre, 
                                          List<String> categorias, 
                                          List<Pregunta.TipoPregunta> tipos, 
                                          int cantidad);
                                          
    PartidaResponse obtenerPartidaConPreguntas(Long partidaId);
    
    RespuestaResultadoDTO registrarRespuesta(Long partidaId, String preguntaId, List<String> respuestasUsuario);
}