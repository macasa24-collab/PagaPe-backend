package com.pagape.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinGrupoRequest {

    @NotBlank(message = "El código único del grupo es obligatorio")
    private String codigoUnico;

    @NotBlank(message = "La clave del grupo es obligatoria")
    private String clave;

}
