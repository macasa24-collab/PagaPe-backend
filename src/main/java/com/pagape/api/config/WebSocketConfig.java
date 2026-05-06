package com.pagape.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // El broker simple gestiona los canales de suscripción /topic/...
        registry.enableSimpleBroker("/topic");
        // Los mensajes enviados desde el cliente van a /app/...
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Punto de conexión WebSocket — Flutter conectará aquí
        // SockJS como fallback para clientes que no soporten WS nativo
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Endpoint nativo WebSocket (Flutter usa este directamente)
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
    }
}