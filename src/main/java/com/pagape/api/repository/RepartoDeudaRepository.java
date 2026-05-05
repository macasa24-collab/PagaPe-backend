package com.pagape.api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.model.auxiliar_id.RepartoDeudaId;

@Repository
public interface RepartoDeudaRepository extends JpaRepository<RepartoDeuda, RepartoDeudaId> {

    List<RepartoDeuda> findById_IdGasto(Integer idGasto);

    // Método para encontrar deudas de un usuario en un gasto específico
    RepartoDeuda findById_IdGastoAndId_IdUsuarioDeudor(Integer idGasto, Integer idUsuarioDeudor);

    @Query("SELECT COALESCE(SUM(r.cuotaDebe), 0) FROM RepartoDeuda r JOIN Gasto g ON r.id.idGasto = g.id WHERE g.pagador.id = :userId AND g.planOrigen.grupo.id = :grupoId")
    BigDecimal sumCuotaDebeWherePagadorIs(@Param("userId") Integer userId, @Param("grupoId") Integer grupoId);

    @Query("SELECT COALESCE(SUM(r.cuotaDebe), 0) FROM RepartoDeuda r JOIN Gasto g ON r.id.idGasto = g.id WHERE r.id.idUsuarioDeudor = :userId AND g.planOrigen.grupo.id = :grupoId")
    BigDecimal sumCuotaDebeWhereDeudorIs(@Param("userId") Integer userId, @Param("grupoId") Integer grupoId);

}