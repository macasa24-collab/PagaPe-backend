package com.pagape.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.dto.request.GrupoRequest;
import com.pagape.api.model.Grupo;
import com.pagape.api.service.GrupoService;
import com.pagape.api.service.PerfilUsuarioGrupoService;

@RestController
@RequestMapping("/groups")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private PerfilUsuarioGrupoService perfilService;

    @PostMapping("/create")
    public ResponseEntity<?> crearGrupo(@RequestBody GrupoRequest request) {
        try {
            // 1. Llamamos al servicio para crear el grupo (genera código y guarda en DB)
            Grupo nuevoGrupo = grupoService.crearGrupo(request.getNombre(), request.getClave());

            // 2. Unimos al creador automáticamente usando el método que ya tienes
            // Como el grupo está recién creado (vacío), tu lógica lo hará ADMIN automáticamente
            String resultadoUnion = perfilService.unirseAGrupo(
                    request.getCreadorId(),
                    nuevoGrupo.getCodigoUnico(),
                    request.getClave()
            );

            // 3. Respondemos con el grupo creado (que ya incluye su código único)
            return ResponseEntity.ok(nuevoGrupo);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear grupo: " + e.getMessage());
        }
    }
}
