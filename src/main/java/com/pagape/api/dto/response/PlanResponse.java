package com.pagape.api.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder // El Builder es genial para mapeos manuales
public class PlanResponse {

    private Integer idPlan;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaPropuesta;
    private boolean votacionCerrada;
    private boolean denegado;

    // Datos del creador simplificados
    private String nombreCreador;
    private String urlFotoCreador;

    // Contadores
    private long votosAFavor;
    private long votosEnContra;
}
