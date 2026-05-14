package com.pagape.api.dto.request;

import java.util.List;

import com.pagape.api.model.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionRequest {

    private List<Integer> idsGastos; // Lista de IDs de gastos que el usuario quiere pagar en esta liquidación. Pueden ser uno o varios gastos, pero todos deben pertenecer al mismo pagador (receptor de la liquidación)

    private String concepto;

    private MetodoPago metodoPago;
}
