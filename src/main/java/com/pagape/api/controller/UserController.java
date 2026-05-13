package com.pagape.api.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${storage.location}")
    private String storageLocation;

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> obtenerPerfil(Authentication auth) {
        Usuario usuario = userService.obtenerPorEmail(auth.getName());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
        }
        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre() != null ? usuario.getNombre() : "",
                "email", usuario.getEmail(),
                "avatarUrl", usuario.getUrlFotoPerfil() != null ? usuario.getUrlFotoPerfil() : ""
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> actualizarPerfil(
            @RequestBody Map<String, String> body,
            Authentication auth) {

        Usuario usuario = userService.obtenerPorEmail(auth.getName());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
        }

        String nombre = body.get("nombre");

        // El email no se puede cambiar: está firmado dentro del JWT y cambiarlo
        // invalida todas las sesiones activas del usuario.
        if (nombre != null && !nombre.isBlank()) {
            usuario.setNombre(nombre.trim());
        }

        userRepository.save(usuario);
        return ResponseEntity.ok(Map.of("message", "ok"));
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> subirAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication auth) {
        try {
            Usuario usuario = userService.obtenerPorEmail(auth.getName());
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
            }

            Path uploadsBase = Paths.get(storageLocation).getParent();
            if (uploadsBase == null) uploadsBase = Paths.get(storageLocation);
            Path dir = uploadsBase.resolve("avatars");
            Files.createDirectories(dir);
            String originalName = file.getOriginalFilename();
            int dotIdx = originalName != null ? originalName.lastIndexOf('.') : -1;
            String ext = dotIdx >= 0 ? originalName.substring(dotIdx) : ".jpg";
            String filename = "avatar_" + usuario.getId() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            String url = "https://pagape-api.duckdns.org/uploads/avatars/" + filename;
            usuario.setUrlFotoPerfil(url);
            userRepository.save(usuario);

            return ResponseEntity.ok(Map.of("avatarUrl", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al subir avatar: " + e.getMessage()));
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
