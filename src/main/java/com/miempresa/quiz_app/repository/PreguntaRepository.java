package com.miempresa.quiz_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miempresa.quiz_app.entity.Pregunta;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

}
