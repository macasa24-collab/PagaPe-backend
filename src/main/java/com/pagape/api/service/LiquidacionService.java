package com.pagape.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    /**
     * Registra una liquidación que puede cubrir uno o varios gastos a la vez.
     *
     * Reglas de negocio: - Todos los gastos deben pertenecer al mismo grupo. -
     * Todos los gastos deben tener el mismo pagador (el receptor de esta
     * liquidación). - El usuario autenticado (pagador de la liquidación) no
     * puede ser el mismo que el receptor. - Ninguno de los gastos puede estar
     * ya pagado. - Al finalizar, todos los RepartoDeuda implicados quedan con
     * pagado = true y se crea UNA SOLA Liquidacion con el importe total
     * acumulado.
     *
     * @param idsGastos Lista de IDs de gastos a liquidar.
     * @param pagador Usuario autenticado que realiza el pago.
     * @param concepto Descripción opcional del pago.
     * @param metodoPago Método de pago utilizado.
     * @return La liquidación creada.
     */
    @Transactional
    public Liquidacion registrarPagoDeuda(List<Integer> idsGastos, Usuario pagador, String concepto, MetodoPago metodoPago) {

        if (idsGastos == null || idsGastos.isEmpty()) {
            throw new RuntimeException("Debes indicar al menos un gasto a liquidar.");
        }

        Grupo grupo = null;
        Usuario receptor = null;
        BigDecimal importeTotal = BigDecimal.ZERO;
        List<RepartoDeuda> deudasAMarcar = new ArrayList<>();

        for (Integer idGasto : idsGastos) {

            // 1. Verificar que el gasto existe
            Gasto gasto = gastoRepository.findById(idGasto)
                    .orElseThrow(() -> new RuntimeException("Gasto no encontrado: " + idGasto));

            if (gasto.getPagador() == null) {
                throw new RuntimeException("El gasto " + idGasto + " no tiene pagador asignado.");
            }

            // 2. El usuario autenticado no puede pagarse a sí mismo
            if (gasto.getPagador().getId().equals(pagador.getId())) {
                throw new RuntimeException("No puedes pagarte a ti mismo (gasto " + idGasto + ").");
            }

            // 3. Todos los gastos deben pertenecer al mismo grupo
            Grupo grupoGasto = gasto.getPlanOrigen().getGrupo();
            if (grupo == null) {
                grupo = grupoGasto;
            } else if (!grupo.getId().equals(grupoGasto.getId())) {
                throw new RuntimeException("Todos los gastos deben pertenecer al mismo grupo.");
            }

            // 4. Todos los gastos deben tener el mismo receptor (pagador del gasto)
            Usuario receptorGasto = gasto.getPagador();
            if (receptor == null) {
                receptor = receptorGasto;
            } else if (!receptor.getId().equals(receptorGasto.getId())) {
                throw new RuntimeException("Todos los gastos deben tener el mismo pagador (receptor de la liquidación).");
            }

            // 5. Verificar que el usuario pertenece al grupo
            if (!perfilRepository.existeUsuarioEnGrupoPorEmail(pagador.getEmail(), grupo.getId())) {
                throw new RuntimeException("No eres miembro del grupo asociado al gasto " + idGasto + ".");
            }

            // 6. Obtener el reparto de deuda correspondiente
            RepartoDeuda deuda = repartoDeudaRepository
                    .findById_IdGastoAndId_IdUsuarioDeudor(idGasto, pagador.getId());
            if (deuda == null) {
                throw new RuntimeException("No existe deuda para el gasto " + idGasto + " y el usuario actual.");
            }

            if (deuda.isPagado()) {
                throw new RuntimeException("La deuda del gasto " + idGasto + " ya ha sido pagada.");
            }

            // 7. Acumular importe y registrar deuda a marcar
            importeTotal = importeTotal.add(deuda.getCuotaDebe());
            deudasAMarcar.add(deuda);
        }

        // 8. Crear la liquidación con el importe total acumulado
        String conceptoFinal = concepto != null && !concepto.isBlank()
                ? concepto
                : "Liquidación de " + idsGastos.size() + " gasto(s): " + idsGastos;

        Liquidacion liquidacion = new Liquidacion(grupo, pagador, receptor, importeTotal, conceptoFinal, metodoPago);
        liquidacion.setEstadoConfirmacion(true);
        Liquidacion pagoRegistrado = liquidacionRepository.save(liquidacion);

        // 9. Marcar todas las deudas como pagadas
        for (RepartoDeuda deuda : deudasAMarcar) {
            deuda.setPagado(true);
        }
        repartoDeudaRepository.saveAll(deudasAMarcar);

        // 10. Recalcular balances del grupo
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
