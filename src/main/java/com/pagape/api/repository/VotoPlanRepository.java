package com.pagape.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.VotoPlan;
import com.pagape.api.model.auxiliar_id.VotoPlanId;

@Repository
public interface VotoPlanRepository extends JpaRepository<VotoPlan, VotoPlanId> {

    // Para obtener todos los votos de un plan específico
    List<VotoPlan> findByIdIdPlan(Integer idPlan);

    long countById_IdPlan(Integer idPlan);

    // Para contar cuántos votos hay de cada tipo en un plan
    long countByIdIdPlanAndVoto(Integer idPlan, String voto);
}
