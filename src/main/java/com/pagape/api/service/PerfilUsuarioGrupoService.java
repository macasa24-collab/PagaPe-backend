package com.pagape.api.service;

import java.math.BigDecimal;
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
    private VotoPlanService votoPlanService;

    public PerfilUsuarioGrupo guardarPerfil(PerfilUsuarioGrupo perfil) {
        return perfilRepository.save(perfil);
    }

    // Método útil para cuando alguien se une a un grupo
    public String unirseAGrupo(Integer usuarioId, String codigoGrupo, String passwordIngresada) {
        // 1. ¿Existe el usuario?
        Optional<Usuario> usuarioOpt = userRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return "Error: Usuario no encontrado.";
        }

        // 2. ¿Existe el grupo por ese código?
        Optional<Grupo> grupoOpt = grupoRepository.findByCodigoUnico(codigoGrupo);
        if (grupoOpt.isEmpty()) {
            return "Error: El código de grupo no existe.";
        }

        Usuario usuario = usuarioOpt.get();
        Grupo grupo = grupoOpt.get();

        // 3. VERIFICACIÓN DE CONTRASEÑA
        // Comparamos la contraseña ingresada con la que tiene el grupo en la DB
        if (!grupo.getClaveAcceso().equals(passwordIngresada)) {
            return "Error: Contraseña de grupo incorrecta.";
        }

        // 4. ¿Ya pertenece al grupo?
        Optional<PerfilUsuarioGrupo> perfilExistente = perfilRepository.findById(new PerfilUsuarioGrupoId(usuario.getId(), grupo.getId()));
        if (perfilExistente.isPresent()) {
            return "Error: Ya eres miembro de este grupo.";
        }

        // 5. Lógica de Administrador Automático
        long cantidadMiembros = perfilRepository.countByIdIdGrupo(grupo.getId());

        PerfilUsuarioGrupo nuevoPerfil = new PerfilUsuarioGrupo(usuario, grupo);
        nuevoPerfil.setBalanceActual(BigDecimal.ZERO);
        nuevoPerfil.setEsAdmin(cantidadMiembros == 0); // true si es el primero

        perfilRepository.save(nuevoPerfil);

        return "Éxito: Te has unido a '" + grupo.getNombre() + "' como " + (nuevoPerfil.isEsAdmin() ? "ADMIN" : "MIEMBRO");

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
        return perfiles.stream().map(perfil -> new GrupoResponse(
                perfil.getGrupo().getId(),
                perfil.getGrupo().getNombre(),
                perfil.getGrupo().getCodigoUnico(),
                perfil.getGrupo().getClaveAcceso(),
                perfil.getGrupo().isEsPremium(),
                perfil.isEsAdmin(),
                perfil.getBalanceActual(),
                perfil.getPuntosKarma(),
                perfil.getContPlanesPropuestos()
        )).collect(Collectors.toList());
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
                perfil.getBalanceActual()
        )).collect(Collectors.toList());
    }
}
