package com.pagape.api.service;

import com.pagape.api.model.Usuario;
import com.pagape.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Método para comprobar si el email ya existe en la DB
    public boolean existePorEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Método simple para guardar el objeto Usuario en MySQL
    public Usuario guardarUsuario(Usuario usuario) {
        return userRepository.save(usuario);
    }

    // En el futuro aquí podrías añadir: buscarPorId, actualizarFoto, etc.
}