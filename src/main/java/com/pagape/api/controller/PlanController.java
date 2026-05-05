package com.pagape.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.dto.request.PlanRequest;
import com.pagape.api.dto.response.PlanActualResponse;
import com.pagape.api.dto.response.PlanResponse;
import com.pagape.api.model.Plan;
import com.pagape.api.model.Usuario;
import com.pagape.api.service.PlanService;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private UserService usuarioService;

    @PostMapping("/create")
    public ResponseEntity<?> crearPlan(@RequestBody PlanRequest request, Authentication authentication) {
        try {
            // 1. Extraemos la identidad del usuario desde el JWT (Seguridad 100%)
            String emailUsuario = authentication.getName();

            // 2. Buscamos al usuario por su email para obtener su objeto/ID
            Usuario creador = usuarioService.obtenerPorEmail(emailUsuario);

            if (creador == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
            }

            // 3. Llamamos al servicio (que ya tiene la lógica de pertenencia al grupo)
            Plan nuevoPlan = planService.crearNuevoPlan(
                    request.getIdGrupo(),
                    creador.getId(),
                    request.getTitulo(),
                    request.getDescripcion(),
                    request.getFechaPropuesta()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", nuevoPlan.getId());
            response.put("titulo", nuevoPlan.getTitulo());
            response.put("descripcion", nuevoPlan.getDescripcion());
            response.put("fechaPropuesta", nuevoPlan.getFechaPropuesta());
            response.put("idGrupo", nuevoPlan.getGrupo().getId());
            response.put("mensaje", "Plan creado con éxito");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

            // 4. Retornamos el plan creado
        } catch (RuntimeException e) {
            // Capturamos errores de lógica (ej: "No pertenece al grupo") o de validación
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Error genérico por si algo falla en el servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @GetMapping("/group/{idGrupo}")
    public ResponseEntity<List<PlanResponse>> obtenerPlanesPorGrupo(@PathVariable Integer idGrupo) {
        List<PlanResponse> resumen = planService.listarPlanesConVotos(idGrupo);

        if (resumen.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/group/{idGrupo}/current-plan")
    public ResponseEntity<PlanActualResponse> getPlanActual(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(planService.obtenerPlanActual(idGrupo));
    }
}
