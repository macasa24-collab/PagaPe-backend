package com.pagape.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.dto.response.PlanActualResponse;
import com.pagape.api.dto.response.PlanResponse;
import com.pagape.api.dto.response.VotoResponse;
import com.pagape.api.model.Grupo;
import com.pagape.api.model.Plan;
import com.pagape.api.model.Usuario;
import com.pagape.api.model.VotoPlan;
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

            // 2. Obtener votos detallados por usuario
            List<VotoPlan> votosPlan = votoPlanRepository.findByIdIdPlan(plan.getId());
            List<VotoResponse> votos = votosPlan.stream().map(voto -> VotoResponse.builder()
                    .idUsuario(voto.getUsuario().getId())
                    .nombreUsuario(voto.getUsuario().getNombre())
                    .voto(voto.getVoto())
                    .build())
                    .collect(Collectors.toList());

            // 3. Mapear manualmente a PlanResponse
            return PlanResponse.builder()
                    .idPlan(plan.getId())
                    .titulo(plan.getTitulo())
                    .descripcion(plan.getDescripcion())
                    .fechaPropuesta(plan.getFechaPropuesta())
                    .votacionCerrada(plan.isVotacionCerrada())
                    .denegado(plan.isDenegado())
                    .nombreCreador(plan.getCreador().getNombre())
                    .urlFotoCreador(plan.getCreador().getUrlFotoPerfil())
                    .votosAFavor(aFavor)
                    .votosEnContra(enContra)
                    .votos(votos)
                    .build();
        }).collect(Collectors.toList());
    }

    public PlanActualResponse obtenerPlanActual(Integer idGrupo) {
        LocalDateTime ahora = LocalDateTime.now(); // Hora exacta del sistema
        LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX); // 23:59:59

        // La lista solo traerá planes que NO hayan pasado todavía
        List<Plan> planesRestantes = planRepository.findProximosPlanesAceptadosDelDia(idGrupo, ahora, finDia);

        if (planesRestantes.isEmpty()) {
            return PlanActualResponse.builder().tienePlanHoy(false).build();
        }

        // Al estar ordenados por fecha ASC, el get(0) SIEMPRE será el más cercano al futuro
        Plan proximoPlan = planesRestantes.get(0);

        return PlanActualResponse.builder()
                .tienePlanHoy(true)
                .idPlan(proximoPlan.getId())
                .titulo(proximoPlan.getTitulo())
                .descripcion(proximoPlan.getDescripcion())
                .fecha(proximoPlan.getFechaPropuesta())
                .build();
    }
}
