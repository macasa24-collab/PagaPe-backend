package com.pagape.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagape.api.dto.request.VotoPlanRequest;
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
    }
}