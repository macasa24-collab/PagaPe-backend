package com.pagape.api.controller;

import com.pagape.api.dto.request.LoginRequest;
import com.pagape.api.dto.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // Todas las rutas de esta clase empezarán por /auth
public class AuthController {

    // --- ENDPOINT DE REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        
        // Aquí es donde llamaremos al UserService en el futuro para guardar el usuario
        // userService.registrarUsuario(request);
        
        System.out.println("Recibida petición de registro para: " + request.getEmail());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("¡Usuario " + request.getNombre() + " recibido correctamente en el backend!");
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