package com.pagape.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.model.Grupo;
import com.pagape.api.model.Plan;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GrupoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.PlanRepository;
import com.pagape.api.repository.UserRepository;

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
}
