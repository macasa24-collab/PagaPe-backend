package com.pagape.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Grupo;
import com.pagape.api.model.PerfilUsuarioGrupo;
import com.pagape.api.model.Usuario;
import com.pagape.api.model.auxiliar_id.PerfilUsuarioGrupoId;

@Repository
public interface PerfilUsuarioGrupoRepository extends JpaRepository<PerfilUsuarioGrupo, PerfilUsuarioGrupoId> {

    // --- Búsquedas por ID Compuesto o @Query ---
    @Query("SELECT p FROM PerfilUsuarioGrupo p WHERE p.id = :pk")
    Optional<PerfilUsuarioGrupo> findByLlaveCompuesta(@Param("pk") PerfilUsuarioGrupoId pk);

    @Query("SELECT p FROM PerfilUsuarioGrupo p WHERE p.id.idUsuario = :userId AND p.id.idGrupo = :grupoId")
    Optional<PerfilUsuarioGrupo> findByIdsDirectos(@Param("userId") Integer userId, @Param("grupoId") Integer grupoId);

    // --- Métodos de Verificación (SEGURIDAD) ---
    // Usando la navegación de Spring Data sobre tu PK compuesta
    boolean existsByIdIdUsuarioAndIdIdGrupo(Integer idUsuario, Integer idGrupo);

    // --- Consultas de Listado y Conteo ---
    long countByIdIdGrupo(Integer idGrupo);

    List<PerfilUsuarioGrupo> findByUsuarioId(Integer idUsuario);

    List<PerfilUsuarioGrupo> findByGrupoId(Integer grupoId);

    public boolean existsByUsuarioAndGrupo(Usuario usuario, Grupo grupo);
}
