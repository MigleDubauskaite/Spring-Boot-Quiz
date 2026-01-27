package com.miempresa.quiz_app.repository.mysql;

import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    Optional<Jugador> findByNombre(String nombre);
}