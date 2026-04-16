package com.pagape.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pagape.api.dto.request.LoginRequest;
import com.pagape.api.dto.request.RegisterRequest;
import com.pagape.api.model.Usuario;

/**
 * Servicio de Autenticación. Centraliza la lógica de registro de usuarios y
 * validación de credenciales (Login).
 */
@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService; // FALTABA: Para generar los tokens

    @Autowired
    private PasswordEncoder passwordEncoder; // FALTABA: Para verificar/hashear contraseñas

    /**
     * Registra un nuevo usuario, encripta su contraseña y devuelve un token
     * JWT.
     */
    public String registrar(RegisterRequest request) throws Exception {

        // 1. Lógica de negocio: Validar si el email está ocupado
        if (userService.existePorEmail(request.getEmail())) {
            throw new Exception("Error: El email ya está registrado en el sistema.");
        }

        // 2. Mapeo: Convertimos el DTO en la Entidad Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setEmail(request.getEmail());

        // 3. Seguridad: Encriptamos la contraseña con BCrypt antes de guardar
        nuevoUsuario.setContraseñaHash(passwordEncoder.encode(request.getPassword()));

        // 4. Guardamos el usuario a través del UserService
        userService.guardarUsuario(nuevoUsuario);

        // 5. Devolvemos el token para que el usuario quede logueado automáticamente
        return jwtService.generateToken(nuevoUsuario.getEmail());
    }

    /**
     * Verifica las credenciales del usuario y emite un token JWT si son
     * correctas.
     */
    public String login(LoginRequest request) throws Exception {

        // 1. Buscamos al usuario por email
        Usuario usuario = userService.obtenerPorEmail(request.getEmail());
        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }

        // 2. Comparamos la contraseña en texto plano del login con el hash de la BD
        if (!passwordEncoder.matches(request.getPassword(), usuario.getContraseñaHash())) {
            throw new Exception("Contraseña incorrecta.");
        }

        // 3. Si las credenciales son válidas, generamos su "llave" de acceso
        return jwtService.generateToken(usuario.getEmail());
    }
}
