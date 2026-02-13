package com.miempresa.quiz_app.service.impl;

import com.miempresa.quiz_app.dto.LoginResponse;
import com.miempresa.quiz_app.model.mysql.entity.Usuario;
import com.miempresa.quiz_app.repository.mysql.UsuarioRepository;
import com.miempresa.quiz_app.service.UsuarioService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository jugadorRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    public UsuarioServiceImpl(UsuarioRepository jugadorRepo, BCryptPasswordEncoder passwordEncoder) {
        this.jugadorRepo = jugadorRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse autenticar(String nombre, String password) {
        // 1. Buscamos al usuario
        Usuario usuario = jugadorRepo.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        // 2. Comparamos contraseña
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        // 3. Generamos el Token JWT (El "Carnet" que viaja a React)
        String token = Jwts.builder()
                .setSubject(usuario.getNombre())
                .claim("rol", usuario.getRol().name()) // Ej: ROLE_USER o ROLE_ADMIN
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Expira en 1 hora
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // 4. Retornamos el DTO con toda la info necesaria para el Frontend
        return new LoginResponse(token, usuario.getNombre(), usuario.getRol().name());
    }

    @Override
    @Transactional
    public Usuario registrar(String nombre, String password) {
        if (jugadorRepo.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ese nombre de jugador ya está en uso.");
        }

        validarFormato(nombre, password);

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setPassword(passwordEncoder.encode(password));
        
        // Por defecto, asignamos ROLE_USER (asegúrate de tener este campo en tu Entity)
        // nuevo.setRol(Rol.ROLE_USER); 

        return jugadorRepo.save(nuevo);
    }

    @Override
    public Usuario buscarPorNombre(String nombre) {
        return jugadorRepo.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    @Override
    public Usuario login(String nombre, String password) {
        Usuario jugador = buscarPorNombre(nombre);
        if (!passwordEncoder.matches(password, jugador.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }
        return jugador;
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