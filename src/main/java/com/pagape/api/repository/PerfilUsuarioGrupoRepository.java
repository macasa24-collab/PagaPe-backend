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

    boolean existsByIdIdUsuarioAndIdIdGrupoAndEsAdminTrue(Integer idUsuario, Integer idGrupo);

    // --- Consultas de Listado y Conteo ---
    long countByIdIdGrupo(Integer idGrupo);

    List<PerfilUsuarioGrupo> findByUsuarioId(Integer idUsuario);

    List<PerfilUsuarioGrupo> findByGrupoId(Integer grupoId);

    public boolean existsByUsuarioAndGrupo(Usuario usuario, Grupo grupo);

    @Query("SELECT COUNT(p) > 0 FROM PerfilUsuarioGrupo p WHERE p.usuario.email = :email AND p.grupo.id = :idGrupo")
    boolean existeUsuarioEnGrupoPorEmail(@Param("email") String email, @Param("idGrupo") Integer idGrupo);

    // Devuelve los FCM tokens de los miembros del grupo, excluyendo al emisor y los que no tienen token
    @Query("SELECT p.usuario.fcmToken FROM PerfilUsuarioGrupo p " +
           "WHERE p.grupo.id = :grupoId " +
           "AND p.usuario.id <> :emisorId " +
           "AND p.usuario.fcmToken IS NOT NULL")
    List<String> findFcmTokensDeGrupoExceptoEmisor(
            @Param("grupoId") Integer grupoId,
            @Param("emisorId") Integer emisorId);
}
