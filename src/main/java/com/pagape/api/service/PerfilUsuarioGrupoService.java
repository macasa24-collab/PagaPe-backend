package com.pagape.api.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.dto.response.GrupoResponse;
import com.pagape.api.dto.response.MiembroResponse;
import com.pagape.api.model.Grupo;
import com.pagape.api.model.PerfilUsuarioGrupo;
import com.pagape.api.model.Usuario;
import com.pagape.api.model.auxiliar_id.PerfilUsuarioGrupoId;
import com.pagape.api.repository.GrupoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.PlanRepository;
import com.pagape.api.repository.UserRepository;

@Service
public class PerfilUsuarioGrupoService {

    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private VotoPlanService votoPlanService;

    public PerfilUsuarioGrupo guardarPerfil(PerfilUsuarioGrupo perfil) {
        return perfilRepository.save(perfil);
    }

    // Método útil para cuando alguien se une a un grupo
    public String unirseAGrupo(Integer usuarioId, String codigoGrupo, String passwordIngresada) {
        try {
            unirseAGrupoConRespuesta(usuarioId, codigoGrupo, passwordIngresada);
            return "Éxito";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public Grupo unirseAGrupoConRespuesta(Integer usuarioId, String codigoGrupo, String passwordIngresada) {
        Optional<Usuario> usuarioOpt = userRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) throw new RuntimeException("Usuario no encontrado.");

        Optional<Grupo> grupoOpt = grupoRepository.findByCodigoUnico(codigoGrupo);
        if (grupoOpt.isEmpty()) throw new RuntimeException("El código de grupo no existe.");

        Usuario usuario = usuarioOpt.get();
        Grupo grupo = grupoOpt.get();

        if (!grupo.getClaveAcceso().equals(passwordIngresada))
            throw new RuntimeException("Contraseña de grupo incorrecta.");

        Optional<PerfilUsuarioGrupo> perfilExistente = perfilRepository
                .findById(new PerfilUsuarioGrupoId(usuario.getId(), grupo.getId()));
        if (perfilExistente.isPresent()) throw new RuntimeException("Ya eres miembro de este grupo.");

        long cantidadMiembros = perfilRepository.countByIdIdGrupo(grupo.getId());
        PerfilUsuarioGrupo nuevoPerfil = new PerfilUsuarioGrupo(usuario, grupo);
        nuevoPerfil.setBalanceActual(BigDecimal.ZERO);
        nuevoPerfil.setEsAdmin(cantidadMiembros == 0);
        perfilRepository.save(nuevoPerfil);

        return grupo;
    }

    public String salirDelGrupo(Integer usuarioId, Integer grupoId) {
        Optional<PerfilUsuarioGrupo> perfilOpt = perfilRepository.findByIdsDirectos(usuarioId, grupoId);
        if (perfilOpt.isEmpty()) {
            return "Error: No perteneces a este grupo.";
        }

        PerfilUsuarioGrupo perfil = perfilOpt.get();
        boolean eraAdmin = perfil.isEsAdmin();
        String nombreGrupo = perfil.getGrupo().getNombre();

        perfilRepository.delete(perfil);

        if (eraAdmin) {
            List<PerfilUsuarioGrupo> restantes = perfilRepository.findByGrupoId(grupoId);
            if (!restantes.isEmpty()) {
                PerfilUsuarioGrupo nuevoAdmin = restantes.stream()
                        .min(Comparator.comparing(PerfilUsuarioGrupo::getFechaIngreso))
                        .get();
                nuevoAdmin.setEsAdmin(true);
                perfilRepository.save(nuevoAdmin);
            }
        }

        return "Éxito: Has salido del grupo '" + nombreGrupo + "'.";
    }

    public List<GrupoResponse> listarMisGrupos(Integer usuarioId) {
        // 1. Buscamos todas las relaciones del usuario con sus grupos
        List<PerfilUsuarioGrupo> perfiles = perfilRepository.findByUsuarioId(usuarioId);

        System.out.println("DEBUG: Grupos encontrados para el usuario " + usuarioId + ": " + perfiles.size());

        // 2. NUEVO: Limpiamos planes expirados para cada grupo antes de devolver la respuesta
        for (PerfilUsuarioGrupo perfil : perfiles) {
            votoPlanService.evaluarCierrePlanPorFecha(perfil.getGrupo().getId());
        }

        // 2. Transformamos la lista de Entidades a lista de DTOs (GrupoResponse)
        return perfiles.stream().map(perfil -> {
            List<com.pagape.api.model.Plan> planes = planRepository.findUltimoPlanAprobadoPorGrupo(perfil.getGrupo().getId());
            java.time.LocalDateTime fechaUltimoPlan = planes.isEmpty() ? null : planes.get(0).getFechaPropuesta();
            return new GrupoResponse(
                    perfil.getGrupo().getId(),
                    perfil.getGrupo().getNombre(),
                    perfil.getGrupo().getCodigoUnico(),
                    perfil.getGrupo().getClaveAcceso(),
                    perfil.getGrupo().isEsPremium(),
                    perfil.isEsAdmin(),
                    perfil.getBalanceActual(),
                    perfil.getPuntosKarma(),
                    perfil.getContPlanesPropuestos(),
                    fechaUltimoPlan,
                    perfil.getGrupo().getUrlFotoGrupo()
            );
        }).collect(Collectors.toList());
    }

    public List<MiembroResponse> listarMiembrosGrupo(Integer grupoId) {
        // 1. Buscamos todas las relaciones de ese grupo
        List<PerfilUsuarioGrupo> perfiles = perfilRepository.findByGrupoId(grupoId);

        // 2. Mapeamos a nuestro DTO de miembros
        return perfiles.stream().map(perfil -> new MiembroResponse(
                perfil.getUsuario().getId(),
                perfil.getUsuario().getNombre(),
                perfil.getUsuario().getEmail(),
                perfil.isEsAdmin(),
                perfil.getBalanceActual(),
                perfil.getFechaIngreso()
        )).collect(Collectors.toList());
    }
}
