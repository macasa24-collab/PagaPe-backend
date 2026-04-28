package com.pagape.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // Puedes probar buscar todos los planes de un grupo específico
    List<Plan> findByGrupoId(Integer idGrupo);
}
