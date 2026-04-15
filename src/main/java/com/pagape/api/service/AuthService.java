package com.pagape.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pagape.api.dto.request.RegisterRequest;
import com.pagape.api.model.Usuario;

@Service
public class AuthService {

    @Autowired
    private UserService userService; // AuthService usa a UserService para tocar la DB

    @Autowired
    private PasswordEncoder passwordEncoder; // Para encriptar las contraseñas

    public Usuario registrar(RegisterRequest request) throws Exception {
        
        // 1. Lógica de negocio: Validar si el email está ocupado
        if (userService.existePorEmail(request.getEmail())) {
            throw new Exception("Error: El email ya está registrado en el sistema.");
        }

        // 2. Mapeo: Convertimos el DTO (lo que llega del móvil) en una Entidad (lo que entiende la DB)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre()); // Asegúrate de que en el DTO sea getNombre o getUsername
        nuevoUsuario.setEmail(request.getEmail());
        
        // 3. SEGURIDAD: Encriptamos la contraseña antes de guardarla
        // En lugar de: nuevoUsuario.setContraseñaHash(request.getPassword());
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());
        nuevoUsuario.setContraseñaHash(passwordEncriptada);

        // 4. Delegamos el guardado real al UserService
        return userService.guardarUsuario(nuevoUsuario);
    }
}