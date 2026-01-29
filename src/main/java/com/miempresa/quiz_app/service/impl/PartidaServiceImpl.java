package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.repository.mysql.PartidaRepository;
import com.miempresa.quiz_app.service.PartidaService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class PartidaServiceImpl implements PartidaService {

    @Autowired
    private PartidaRepository partidaRepo;

    @Override
    public Partida guardar(Partida partida) {
        return partidaRepo.save(partida);
    }

    @Override
    public Optional<Partida> obtenerPorId(Long id) {
        return partidaRepo.findById(id);
    }

    @Override
    public List<Partida> obtenerRankingTop10() {
        return partidaRepo.findTop10ByOrderByAciertosDesc();
    }

    @Override
    public List<Partida> obtenerPorJugador(Long jugadorId) {
        return partidaRepo.findByJugadorId(jugadorId);
    }

    @Override
    public Partida actualizar(Partida partida) {
        return partidaRepo.save(partida);
    }
}
