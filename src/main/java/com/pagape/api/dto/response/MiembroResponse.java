package com.pagape.api.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiembroResponse {

    private Integer usuarioId;
    private String nombre;
    private String email;
    private boolean esAdmin;
    private BigDecimal balance;
}
