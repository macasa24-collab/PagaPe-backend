package com.pagape.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.repository.RepartoDeudaRepository;

@Service
public class RepartoDeudaService {

    @Autowired
    private RepartoDeudaRepository repartoDeudaRepository;

    @Transactional
    public void asignarDeudasPersonalizadas(Integer idGasto, List<RepartoDeuda> deudas) {
        // Primero, eliminar cualquier deuda existente para este gasto
        List<RepartoDeuda> existentes = repartoDeudaRepository.findById_IdGasto(idGasto);
        if (!existentes.isEmpty()) {
            repartoDeudaRepository.deleteAll(existentes);
        }

        // Guardar las nuevas deudas
        for (RepartoDeuda deuda : deudas) {
            if (deuda.getCuotaDebe().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La cuota debe ser positiva");
            }
        }
        repartoDeudaRepository.saveAll(deudas);
    }

    public List<RepartoDeuda> obtenerDeudasPorGasto(Integer idGasto) {
        return repartoDeudaRepository.findById_IdGasto(idGasto);
    }

    public RepartoDeuda obtenerDeudaEspecifica(Integer idGasto, Integer idUsuarioDeudor) {
        return repartoDeudaRepository.findById_IdGastoAndId_IdUsuarioDeudor(idGasto, idUsuarioDeudor);
    }
}