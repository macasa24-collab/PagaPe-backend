package com.pagape.api.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeudaResumenResponse {

    private Integer idPagador;
    private String nombrePagador;
    private BigDecimal totalDebido;
}
