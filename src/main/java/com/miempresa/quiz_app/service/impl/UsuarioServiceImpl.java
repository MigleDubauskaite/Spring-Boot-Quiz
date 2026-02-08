package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.model.mysql.entity.Jugador;
import com.miempresa.quiz_app.repository.mysql.JugadorRepository;
import com.miempresa.quiz_app.service.UsuarioService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final JugadorRepository jugadorRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    // Spring inyectará automáticamente el Bean que definimos en la configuración
    public UsuarioServiceImpl(JugadorRepository jugadorRepo, BCryptPasswordEncoder passwordEncoder) {
        this.jugadorRepo = jugadorRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Jugador login(String nombre, String password) {
        // Buscamos al jugador por nombre
        Jugador jugador = jugadorRepo.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        // Comparamos la contraseña plana con el hash de la DB
        if (!passwordEncoder.matches(password, jugador.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }
        return jugador;
    }

    @Override
    @Transactional
    public Jugador registrar(String nombre, String password) {
        // 1. Validar que el nombre no esté pillado (Unique Constraint Check)
        if (jugadorRepo.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ese nombre de jugador ya está en uso.");
        }

        // 2. Validaciones de formato
        validarFormato(nombre, password);

        // 3. Crear nuevo jugador con password encriptada
        Jugador nuevo = new Jugador();
        nuevo.setNombre(nombre);
        nuevo.setPassword(passwordEncoder.encode(password)); // ✅ Hash seguro
        
        return jugadorRepo.save(nuevo);
    }

    private void validarFormato(String nombre, String password) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (nombre.length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
    }
}