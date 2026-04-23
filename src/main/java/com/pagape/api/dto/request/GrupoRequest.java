package com.pagape.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GrupoRequest {

    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;

    @NotBlank(message = "La clave del grupo es obligatoria")
    @Size(max = 12, message = "La clave debe tener como máximo 12 caracteres")
    private String clave;

}
