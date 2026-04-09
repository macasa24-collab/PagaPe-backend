package com.pagape.api.model;

public class RepartoDeuda {

    private int idGasto;
    private int idUsuarioDeudor;
    private float cuotaDebe;

    public RepartoDeuda() {
    }

    public RepartoDeuda(int idGasto, int idUsuarioDeudor, float cuotaDebe) {
        this.idGasto = idGasto;
        this.idUsuarioDeudor = idUsuarioDeudor;
        this.cuotaDebe = cuotaDebe;
    }

    public int getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(int idGasto) {
        this.idGasto = idGasto;
    }

    public int getIdUsuarioDeudor() {
        return idUsuarioDeudor;
    }

    public void setIdUsuarioDeudor(int idUsuarioDeudor) {
        this.idUsuarioDeudor = idUsuarioDeudor;
    }

    public float getCuotaDebe() {
        return cuotaDebe;
    }

    public void setCuotaDebe(float cuotaDebe) {
        this.cuotaDebe = cuotaDebe;
    }
}
