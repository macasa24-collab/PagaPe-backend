package com.pagape.api.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartoDeudaRequest {

    private Integer idUsuarioDeudor;
    private BigDecimal cuotaDebe;
}