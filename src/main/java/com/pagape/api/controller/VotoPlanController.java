package com.pagape.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.dto.request.VotoPlanRequest;
import com.pagape.api.service.VotoPlanService;

@RestController
@RequestMapping("/poll")
public class VotoPlanController {

    @Autowired
    private VotoPlanService votoPlanService;

     /*Endpoint para registrar o actualizar un voto en un plan.
     El usuario se extrae de forma segura a través del token JWT (Authentication). */
     
    @PostMapping("/vote")
    public ResponseEntity<?> votar(@RequestBody VotoPlanRequest request, Authentication authentication) {
        try {
            // 1. Extraemos el email del usuario desde el Token JWT de forma segura
            String emailUsuario = authentication.getName();

            // 2. Delegamos la lógica y validaciones al servicio
            votoPlanService.registrarVoto(request, emailUsuario);

            // 3. Respuesta de éxito
            return ResponseEntity.ok("Voto registrado correctamente.");

        } catch (RuntimeException e) {
            // Manejo de errores de lógica de negocio (Plan cerrado, no es miembro, etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Manejo de errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar el voto: " + e.getMessage());
        }
    }
}