package com.pagape.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagape.api.model.Gasto;
import com.pagape.api.model.Grupo;
import com.pagape.api.model.Liquidacion;
import com.pagape.api.model.MetodoPago;
import com.pagape.api.model.PerfilUsuarioGrupo;
import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GastoRepository;
import com.pagape.api.repository.LiquidacionRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.RepartoDeudaRepository;

@Service
public class LiquidacionService {

    @Autowired
    private LiquidacionRepository liquidacionRepository;

    @Autowired
    private RepartoDeudaRepository repartoDeudaRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

    @Transactional
    public Liquidacion registrarPagoDeuda(Integer idGasto, Usuario pagador, String concepto, MetodoPago metodoPago) {
        Gasto gasto = gastoRepository.findById(idGasto)
                .orElseThrow(() -> new RuntimeException("Gasto inexistente"));

        if (gasto.getPagador() == null) {
            throw new RuntimeException("El gasto no tiene pagador asignado");
        }

        if (gasto.getPagador().getId().equals(pagador.getId())) {
            throw new RuntimeException("No puedes pagarte a ti mismo");
        }

        RepartoDeuda deuda = repartoDeudaRepository.findById_IdGastoAndId_IdUsuarioDeudor(idGasto, pagador.getId());
        if (deuda == null) {
            throw new RuntimeException("No existe deuda para este gasto y usuario");
        }

        if (deuda.isPagado()) {
            throw new RuntimeException("Esta deuda ya ha sido pagada");
        }

        Grupo grupo = gasto.getPlanOrigen().getGrupo();
        if (!perfilRepository.existeUsuarioEnGrupoPorEmail(pagador.getEmail(), grupo.getId())) {
            throw new RuntimeException("No eres miembro del grupo asociado a esta deuda");
        }

        Usuario receptor = gasto.getPagador();
        Liquidacion liquidacion = new Liquidacion(
                grupo,
                pagador,
                receptor,
                deuda.getCuotaDebe(),
                concepto != null ? concepto : "Pago de deuda del gasto " + idGasto,
                metodoPago
        );
        liquidacion.setEstadoConfirmacion(true);

        deuda.setPagado(true);
        repartoDeudaRepository.save(deuda);

        Liquidacion pagoRegistrado = liquidacionRepository.save(liquidacion);
        actualizarBalances(grupo.getId());

        return pagoRegistrado;
    }

    private void actualizarBalances(Integer grupoId) {
        List<PerfilUsuarioGrupo> miembros = perfilRepository.findByGrupoId(grupoId);
        for (PerfilUsuarioGrupo perfil : miembros) {
            BigDecimal totalOwedToThem = repartoDeudaRepository.sumCuotaDebeWherePagadorIs(perfil.getUsuario().getId(), grupoId);
            BigDecimal totalTheyOwe = repartoDeudaRepository.sumCuotaDebeWhereDeudorIs(perfil.getUsuario().getId(), grupoId);
            perfil.setBalanceActual(totalOwedToThem.subtract(totalTheyOwe));
        }
        perfilRepository.saveAll(miembros);
    }
}
