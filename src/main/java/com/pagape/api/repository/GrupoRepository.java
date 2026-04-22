package com.pagape.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {

    // Esto generará automáticamente: SELECT * FROM grupos WHERE codigo_unico = ?
    Optional<Grupo> findByCodigoUnico(String codigoUnico);

}
