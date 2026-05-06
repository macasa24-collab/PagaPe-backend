package com.pagape.api.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.dto.response.DeudaUsuarioResponse;
import com.pagape.api.model.Gasto;
import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GastoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.RepartoDeudaRepository;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/debts")
@CrossOrigin(origins = "*")
public class DeudaController {

    @Autowired
    private UserService usuarioService;

    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

    @Autowired
    private RepartoDeudaRepository repartoDeudaRepository;

    @Autowired
    private GastoRepository gastoRepository;

    /**
     * Devuelve las deudas del usuario autenticado dentro de un grupo específico.
     * El usuario se toma del token JWT en Authentication, no de la URL.
     */
    @GetMapping("/my-debts/{groupId}")
    public ResponseEntity<?> obtenerDeudasUsuarioEnGrupo(
            @PathVariable Integer groupId,
            Authentication authentication) {
        try {
            // Obtener el email del usuario a partir del token JWT
            String email = authentication.getName();
            Usuario usuarioAutenticado = usuarioService.obtenerPorEmail(email);

            // Si no se encuentra el usuario, no está autorizado
            if (usuarioAutenticado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Usuario no válido para esta operación.");
            }

            // Verificar que el usuario pertenece al grupo solicitado
            boolean esMiembro = perfilRepository.existeUsuarioEnGrupoPorEmail(email, groupId);
            if (!esMiembro) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No eres miembro de este grupo.");
            }

            // Buscar todas las deudas donde el usuario autenticado es deudor en ese grupo
            List<RepartoDeuda> deudas = repartoDeudaRepository.findByUsuarioDeudorAndGrupo(usuarioAutenticado.getId(), groupId);

            // Mapear las deudas a la respuesta esperada por el frontend
            List<DeudaUsuarioResponse> response = deudas.stream()
                    .map(d -> {
                        Gasto gasto = gastoRepository.findById(d.getId().getIdGasto()).orElse(null);
                        if (gasto == null) {
                            return null;
                        }
                        return new DeudaUsuarioResponse(
                                d.getId().getIdGasto(),
                                gasto.getPlanOrigen().getId(),
                                gasto.getPagador().getId(),
                                d.getCuotaDebe());
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
