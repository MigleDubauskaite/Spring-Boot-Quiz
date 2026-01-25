package com.miempresa.quiz_app.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miempresa.quiz_app.model.mongo.document.Pregunta;
import com.miempresa.quiz_app.repository.mongo.PreguntaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PreguntaRepository repositorio) {
        return args -> {
            // Solo cargar si la colección está vacía
            if (repositorio.count() == 0) {
                ObjectMapper mapper = new ObjectMapper(); // convierte tu JSON en una lista de objetos Pregunta
                
                // Leer preguntas.json de resources
                TypeReference<List<Pregunta>> typeReference = new TypeReference<List<Pregunta>>() {};
                InputStream inputStream = TypeReference.class.getResourceAsStream("/preguntas.json");
                
                try {
                    List<Pregunta> preguntas = mapper.readValue(inputStream, typeReference);
                    repositorio.saveAll(preguntas);
                    System.out.println("Preguntas cargadas en MongoDB!");
                } catch (Exception e) {
                    System.out.println("No se pudieron cargar las preguntas: " + e.getMessage());
                }
            }
        };
    }
}
