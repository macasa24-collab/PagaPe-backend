package com.pagape.api.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Grupo {

    private Integer id;
    private String nombre;
    private String codigoUnico;
    private String claveAcceso;
    private ArrayList<PerfilUsuarioGrupo> usuarios;
    private ArrayList<Plan> planes;
    private boolean esPremium;
    private LocalDate fechaFinPremium;
    private String urlFotoGrupo;

    public Grupo() {
    }

    public Grupo(String nombre, String codigoUnico, String claveAcceso, String urlFotoGrupo) {
        this.nombre = nombre;
        this.codigoUnico = codigoUnico;
        this.claveAcceso = claveAcceso;
        this.urlFotoGrupo = urlFotoGrupo;
        this.usuarios = new ArrayList<>();
        this.planes = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public ArrayList<PerfilUsuarioGrupo> getUsuarios() {
        return usuarios;
    }

    public ArrayList<Plan> getPlanes() {
        return planes;
    }

    public boolean isEsPremium() {
        return esPremium;
    }

    public LocalDate getFechaFinPremium() {
        return fechaFinPremium;
    }

    public String getUrlFotoGrupo() {
        return urlFotoGrupo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public void setUsuarios(ArrayList<PerfilUsuarioGrupo> usuarios) {
        this.usuarios = usuarios;
    }

    public void setPlanes(ArrayList<Plan> planes) {
        this.planes = planes;
    }

    public void setEsPremium(boolean esPremium) {
        this.esPremium = esPremium;
    }

    public void setFechaFinPremium(LocalDate fechaFinPremium) {
        this.fechaFinPremium = fechaFinPremium;
    }

    public void setUrlFotoGrupo(String urlFotoGrupo) {
        this.urlFotoGrupo = urlFotoGrupo;
    }

}
