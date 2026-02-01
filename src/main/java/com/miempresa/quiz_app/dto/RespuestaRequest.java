package com.miempresa.quiz_app.dto;

import java.util.List;

/**
 * Este es el DTO de "Entrada". 
 * Lo usamos en el Controller con @RequestBody.
 */
public record RespuestaRequest(
    Long partidaId, 
    String preguntaId, 
    List<String> respuestasUsuario 
) {}