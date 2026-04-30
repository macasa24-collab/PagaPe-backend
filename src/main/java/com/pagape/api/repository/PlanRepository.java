package com.pagape.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // Puedes probar buscar todos los planes de un grupo específico
    List<Plan> findByGrupoId(Integer idGrupo);

    List<Plan> findByGrupoIdAndVotacionCerradaFalse(Integer idGrupo);

    // Busca planes aceptados que caigan entre dos horas (timempo actual y fin del día)
    @Query("SELECT p FROM Plan p WHERE p.grupo.id = :idGrupo "
            + "AND p.votacionCerrada = true "
            + "AND p.denegado = false "
            + "AND p.fechaPropuesta BETWEEN :ahora AND :fin "
            + "ORDER BY p.fechaPropuesta ASC")
    List<Plan> findProximosPlanesAceptadosDelDia(
            @Param("idGrupo") Integer idGrupo,
            @Param("ahora") LocalDateTime ahora, // Hora actual
            @Param("fin") LocalDateTime fin // 23:59:59
    );
}
