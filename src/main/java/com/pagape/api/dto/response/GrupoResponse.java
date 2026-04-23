package com.pagape.api.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrupoResponse {

    private Integer id;
    private String nombre;
    private String codigo;
    private String claveAcceso;
    private boolean esPremium;
    private boolean esAdmin;
    private BigDecimal balanceActual;
    private Integer puntosKarma;
    private Integer contPlanesPropuestos;
}
