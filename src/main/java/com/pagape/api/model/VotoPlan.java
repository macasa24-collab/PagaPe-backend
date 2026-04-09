package com.pagape.api.model;

public class VotoPlan {

    private int idPlan;
    private int idUsuario;
    private String voto;

    public VotoPlan() {
    }

    public VotoPlan(int idPlan, int idUsuario, String voto) {
        this.idPlan = idPlan;
        this.idUsuario = idUsuario;
        this.voto = voto;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }
}
