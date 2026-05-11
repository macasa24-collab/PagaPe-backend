package com.pagape.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;
import com.pagape.api.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    // El frontend llama a este endpoint tras el login para registrar su token FCM
    @PutMapping("/fcm-token")
    public ResponseEntity<?> actualizarFcmToken(
            @RequestBody Map<String, String> body,
            Authentication auth) {

        String fcmToken = body.get("fcmToken");
        if (fcmToken == null || fcmToken.isBlank()) {
            return ResponseEntity.badRequest().body("fcmToken requerido");
        }

        Usuario usuario = userService.obtenerPorEmail(auth.getName());
        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        usuario.setFcmToken(fcmToken);
        userRepository.save(usuario);
        return ResponseEntity.ok("Token FCM actualizado");
    }
}
