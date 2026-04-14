package com.pagape.api.model;

public class Liquidacion {

    private Integer id;
    private int idPagador;
    private int idReceptor;
    private int idGrupo;
    private float importe;
    private String concepto;
    private boolean estadoConfirmacion;
    private String metodoPago;

    public Liquidacion() {
    }

    public Liquidacion(int idPagador, int idReceptor, int idGrupo, float importe, String concepto, String metodoPago) {
        this.idPagador = idPagador;
        this.idReceptor = idReceptor;
        this.idGrupo = idGrupo;
        this.importe = importe;
        this.concepto = concepto;
        this.metodoPago = metodoPago;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdPagador() {
        return idPagador;
    }

    public void setIdPagador(int idPagador) {
        this.idPagador = idPagador;
    }

    public int getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(int idReceptor) {
        this.idReceptor = idReceptor;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
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

    public boolean isEstadoConfirmacion() {
        return estadoConfirmacion;
    }

    public void setEstadoConfirmacion(boolean estadoConfirmacion) {
        this.estadoConfirmacion = estadoConfirmacion;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

}
