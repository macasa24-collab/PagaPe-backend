package com.pagape.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Gasto;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {

    // Este método te será MUY útil para cuando quieras listar
    // todos los tickets/gastos que pertenecen a un plan específico.
    List<Gasto> findByPlanOrigenId(Integer idPlan);

    // Opcional: Para saber cuánto ha pagado un usuario en total en la app
    List<Gasto> findByPagadorId(Integer idUsuario);

    List<Gasto> findByPlanOrigen_Grupo_IdAndPlanOrigen_VotacionCerradaTrueAndPlanOrigen_DenegadoFalse(Integer idGrupo);
}
