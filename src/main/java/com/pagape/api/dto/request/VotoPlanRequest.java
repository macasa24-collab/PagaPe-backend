package com.pagape.api.dto.request;

import lombok.Data;

@Data
public class VotoPlanRequest {
    private Integer idPlan;
    private String voto; // "A favor" o "En contra"
}

