package com.pagape.api.model;

import java.math.BigDecimal;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "liquidaciones")
@Check(constraints = "id_pagador <> id_receptor AND importe > 0")
@Data
@NoArgsConstructor
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    @ToString.Exclude
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "id_pagador", nullable = false)
    @ToString.Exclude
    private Usuario pagador;

    @ManyToOne
    @JoinColumn(name = "id_receptor", nullable = false)
    @ToString.Exclude
    private Usuario receptor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @Column(length = 300)
    private String concepto;

    @Column(name = "estado_confirmacion")
    private boolean estadoConfirmacion = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20, nullable = false)
    private MetodoPago metodoPago;

    public Liquidacion(Grupo grupo, Usuario pagador, Usuario receptor, BigDecimal importe, String concepto, MetodoPago metodoPago) {
        this.grupo = grupo;
        this.pagador = pagador;
        this.receptor = receptor;
        this.importe = importe;
        this.concepto = concepto;
        this.metodoPago = metodoPago;
    }

}
