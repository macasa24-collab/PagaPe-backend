package com.pagape.api.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pagape.api.repository.UserRepository;
import com.pagape.api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * COMPONENTE DE SEGURIDAD: JwtAuthenticationFilter * Esta clase es un filtro
 * personalizado que intercepta todas las peticiones HTTP (una vez por
 * petición). Su función principal es extraer el token JWT de la cabecera,
 * validarlo y establecer la autenticación en el contexto de Spring Security si
 * el token es correcto.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param jwtService Servicio que contiene la lógica de validación de
     * tokens.
     * @param userRepository Repositorio para verificar que el usuario existe en
     * la BD.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraemos el encabezado 'Authorization'
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si el encabezado no existe o no empieza con 'Bearer ', ignoramos el filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Obtenemos el token eliminando la palabra 'Bearer ' (7 caracteres)
        jwt = authHeader.substring(7);

        try {
            // 3. Extraemos el email del token usando el servicio JWT
            userEmail = jwtService.extractEmail(jwt);

            /* * Verificamos si el email es válido y si el usuario no ha sido autenticado aún.
             * Esto evita procesar la autenticación múltiples veces en la misma petición.
             */
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Comprobamos si el usuario existe realmente en nuestra base de datos
                var usuarioOptional = userRepository.findByEmail(userEmail);

                if (usuarioOptional.isPresent() && jwtService.isTokenValid(jwt)) {
                    /*
                     * Creamos el objeto de autenticación de Spring.
                     * Usamos el email como 'principal' y una lista de roles vacía (Collections.emptyList())
                     * ya que por ahora no estamos gestionando perfiles de ADMIN/USER.
                     */
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            Collections.emptyList()
                    );

                    // Adjuntamos detalles técnicos de la conexión (IP, sesión, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 4. Establecemos al usuario como autenticado globalmente en esta petición
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log de error en caso de que el token esté corrupto o haya expirado
            this.logger.error("Error al procesar el token JWT: " + e.getMessage());
        }

        // 5. IMPORTANTE: Continuar con la cadena de filtros hacia el controlador
        filterChain.doFilter(request, response);
    }
}
