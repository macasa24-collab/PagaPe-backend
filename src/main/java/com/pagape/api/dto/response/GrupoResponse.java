package com.pagape.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
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
    private LocalDateTime fechaUltimoPlan;
    private String imagenUrl;

    public GrupoResponse(Integer id, String nombre, String codigo, String claveAcceso,
                         boolean esPremium, boolean esAdmin, BigDecimal balanceActual,
                         Integer puntosKarma, Integer contPlanesPropuestos, LocalDateTime fechaUltimoPlan) {
        this(id, nombre, codigo, claveAcceso, esPremium, esAdmin, balanceActual,
             puntosKarma, contPlanesPropuestos, fechaUltimoPlan, null);
    }

    public GrupoResponse(Integer id, String nombre, String codigo, String claveAcceso,
                         boolean esPremium, boolean esAdmin, BigDecimal balanceActual,
                         Integer puntosKarma, Integer contPlanesPropuestos,
                         LocalDateTime fechaUltimoPlan, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.claveAcceso = claveAcceso;
        this.esPremium = esPremium;
        this.esAdmin = esAdmin;
        this.balanceActual = balanceActual;
        this.puntosKarma = puntosKarma;
        this.contPlanesPropuestos = contPlanesPropuestos;
        this.fechaUltimoPlan = fechaUltimoPlan;
        this.imagenUrl = imagenUrl;
    }
}
