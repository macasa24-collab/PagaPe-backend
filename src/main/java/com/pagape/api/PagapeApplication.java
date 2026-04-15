package com.pagape.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PagapeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PagapeApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner testHibernate(UserRepository userRepository) {
        return args -> {
            System.out.println("--- PROBANDO HIBERNATE ---");

            try {
                // 1. Creamos el objeto
                Usuario user = new Usuario("Daniel Test 15-04", "sadsada@pagape.com", "hash_seguro_123");

                // 2. Guardamos (Aquí Hibernate genera el INSERT)
                userRepository.save(user);

                System.out.println("¡Éxito! Usuario guardado con ID: " + user.getId());

            } catch (Exception e) {
                System.err.println("Error al guardar: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("--------------------------");
        };
    }^*/
}
