package com.pagape.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VotoEstadoResponse {
    private boolean haVotado;
    private String voto; // "A favor", "En contra" o null si no ha votado
}