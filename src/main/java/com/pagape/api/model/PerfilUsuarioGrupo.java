package com.pagape.api.model;

public class PerfilUsuarioGrupo {

    private int idGrupo;
    private int idUsuario;
    private int puntosKarma;
    private int contPlanesPropuestos;
    private boolean esAdmin;
    private float balanceActual;

    public PerfilUsuarioGrupo() {
    }

    public PerfilUsuarioGrupo(int idGrupo, int idUsuario) {
        this.idGrupo = idGrupo;
        this.idUsuario = idUsuario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getPuntosKarma() {
        return puntosKarma;
    }

    public int getContPlanesPropuestos() {
        return contPlanesPropuestos;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public float getBalanceActual() {
        return balanceActual;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setPuntosKarma(int puntosKarma) {
        this.puntosKarma = puntosKarma;
    }

    public void setContPlanesPropuestos(int contPlanesPropuestos) {
        this.contPlanesPropuestos = contPlanesPropuestos;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public void setBalanceActual(float balanceActual) {
        this.balanceActual = balanceActual;
    }

}
