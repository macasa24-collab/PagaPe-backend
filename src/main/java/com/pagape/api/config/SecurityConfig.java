package com.pagape.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // Define esta clase como fuente de configuración para el contexto de Spring
@EnableWebSecurity // Habilita la seguridad web personalizada en la aplicación
public class SecurityConfig {

    @Bean // Registra el filtro de seguridad como un componente gestionado por Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF por compatibilidad con clientes REST y Postman
            .csrf(csrf -> csrf.disable())

            // Definición de reglas de autorización
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // Endpoints públicos (Login/Registro)
                .anyRequest().authenticated()           // El resto de rutas requieren autenticación
            )

            // Autenticación básica mediante cabeceras HTTP
            .httpBasic(withDefaults());

        return http.build();
    }
  
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}