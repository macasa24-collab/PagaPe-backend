package com.pagape.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gastos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Vinculamos id_pagador con la entidad Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagador", nullable = false)
    private Usuario pagador;

    // Vinculamos id_plan_origen con la entidad Plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan_origen", nullable = false)
    private Plan planOrigen;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @Column(length = 300)
    private String concepto;

    @Column(name = "url_foto_ticket", length = 255)
    private String urlFotoTicket;

    public Gasto(Usuario pagador, Plan planOrigen, BigDecimal importe, String concepto, String urlFotoTicket) {
        this.pagador = pagador;
        this.planOrigen = planOrigen;
        this.importe = importe;
        this.concepto = concepto;
        this.urlFotoTicket = urlFotoTicket;
    }

}
