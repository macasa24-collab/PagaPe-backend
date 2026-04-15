package com.pagape.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.dto.request.LoginRequest;
import com.pagape.api.dto.request.RegisterRequest;
import com.pagape.api.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth") // Todas las rutas de esta clase empezarán por /auth
public class AuthController {
    @Autowired
    private AuthService authService;

    // --- ENDPOINT DE REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 1. Llamamos al servicio para que haga la magia
            authService.registrar(request);

            System.out.println("Usuario guardado con éxito: " + request.getEmail());

            // 2. Respondemos con éxito
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("¡Usuario " + request.getNombre() + " registrado con éxito!");

        } catch (Exception e) {
            // 3. Si algo falla (ej: email duplicado), capturamos el error y avisamos
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // --- ENDPOINT DE LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

        // Aquí en el futuro comprobaremos la contraseña y generaremos el JWT
        // String token = authService.login(request);

        System.out.println("Recibida petición de login para: " + request.getEmail());

        return ResponseEntity.ok("Login simulado con éxito para: " + request.getEmail());
    }
}