package com.pagape.api.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagape.api.dto.request.VotoPlanRequest;
import com.pagape.api.dto.response.VotoEstadoResponse;
import com.pagape.api.model.Plan;
import com.pagape.api.model.Usuario;
import com.pagape.api.model.VotoPlan;
import com.pagape.api.model.auxiliar_id.VotoPlanId;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.PlanRepository;
import com.pagape.api.repository.UserRepository;
import com.pagape.api.repository.VotoPlanRepository;

@Service
public class VotoPlanService {

    @Autowired
    private VotoPlanRepository votoPlanRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

    @Transactional
    public void registrarVoto(VotoPlanRequest request, String emailUsuario) {
        // 1. Buscamos el usuario real en la DB mediante el email del token
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado."));

        // 2. Buscamos el plan
        Plan plan = planRepository.findById(request.getIdPlan())
                .orElseThrow(() -> new RuntimeException("El plan no existe."));

        // 3. SEGURIDAD: Validamos que el usuario pertenezca al grupo del plan
        boolean esMiembro = perfilRepository.existsByUsuarioAndGrupo(usuario, plan.getGrupo());
        if (!esMiembro) {
            throw new RuntimeException("No tienes permiso para votar: no perteneces a este grupo.");
        }

        // 4. Validamos que la votación no esté cerrada
        if (plan.isVotacionCerrada()) {
            throw new RuntimeException("La votación ya está cerrada.");
        }

        // 5. Creamos la entidad con la clave compuesta
        VotoPlanId idId = new VotoPlanId(plan.getId(), usuario.getId());

        VotoPlan votoPlan = new VotoPlan();
        votoPlan.setId(idId);
        votoPlan.setPlan(plan);
        votoPlan.setUsuario(usuario);
        votoPlan.setVoto(request.getVoto());

        // 6. Guardar (JPA hará un insert o un update si ya existe el voto)
        votoPlanRepository.save(votoPlan);

        // Sincronizamos con la DB para que los conteos de abajo sean exactos
        votoPlanRepository.flush();

        // 6. NUEVO: Evaluar si el plan debe cerrarse con la nueva lógica
        evaluarCierreDePlan(plan);
    }

    private void evaluarCierreDePlan(Plan plan) {
        long totalMiembros = perfilRepository.countByIdIdGrupo(plan.getGrupo().getId());
        long totalVotosEmitidos = votoPlanRepository.countById_IdPlan(plan.getId());
        long votosNo = votoPlanRepository.countByIdIdPlanAndVoto(plan.getId(), "En contra");

        LocalDate hoy = LocalDate.now();
        LocalDate fechaDelPlan = plan.getFechaPropuesta().toLocalDate();

        boolean debeCerrarse = false;
        boolean esDenegado = false;

        // --- REGLA 1: Todos han votado ---
        if (totalVotosEmitidos == totalMiembros) {
            debeCerrarse = true;
            // "si el 50% o menos vota NO, plan_denegado = true"
            // Nota: Si el 50% vota NO y el otro 50% SÍ, el plan se deniega.
            if (votosNo >= (totalVotosEmitidos / 2.0)) {
                esDenegado = true;
            }
        } // --- REGLA 2: Es el mismo día del plan ---
        else if (hoy.isEqual(fechaDelPlan)) {
            debeCerrarse = true;
            double porcentajeParticipacion = (double) totalVotosEmitidos / totalMiembros;

            if (porcentajeParticipacion <= 0.50) {
                // Participación del 50% o menos el mismo día -> Denegado
                esDenegado = true;
            } else {
                // Participación > 50%, pero decidimos por votos NO
                if (votosNo >= (totalVotosEmitidos / 2.0)) {
                    esDenegado = true;
                }
            }
        }

        if (debeCerrarse) {
            plan.setVotacionCerrada(true);
            plan.setDenegado(esDenegado);
            planRepository.save(plan);
        }
    }

    public VotoEstadoResponse verificarVotoUsuario(Integer idPlan, String emailUsuario) {
        // 1. Validar si el plan existe
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new RuntimeException("Error: El plan con ID " + idPlan + " no existe."));

        // 2. Buscar al usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 3. SEGURIDAD: ¿El usuario pertenece al grupo del plan?
        // Si no pertenece, no le damos información (lanzamos error)
        boolean esMiembro = perfilRepository.existsByUsuarioAndGrupo(usuario, plan.getGrupo());
        if (!esMiembro) {
            throw new RuntimeException("Acceso denegado: No perteneces al grupo de este plan.");
        }

        // 4. Si ha pasado los filtros anteriores, buscamos el voto
        VotoPlanId idId = new VotoPlanId(idPlan, usuario.getId());

        return votoPlanRepository.findById(idId)
                .map(voto -> new VotoEstadoResponse(true, voto.getVoto()))
                .orElse(new VotoEstadoResponse(false, null));
    }
}
