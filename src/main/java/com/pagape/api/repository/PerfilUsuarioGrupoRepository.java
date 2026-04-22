package com.pagape.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.PerfilUsuarioGrupo;
import com.pagape.api.model.auxiliar_id.PerfilUsuarioGrupoId;

@Repository
public interface PerfilUsuarioGrupoRepository extends JpaRepository<PerfilUsuarioGrupo, PerfilUsuarioGrupoId> {

    // Aquí podrías añadir búsquedas personalizadas si las necesitas luego
    @Query("SELECT p FROM PerfilUsuarioGrupo p WHERE p.id = :pk")
    Optional<PerfilUsuarioGrupo> findByLlaveCompuesta(@Param("pk") PerfilUsuarioGrupoId pk);

    // Buscar por los componentes de la PK (accediendo a los campos dentro del ID)
    @Query("SELECT p FROM PerfilUsuarioGrupo p WHERE p.id.idUsuario = :userId AND p.id.idGrupo = :grupoId")
    Optional<PerfilUsuarioGrupo> findByIdsDirectos(@Param("userId") Integer userId, @Param("grupoId") Integer grupoId);

    long countByIdIdGrupo(Integer idGrupo);
}
