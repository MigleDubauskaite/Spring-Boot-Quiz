package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.dto.*;
import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;
import com.miempresa.quiz_app.repository.mysql.JugadorRepository;
import com.miempresa.quiz_app.repository.mysql.PartidaRepository;
import com.miempresa.quiz_app.service.JuegoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JuegoServiceImpl implements JuegoService {

    private final PreguntaRepository preguntaRepo;
    private final PartidaRepository partidaRepo;
    private final JugadorRepository jugadorRepo;

    public JuegoServiceImpl(PreguntaRepository preguntaRepo,
                            PartidaRepository partidaRepo,
                            JugadorRepository jugadorRepo) {
        this.preguntaRepo = preguntaRepo;
        this.partidaRepo = partidaRepo;
        this.jugadorRepo = jugadorRepo;
    }

    // =====================================================
    // OPCIONES DEL QUIZ (HOME - THYMELEAF)
    // =====================================================

    @Override
    public OpcionesQuizDTO obtenerOpcionesDisponibles() {
        List<String> categorias = preguntaRepo.findCategorias().stream()
                .map(Pregunta::getCategoria)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Pregunta.TipoPregunta> tipos = Arrays.asList(Pregunta.TipoPregunta.values());
        
        // Lógica de negocio: Opciones reales de cantidad
        List<Integer> opcionesCantidad = List.of(10, 15, 20);

        return new OpcionesQuizDTO(categorias, tipos, opcionesCantidad);
    }

    // =====================================================
    // INICIO DE PARTIDA
    // =====================================================

    @Override
    @Transactional
    public PartidaResponse iniciarPartida(Long jugadorId, String nombre, List<String> categorias, 
                                         List<String> tiposStr, Integer cantidadSeleccionada) {

        validarNombre(nombre);

        // Lógica de negocio: Cantidad por defecto (10) si no es válida
        List<Integer> opcionesPermitidas = List.of(10, 15, 20);
        int cantidadFinal = (cantidadSeleccionada != null && opcionesPermitidas.contains(cantidadSeleccionada))
                ? cantidadSeleccionada
                : 10;

        // Gestionar jugador (Recuperar o Crear)
        Jugador jugador = gestionarJugador(jugadorId, nombre);

        // Convertir filtros y obtener preguntas
        List<Pregunta.TipoPregunta> tipos = convertirTiposStringAEnum(tiposStr);
        List<Pregunta> preguntas = obtenerPreguntasFiltradas(categorias, tipos, cantidadFinal);

        if (preguntas.isEmpty()) {
            throw new RuntimeException("No hay preguntas disponibles para los filtros seleccionados.");
        }

        // Crear Entidad Partida
        Partida partida = new Partida();
        partida.setJugador(jugador);
        partida.setTotalPreguntas(preguntas.size());
        partida.setCategorias(categorias);
        partida.setTipos(tipos);
        partida.setAciertos(0);
        partida.setPuntos(0);
        // Guardamos solo los IDs de MongoDB en la base de datos relacional
        partida.setPreguntaIds(preguntas.stream().map(Pregunta::getId).toList());

        partidaRepo.save(partida);

        // Retornar DTO (Mapeo manual para mantener DTO mudo)
        return new PartidaResponse(
                partida.getId(),
                jugador.getId(),
                jugador.getNombre(),
                partida.getAciertos(),
                partida.getTotalPreguntas(),
                preguntas.stream().map(PreguntaDTO::new).toList()
        );
    }

    // =====================================================
    // REGISTRO DE RESPUESTAS
    // =====================================================

    @Override
    @Transactional
    public RespuestaResultadoDTO registrarRespuesta(Long partidaId, String preguntaId, List<String> respuestasUsuario) {
        
        Partida partida = partidaRepo.findById(partidaId)
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        
        Pregunta pregunta = preguntaRepo.findById(preguntaId)
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));

        // Lógica de corrección (independiente del orden)
        List<String> correctas = pregunta.getRespuestasCorrectas();
        boolean esCorrecta = correctas.size() == respuestasUsuario.size() &&
                             new HashSet<>(correctas).equals(new HashSet<>(respuestasUsuario));

        int puntosObtenidos = 0;
        if (esCorrecta) {
            puntosObtenidos = 10;
            partida.setAciertos(partida.getAciertos() + 1);
            partida.setPuntos(partida.getPuntos() + puntosObtenidos);
            partidaRepo.save(partida);
        }

        return new RespuestaResultadoDTO(
                esCorrecta,
                correctas,
                puntosObtenidos,
                partida.getPuntos(),
                partida.getAciertos(),
                partida.getTotalPreguntas(),
                false // Aquí podrías añadir lógica de si es la última pregunta
        );
    }

    // =====================================================
    // CONSULTA DE PARTIDA
    // =====================================================

    @Override
    public PartidaResponse obtenerPartidaConPreguntas(Long partidaId) {
        Partida partida = partidaRepo.findById(partidaId)
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));

        List<Pregunta> preguntas = (List<Pregunta>) preguntaRepo.findAllById(partida.getPreguntaIds());

        return new PartidaResponse(
                partida.getId(),
                partida.getJugador().getId(),
                partida.getJugador().getNombre(),
                partida.getAciertos(),
                partida.getTotalPreguntas(),
                preguntas.stream().map(PreguntaDTO::new).toList()
        );
    }

    // =====================================================
    // MÉTODOS PRIVADOS / AUXILIARES
    // =====================================================

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío");
        }
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del jugador no puede exceder 50 caracteres");
        }
    }

    private Jugador gestionarJugador(Long jugadorId, String nombre) {
        if (jugadorId != null) {
            return jugadorRepo.findById(jugadorId)
                    .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));
        }
        // Si no hay ID, buscamos por nombre para evitar duplicados, o creamos uno nuevo
        return jugadorRepo.findByNombre(nombre)
                .orElseGet(() -> jugadorRepo.save(new Jugador(nombre)));
    }

    private List<Pregunta.TipoPregunta> convertirTiposStringAEnum(List<String> tiposStr) {
        if (tiposStr == null || tiposStr.isEmpty()) return List.of();
        return tiposStr.stream()
                .map(String::toUpperCase)
                .map(Pregunta.TipoPregunta::valueOf)
                .toList();
    }

    private List<Pregunta> obtenerPreguntasFiltradas(List<String> categorias, List<Pregunta.TipoPregunta> tipos, int cantidad) {
        // Obtenemos todas y filtramos en memoria (Shuffle para aleatoriedad)
        List<Pregunta> pool = preguntaRepo.findAll().stream()
                .filter(p -> categorias == null || categorias.isEmpty() || categorias.contains(p.getCategoria()))
                .filter(p -> tipos == null || tipos.isEmpty() || tipos.contains(p.getTipo()))
                .collect(Collectors.toList());

        Collections.shuffle(pool);
        return pool.stream().limit(cantidad).toList();
    }
}