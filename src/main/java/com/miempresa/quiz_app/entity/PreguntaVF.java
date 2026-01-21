package com.miempresa.quiz_app.entity;

import jakarta.persistence.Entity;

@Entity
public class PreguntaVF extends Pregunta {

	private static final long serialVersionUID = 1L;
	
	private boolean respuestaCorrecta;

	public PreguntaVF() {
	}

	public boolean isRespuestaCorrecta() {
		return respuestaCorrecta;
	}

	public void setRespuestaCorrecta(boolean respuestaCorrecta) {
		this.respuestaCorrecta = respuestaCorrecta;
	}
	
}
