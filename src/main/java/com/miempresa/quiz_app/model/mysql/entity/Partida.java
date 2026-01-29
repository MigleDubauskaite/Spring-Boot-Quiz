package com.miempresa.quiz_app.model.mysql.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    private int totalPreguntas;
    private int aciertos;

    private List<String> categorias; 
	private	List<Pregunta.TipoPregunta> tipos;

    private LocalDateTime fecha;

    public Partida() {
        this.fecha = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}


	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	public int getTotalPreguntas() {
		return totalPreguntas;
	}

	public void setTotalPreguntas(int totalPreguntas) {
		this.totalPreguntas = totalPreguntas;
	}

	public int getAciertos() {
		return aciertos;
	}

	public void setAciertos(int aciertos) {
		this.aciertos = aciertos;
	}

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	public List<Pregunta.TipoPregunta> getTipos() {
		return tipos;
	}

	public void setTipos(List<Pregunta.TipoPregunta> tipos) {
		this.tipos = tipos;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

    
}