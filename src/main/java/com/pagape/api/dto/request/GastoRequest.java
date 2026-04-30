package com.pagape.api.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GastoRequest {

    private BigDecimal importe;
    private String concepto;
    private String urlFotoTicket;
}
