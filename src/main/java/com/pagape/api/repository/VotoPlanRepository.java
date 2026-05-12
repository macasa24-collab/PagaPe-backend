package com.pagape.api.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.VotoPlan;
import com.pagape.api.model.auxiliar_id.VotoPlanId;

@Repository
public interface VotoPlanRepository extends JpaRepository<VotoPlan, VotoPlanId> {

    List<VotoPlan> findByIdIdPlan(Integer idPlan);

    long countById_IdPlan(Integer idPlan);

    long countByIdIdPlanAndVoto(Integer idPlan, String voto);

    @Query("SELECT v.id.idUsuario FROM VotoPlan v WHERE v.id.idPlan = :idPlan AND v.voto = :voto")
    Set<Integer> findIdUsuariosByIdPlanAndVoto(@Param("idPlan") Integer idPlan, @Param("voto") String voto);
}
