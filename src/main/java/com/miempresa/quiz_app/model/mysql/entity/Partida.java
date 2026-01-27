package com.miempresa.quiz_app.model.mysql.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    private String categoria;
    private String tipoPregunta;

    private LocalDateTime fecha;

    public Partida() {
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Jugador getJugador() { return jugador; }
    public int getTotalPreguntas() { return totalPreguntas; }
    public int getAciertos() { return aciertos; }
    public String getCategoria() { return categoria; }
    public String getTipoPregunta() { return tipoPregunta; }
    public LocalDateTime getFecha() { return fecha; }

    public void setJugador(Jugador jugador) { this.jugador = jugador; }
    public void setTotalPreguntas(int totalPreguntas) { this.totalPreguntas = totalPreguntas; }
    public void setAciertos(int aciertos) { this.aciertos = aciertos; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setTipoPregunta(String tipoPregunta) { this.tipoPregunta = tipoPregunta; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
