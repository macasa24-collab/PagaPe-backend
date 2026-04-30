package com.pagape.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String storageLocation; // Aquí llega "/var/pagape/uploads/gastos"

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. EL MANEJADOR (URL)
        // Le dice a Spring: "Si llega una petición que empiece por /uploads/gastos/..."
        registry.addResourceHandler("/uploads/gastos/**")
                // 2. LA UBICACIÓN FÍSICA (SISTEMA DE ARCHIVOS)
                // "... vete a buscarlo a la ruta física: file:/var/pagape/uploads/gastos/"
                .addResourceLocations("file:" + storageLocation + "/");
    }
}
