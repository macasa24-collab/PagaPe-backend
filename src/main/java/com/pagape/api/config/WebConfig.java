package com.pagape.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${storage.avatars.location}")
    private String avatarsLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/gastos/**")
                .addResourceLocations("file:" + storageLocation + "/");

        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + avatarsLocation + "/");
    }
}
