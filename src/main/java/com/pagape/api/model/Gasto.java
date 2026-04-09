package com.pagape.api.model;

public class Gasto {

    private int id;
    private int idPagador;
    private int idPlanOrigen;
    private float importe;
    private String concepto;
    private String urlFotoTicket;

    public Gasto() {
    }

    public Gasto(int idPagador, int idPlanOrigen, float importe, String concepto, String urlFotoTicket) {
        this.idPagador = idPagador;
        this.idPlanOrigen = idPlanOrigen;
        this.importe = importe;
        this.concepto = concepto;
        this.urlFotoTicket = urlFotoTicket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPagador() {
        return idPagador;
    }

    public void setIdPagador(int idPagador) {
        this.idPagador = idPagador;
    }

    public int getIdPlanOrigen() {
        return idPlanOrigen;
    }

    public void setIdPlanOrigen(int idPlanOrigen) {
        this.idPlanOrigen = idPlanOrigen;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getUrlFotoTicket() {
        return urlFotoTicket;
    }

    public void setUrlFotoTicket(String urlFotoTicket) {
        this.urlFotoTicket = urlFotoTicket;
    }

}
