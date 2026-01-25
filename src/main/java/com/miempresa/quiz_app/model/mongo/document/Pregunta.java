package com.miempresa.quiz_app.model.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "preguntas") // Nombre de la colección en Mongo
public class Pregunta {

	@Id
	private String id;
	private String enunciado; // La pregunta
	private TipoPregunta tipo; // VF, MULTIPLE, TEXTO
	private String categoria; // Tema o categoría
	private String dificultad; // Fácil, Media, Difícil
	private List<String> opciones; // Opciones de respuesta (si aplica)
	private List<String> respuestas; // Respuesta(s) correcta(s)

	public Pregunta() {
	}

	// Getters y Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEnunciado() {
		return enunciado;
	}

	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}

	public TipoPregunta getTipo() {
		return tipo;
	}

	public void setTipo(TipoPregunta tipo) {
		this.tipo = tipo;
	}

	public List<String> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<String> opciones) {
		this.opciones = opciones;
	}

	public List<String> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(List<String> respuestas) {
		this.respuestas = respuestas;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getDificultad() {
		return dificultad;
	}

	public void setDificultad(String dificultad) {
		this.dificultad = dificultad;
	}

	public enum TipoPregunta {
		VF, UNICA, MULTIPLE;
	}

}
