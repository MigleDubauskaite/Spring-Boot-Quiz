package com.miempresa.quiz_app.dto;

import java.util.List;

public record RespuestaResultadoDTO(
    boolean esCorrecta,
    List<String> respuestasCorrectas, // Lista para soportar múltiples soluciones
    int puntosObtenidos,
    int puntosTotales,
    int aciertosActuales,
    int totalPreguntas,
    boolean terminada             // El "false" que pasas al final
) {
    // Constructor compacto opcional para asegurar que la lista no sea nula
    public RespuestaResultadoDTO {
        if (respuestasCorrectas == null) {
            respuestasCorrectas = List.of();
        }
    }
}