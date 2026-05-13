package com.pagape.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.dto.response.UsuarioResponse;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;
import com.pagape.api.service.AvatarService;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private AvatarService avatarService;

    @GetMapping("/me")
    public ResponseEntity<?> obtenerPerfil(Authentication auth) {
        Usuario usuario = userService.obtenerPorEmail(auth.getName());
        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        return ResponseEntity.ok(new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getUrlFotoPerfil()
        ));
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> subirAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        try {
            Usuario usuario = userService.obtenerPorEmail(auth.getName());
            if (usuario == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            String url = avatarService.subirAvatarUsuario(usuario.getId(), file);
            return ResponseEntity.ok(Map.of("avatarUrl", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error al subir el avatar"));
        }
    }

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
