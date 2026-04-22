package com.pagape.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GrupoRequest {

    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;

    @NotBlank(message = "La clave del grupo es obligatoria")
    private String clave;

    private Integer creadorId; // El ID del usuario que le da a "Crear Grupo"
}
