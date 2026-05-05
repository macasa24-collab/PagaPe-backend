package com.pagape.api.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartoDeudaResponse {

    private Integer idGasto;
    private Integer idUsuarioDeudor;
    private BigDecimal cuotaDebe;
}
