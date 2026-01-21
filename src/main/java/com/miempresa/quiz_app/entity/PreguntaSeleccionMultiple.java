package com.miempresa.quiz_app.entity;

import java.util.List;

import jakarta.persistence.Entity;

@Entity
public class PreguntaSeleccionMultiple extends Pregunta {

	private static final long serialVersionUID = 1L;

	private List<String> opciones;
	private List<String> opcionesCorrectas;

	public PreguntaSeleccionMultiple() {
	}

	public List<String> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<String> opciones) {
		this.opciones = opciones;
	}

	public List<String> getOpcionesCorrectas() {
		return opcionesCorrectas;
	}

	public void setOpcionesCorrectas(List<String> opcionesCorrectas) {
		this.opcionesCorrectas = opcionesCorrectas;
	}

}
