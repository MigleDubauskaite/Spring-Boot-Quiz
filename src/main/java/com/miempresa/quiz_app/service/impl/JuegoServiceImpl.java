package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.dto.*;
import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.model.mysql.entity.Partida;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;
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

    public JuegoServiceImpl(PreguntaRepository preguntaRepo, PartidaRepository partidaRepo) {
        this.preguntaRepo = preguntaRepo;
        this.partidaRepo = partidaRepo;
    }

    // =====================================================
    // OPCIONES DEL QUIZ
    // =====================================================

    @Override
    public OpcionesQuizDTO obtenerOpcionesDisponibles() {
        List<String> categorias = preguntaRepo.findCategorias().stream()
                .map(Pregunta::getCategoria)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Pregunta.TipoPregunta> tipos = Arrays.asList(Pregunta.TipoPregunta.values());
        List<Integer> opcionesCantidad = List.of(10, 15, 20);

        return new OpcionesQuizDTO(categorias, tipos, opcionesCantidad);
    }

    // =====================================================
    // INICIO DE PARTIDA
    // =====================================================

    @Override
    @Transactional
    public PartidaResponse iniciarPartida(Jugador jugador, List<String> categorias, 
                                         List<String> tiposStr, Integer cantidadSeleccionada) {

        // 1. Lógica de negocio: Cantidad de preguntas
        List<Integer> opcionesPermitidas = List.of(10, 15, 20);
        int cantidadFinal = (cantidadSeleccionada != null && opcionesPermitidas.contains(cantidadSeleccionada))
                ? cantidadSeleccionada
                : 10;

        // 2. Convertir filtros y obtener preguntas de MongoDB
        List<Pregunta.TipoPregunta> tipos = convertirTiposStringAEnum(tiposStr);
        List<Pregunta> preguntas = obtenerPreguntasFiltradas(categorias, tipos, cantidadFinal);

        if (preguntas.isEmpty()) {
            throw new RuntimeException("No hay preguntas disponibles para los filtros seleccionados.");
        }

        // 3. Crear y guardar Entidad Partida en MySQL
        Partida partida = new Partida();
        partida.setJugador(jugador);
        partida.setTotalPreguntas(preguntas.size());
        partida.setCategorias(categorias);
        partida.setTipos(tipos);
        partida.setAciertos(0);
        partida.setPuntos(0);
        partida.setPreguntaIds(preguntas.stream().map(Pregunta::getId).toList());

        partidaRepo.save(partida);

        // 4. Retornar respuesta mapeada al DTO
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

        // 1. Recuperar la partida y la pregunta
        Partida partida = partidaRepo.findById(partidaId)
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));

        Pregunta pregunta = preguntaRepo.findById(preguntaId)
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));

        // 2. Lógica de corrección
        List<String> correctas = pregunta.getRespuestasCorrectas();
        boolean esCorrecta = correctas.size() == respuestasUsuario.size() &&
                             new HashSet<>(correctas).equals(new HashSet<>(respuestasUsuario));

        // 3. ACTUALIZAR ESTADO DE LA PARTIDA
        // Sumamos una respuesta intentada (sea acierto o fallo)
        partida.setPreguntasRespondidas(partida.getPreguntasRespondidas() + 1);

        int puntosObtenidos = 0;
        if (esCorrecta) {
            puntosObtenidos = 10;
            partida.setAciertos(partida.getAciertos() + 1);
            partida.setPuntos(partida.getPuntos() + puntosObtenidos);
        }

        // 4. Calcular si hemos llegado al final
        boolean terminada = partida.getPreguntasRespondidas() >= partida.getTotalPreguntas();

        // 5. Persistir cambios
        partidaRepo.save(partida);

        // 6. Retornar el DTO con la verdad absoluta del servidor
        return new RespuestaResultadoDTO(
                esCorrecta,
                correctas,
                puntosObtenidos,
                partida.getPuntos(),
                partida.getAciertos(),
                partida.getTotalPreguntas(),
                terminada
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
    // MÉTODOS PRIVADOS AUXILIARES
    // =====================================================

    private List<Pregunta.TipoPregunta> convertirTiposStringAEnum(List<String> tiposStr) {
        if (tiposStr == null || tiposStr.isEmpty()) return List.of();
        return tiposStr.stream()
                .map(String::toUpperCase)
                .map(Pregunta.TipoPregunta::valueOf)
                .toList();
    }

    private List<Pregunta> obtenerPreguntasFiltradas(List<String> categorias, List<Pregunta.TipoPregunta> tipos, int cantidad) {
        List<Pregunta> pool = preguntaRepo.findAll().stream()
                .filter(p -> categorias == null || categorias.isEmpty() || categorias.contains(p.getCategoria()))
                .filter(p -> tipos == null || tipos.isEmpty() || tipos.contains(p.getTipo()))
                .collect(Collectors.toList());

        Collections.shuffle(pool);
        return pool.stream().limit(cantidad).toList();
    }
}