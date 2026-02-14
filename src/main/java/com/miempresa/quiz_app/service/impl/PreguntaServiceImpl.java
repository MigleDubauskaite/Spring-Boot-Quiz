package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;
import com.miempresa.quiz_app.service.PreguntaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.stereotype.Service;

import java.io.IOException;
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
	
	public List<Pregunta> importarDesdeArchivo(MultipartFile archivo) {
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        
	        // SEGURIDAD: Configuramos el mapper para este proceso específico
	        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	        // Convertimos el JSON a la lista de Entidades
	        List<Pregunta> preguntas = objectMapper.readValue(
	            archivo.getInputStream(), 
	            new TypeReference<List<Pregunta>>() {}
	        );
	        
	        // Guardamos en MongoDB
	        return repositorio.saveAll(preguntas);
	    } catch (IOException e) {
	        // Manejo de errores para que el sistema no se caiga
	        throw new RuntimeException("Error técnico al procesar el archivo: " + e.getMessage());
	    }
	}

}
