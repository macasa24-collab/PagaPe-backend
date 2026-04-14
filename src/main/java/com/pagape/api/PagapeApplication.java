package com.pagape.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;

@SpringBootApplication
public class PagapeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PagapeApplication.class, args);
    }

    @Bean
    public CommandLineRunner testHibernate(UserRepository userRepository) {
        return args -> {
            System.out.println("--- PROBANDO HIBERNATE ---");

            try {
                // 1. Creamos el objeto
                Usuario user = new Usuario("Gemini Test", "test@pagape.com", "foto_perfil.png", "hash_seguro_123");

                // 2. Guardamos (Aquí Hibernate genera el INSERT)
                userRepository.save(user);

                System.out.println("¡Éxito! Usuario guardado con ID: " + user.getId());

            } catch (Exception e) {
                System.err.println("Error al guardar: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("--------------------------");
        };
    }
}
