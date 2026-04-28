package com.pagape.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.dto.response.PlanResponse;
import com.pagape.api.model.Grupo;
import com.pagape.api.model.Plan;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GrupoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.PlanRepository;
import com.pagape.api.repository.UserRepository;
import com.pagape.api.repository.VotoPlanRepository;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

    @Autowired
    private VotoPlanRepository votoPlanRepository;

    public Plan crearNuevoPlan(Integer idGrupo, Integer idCreador, String titulo, String descripcion, LocalDateTime fechaPropuesta) {

        // 1. Verificación de existencia del Grupo
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RuntimeException("Error: El grupo especificado no existe."));

        // 2. Verificación de existencia del Usuario
        Usuario creador = usuarioRepository.findById(idCreador)
                .orElseThrow(() -> new RuntimeException("Error: El usuario creador no existe."));

        // 3. BLINDAJE DE SEGURIDAD: ¿El usuario pertenece realmente a este grupo?
        // Buscamos el perfil usando la clave compuesta (idUsuario e idGrupo)
        boolean esMiembro = perfilRepository.existsByIdIdUsuarioAndIdIdGrupo(idCreador, idGrupo);

        if (!esMiembro) {
            throw new RuntimeException("Acceso Denegado: No puedes crear planes en un grupo al que no perteneces.");
        }

        // 4. Validación de datos de entrada (Business Rules)
        if (fechaPropuesta.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Error: La fecha del plan no puede ser en el pasado.");
        }

        // 5. Construcción y Persistencia
        Plan nuevoPlan = new Plan(grupo, creador, titulo, descripcion, fechaPropuesta);

        return planRepository.save(nuevoPlan);
    }

    public List<Plan> obtenerPlanesPorGrupo(Integer idGrupo) {
        return planRepository.findByGrupoId(idGrupo);
    }

    public List<PlanResponse> listarPlanesConVotos(Integer idGrupo) {
        List<Plan> planes = planRepository.findByGrupoId(idGrupo);

        return planes.stream().map(plan -> {
            // 1. Contar votos
            long aFavor = votoPlanRepository.countByIdIdPlanAndVoto(plan.getId(), "A favor");
            long enContra = votoPlanRepository.countByIdIdPlanAndVoto(plan.getId(), "En contra");

            // 2. Mapear manualmente a PlanResponse
            return PlanResponse.builder()
                    .idPlan(plan.getId())
                    .titulo(plan.getTitulo())
                    .descripcion(plan.getDescripcion())
                    .fechaPropuesta(plan.getFechaPropuesta())
                    .votacionCerrada(plan.isVotacionCerrada())
                    .nombreCreador(plan.getCreador().getNombre())
                    .urlFotoCreador(plan.getCreador().getUrlFotoPerfil())
                    .votosAFavor(aFavor)
                    .votosEnContra(enContra)
                    .build();
        }).collect(Collectors.toList());
    }
}
