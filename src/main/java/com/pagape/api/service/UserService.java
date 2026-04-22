package com.pagape.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Comprueba si el email ya existe en la DB. Útil para validaciones durante
     * el registro.
     */
    public boolean existePorEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Busca un usuario por su email. Es fundamental para el proceso de Login.
     */
    public Usuario obtenerPorEmail(String email) {
        // Buscamos en el repo y si no existe devolvemos null 
        // (El AuthService se encargará de lanzar la excepción si es null)
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Guarda el objeto Usuario en MySQL.
     */
    public Usuario guardarUsuario(Usuario usuario) {
        // 1. Verificamos si el email ya está registrado usando tu método existente
        if (existePorEmail(usuario.getEmail())) {
            // Aquí podrías lanzar una excepción personalizada, de momento lanzamos una genérica
            throw new RuntimeException("Error: El correo " + usuario.getEmail() + " ya está registrado.");
        }

        // 2. Si no existe, procedemos a guardar
        return userRepository.save(usuario);
    }

    // --- Futuros métodos ---
    // public Usuario buscarPorId(Integer id) { ... }
    // public void eliminarUsuario(Integer id) { ... }
}
