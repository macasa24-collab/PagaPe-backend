package com.pagape.api.dto.response;

import java.math.BigDecimal;

public class DeudaUsuarioResponse {
    private Integer idGasto;
    private Integer idPlan;
    private Integer idUsuarioPagador;
    private BigDecimal cuotaDebe;

    public DeudaUsuarioResponse(Integer idGasto, Integer idPlan, Integer idUsuarioPagador, BigDecimal cuotaDebe) {
        this.idGasto = idGasto;
        this.idPlan = idPlan;
        this.idUsuarioPagador = idUsuarioPagador;
        this.cuotaDebe = cuotaDebe;
    }

    // Getters and setters
    public Integer getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(Integer idGasto) {
        this.idGasto = idGasto;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public Integer getIdUsuarioPagador() {
        return idUsuarioPagador;
    }

    public void setIdUsuarioPagador(Integer idUsuarioPagador) {
        this.idUsuarioPagador = idUsuarioPagador;
    }

    public BigDecimal getCuotaDebe() {
        return cuotaDebe;
    }

    public void setCuotaDebe(BigDecimal cuotaDebe) {
        this.cuotaDebe = cuotaDebe;
    }
}