package com.miempresa.quiz_app.service;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreguntaServiceImpl implements PreguntaService {

	private final PreguntaRepository repositorio;

	public PreguntaServiceImpl(PreguntaRepository repositorio) {
		this.repositorio = repositorio;
	}

	public List<Pregunta> obtenerTodas() {
		return repositorio.findAll();
	}

	public Optional<Pregunta> obtenerPorId(String id) {
		return repositorio.findById(id);
	}

	public List<Pregunta> obtenerPorCategoria(String categoria) {
		return repositorio.findByCategoria(categoria);
	}

	public List<Pregunta> obtenerPorTipo(Pregunta.TipoPregunta tipo) {
		return repositorio.findByTipo(tipo);
	}

	public Pregunta guardarPregunta(Pregunta pregunta) {
		return repositorio.save(pregunta);
	}

	public void eliminarPregunta(String id) {
		repositorio.deleteById(id);
	}

}
