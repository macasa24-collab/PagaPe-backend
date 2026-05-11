package com.pagape.api.dto.request;

import com.pagape.api.model.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionRequest {

    private Integer idGasto;
    private String concepto;
    private MetodoPago metodoPago;
}
