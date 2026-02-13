package com.miempresa.quiz_app.config;

import com.miempresa.quiz_app.config.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // Inyectamos el filtro que ya creaste
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (no se usa con JWT) y activar CORS
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Política de sesiones sin estado (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Reglas de acceso
            .authorizeHttpRequests(auth -> auth
                // Permitir login y registro a todos
                .requestMatchers("/api/auth/**").permitAll()
                
                // --- Seguridad para MongoDB (Preguntas) ---
                // Solo los ADMIN pueden crear, editar o borrar
                .requestMatchers(HttpMethod.POST, "/api/preguntas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/preguntas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/preguntas/**").hasRole("ADMIN")
                
                // --- Seguridad para el Juego ---
                // Usuarios y Admins pueden jugar
                .requestMatchers("/api/juego/**").hasAnyRole("USER", "ADMIN")
                
                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
            )
            
            // 4. EL PASO CLAVE: Añadir tu JwtFilter antes del filtro de usuario/password
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración de CORS para que React (puerto 5173 o 3000) pueda entrar
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}