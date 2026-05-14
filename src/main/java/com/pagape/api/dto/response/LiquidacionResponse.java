package com.pagape.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.pagape.api.model.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionResponse {

    private Integer id;
    private Integer idGrupo;
    private Integer idPagador;
    private Integer idReceptor;
    private BigDecimal importe;
    private String concepto;
    private boolean estadoConfirmacion;
    private MetodoPago metodoPago;

    /**
     * IDs de los gastos que quedaron saldados con esta liquidación.
     */
    private List<Integer> idsGastosPagados;
}
