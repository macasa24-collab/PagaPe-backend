package com.pagape.api.controller;

import java.util.List;
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

import com.pagape.api.dto.request.LiquidacionRequest;
import com.pagape.api.dto.response.LiquidacionResponse;
import com.pagape.api.dto.response.RepartoDeudaResponse;
import com.pagape.api.model.Liquidacion;
import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.RepartoDeudaRepository;
import com.pagape.api.service.LiquidacionService;
import com.pagape.api.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    private LiquidacionService liquidacionService;

    /**
     * Devuelve las deudas del usuario autenticado dentro de un grupo
     * específico. El usuario se toma del token JWT en Authentication, no de la
     * URL.
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
            List<RepartoDeudaResponse> response = deudas.stream()
                    .map(d -> new RepartoDeudaResponse(
                    d.getId().getIdGasto(),
                    d.getId().getIdUsuarioDeudor(),
                    d.getCuotaDebe(),
                    d.isPagado()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<?> pagarDeudaIndividual(
            @RequestBody LiquidacionRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario pagador = usuarioService.obtenerPorEmail(email);

            if (pagador == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Usuario no válido para esta operación.");
            }

            Liquidacion liquidacion = liquidacionService.registrarPagoDeuda(
                    request.getIdGasto(),
                    pagador,
                    request.getConcepto(),
                    request.getMetodoPago());

            LiquidacionResponse response = new LiquidacionResponse(
                    liquidacion.getId(),
                    liquidacion.getGrupo().getId(),
                    liquidacion.getPagador().getId(),
                    liquidacion.getReceptor().getId(),
                    liquidacion.getImporte(),
                    liquidacion.getConcepto(),
                    liquidacion.isEstadoConfirmacion(),
                    liquidacion.getMetodoPago()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
