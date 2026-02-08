package com.miempresa.quiz_app.dto;

import java.util.List;

public record JuegoRequest(
    String nombre,
    String password,
    List<String> categorias,
    List<String> tipos,
    Integer cantidad
) {}