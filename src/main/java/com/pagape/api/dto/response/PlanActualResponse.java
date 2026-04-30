package com.pagape.api.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanActualResponse {

    private boolean tienePlanHoy;
    private Integer idPlan;
    private String titulo;
    private String descripcion;
    private LocalDateTime fecha;
}
