package com.miempresa.quiz_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miempresa.quiz_app.entity.Tema;

public interface TemaRepository extends JpaRepository<Tema, Long> {
}