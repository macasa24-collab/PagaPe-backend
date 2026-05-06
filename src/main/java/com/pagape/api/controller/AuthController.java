package com.pagape.api.controller;
 
import java.util.HashMap;
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.pagape.api.dto.request.LoginRequest;
import com.pagape.api.dto.request.RegisterRequest;
import com.pagape.api.model.Usuario;
import com.pagape.api.service.AuthService;
import com.pagape.api.service.UserService;
 
import jakarta.validation.Valid;
 
@RestController
@RequestMapping("/auth")
public class AuthController {
 
    @Autowired
    private AuthService authService;
 
    @Autowired
    private UserService userService;
 
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("¡Usuario " + request.getNombre() + " registrado con éxito!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
 
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
 
            // Recuperamos el usuario para devolver id y nombre junto al token
            Usuario usuario = userService.obtenerPorEmail(request.getEmail());
 
            Map<String, Object> response = new HashMap<>();
            response.put("token",  token);
            response.put("userId", usuario.getId());
            response.put("userName", usuario.getNombre());
 
            return ResponseEntity.ok(response);
 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autenticación: " + e.getMessage());
        }
    }
}