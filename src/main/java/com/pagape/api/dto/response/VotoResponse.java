package com.pagape.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VotoResponse {

    private Integer idUsuario;
    private String nombreUsuario;
    private String voto;
}
