package com.pagape.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.model.auxiliar_id.RepartoDeudaId;

@Repository
public interface RepartoDeudaRepository extends JpaRepository<RepartoDeuda, RepartoDeudaId> {

    List<RepartoDeuda> findById_IdGasto(Integer idGasto);

    // Método para encontrar deudas de un usuario en un gasto específico
    RepartoDeuda findById_IdGastoAndId_IdUsuarioDeudor(Integer idGasto, Integer idUsuarioDeudor);

}