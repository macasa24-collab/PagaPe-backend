package com.pagape.api.service;

import com.pagape.api.dto.request.RegisterRequest;
import com.pagape.api.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService; // AuthService usa a UserService para tocar la DB

    public Usuario registrar(RegisterRequest request) throws Exception {
        
        // 1. Lógica de negocio: Validar si el email está ocupado
        if (userService.existePorEmail(request.getEmail())) {
            throw new Exception("Error: El email ya está registrado en el sistema.");
        }

        // 2. Mapeo: Convertimos el DTO (lo que llega del móvil) en una Entidad (lo que entiende la DB)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre()); // Asegúrate de que en el DTO sea getNombre o getUsername
        nuevoUsuario.setEmail(request.getEmail());
        
        // 3. Seguridad: Aquí es donde irá el hash de BCrypt en la Historia 4.
        // De momento, guardamos la pass tal cual para que puedas probar el flujo.
        nuevoUsuario.setContraseñaHash(request.getPassword()); 

        // 4. Delegamos el guardado real al UserService
        return userService.guardarUsuario(nuevoUsuario);
    }
}