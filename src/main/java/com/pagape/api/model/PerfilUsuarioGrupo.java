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

}
