package com.miempresa.quiz_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miempresa.quiz_app.entity.Respuesta;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
}