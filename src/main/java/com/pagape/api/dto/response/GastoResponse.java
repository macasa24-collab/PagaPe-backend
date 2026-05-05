package com.pagape.api.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoResponse {
    private Integer id;
    private UsuarioResponse pagador;
    private BigDecimal importe;
    private String concepto;
    private String urlFotoTicket;
}