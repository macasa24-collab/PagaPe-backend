package com.pagape.api.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
