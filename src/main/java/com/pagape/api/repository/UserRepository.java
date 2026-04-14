package com.pagape.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {
    // Spring Boot leerá este nombre y creará la consulta SQL automáticamente:
    // SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);
}
