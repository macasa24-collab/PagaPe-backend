package com.pagape.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existePorEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Usuario obtenerPorEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Nuevo: necesario para el chat (el payload lleva idUsuario)
    public Usuario obtenerPorId(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        if (existePorEmail(usuario.getEmail())) {
            throw new RuntimeException("Error: El correo " + usuario.getEmail() + " ya está registrado.");
        }
        return userRepository.save(usuario);
    }
}