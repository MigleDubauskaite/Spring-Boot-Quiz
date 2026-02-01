package com.miempresa.quiz_app.dto;

import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import java.util.List;

public class JuegoRequest {
	private long jugadorId;
    private String nombre;
    private List<String> categorias;
    private List<Pregunta.TipoPregunta> tipos;
    private int cantidad = 10;

    // Getters y setters
    
    public long getJugadorId() {return jugadorId;	}
    public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }

    public List<Pregunta.TipoPregunta> getTipos() { return tipos; }
    public void setTipos(List<Pregunta.TipoPregunta> tipos) { this.tipos = tipos; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}