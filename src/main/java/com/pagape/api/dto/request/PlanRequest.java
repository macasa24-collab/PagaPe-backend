package com.pagape.api.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PlanRequest {

    private Integer idGrupo;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaPropuesta;
}
