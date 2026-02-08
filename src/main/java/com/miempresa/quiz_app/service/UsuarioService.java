package com.miempresa.quiz_app.service;

import com.miempresa.quiz_app.model.mysql.entity.Jugador;

public interface UsuarioService {
    // Para usuarios que ya tienen cuenta
    Jugador login(String nombre, String password);
    
    // Para nuevos usuarios
    Jugador registrar(String nombre, String password);
}
