package com.miempresa.quiz_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. Definimos el Bean que UsuarioServiceImpl está esperando
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Configuramos el filtro para evitar el bloqueo de la web
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF para que el formulario POST funcione
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permitimos el acceso total a todas las rutas
            )
            .formLogin(form -> form.disable()) // Desactivamos el formulario por defecto
            .httpBasic(basic -> basic.disable()); // Desactivamos la autenticación básica de ventana emergente
            
        return http.build();
    }
}