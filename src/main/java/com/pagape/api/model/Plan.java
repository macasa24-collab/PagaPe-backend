package com.pagape.api.model;

import java.time.LocalDate;

public class Plan {

    private int id;
    private int idGrupo;
    private int idCreador;
    private String titulo;
    private String descripcion;
    private boolean votacionCerrada;
    private LocalDate fechaCreacion;
    private boolean denegado;

    public Plan() {
    }

    public Plan(int idGrupo, int idCreador, String titulo, String descripcion) {
        this.idGrupo = idGrupo;
        this.idCreador = idCreador;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public int getIdCreador() {
        return idCreador;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isVotacionCerrada() {
        return votacionCerrada;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean isDenegado() {
        return denegado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public void setIdCreador(int idCreador) {
        this.idCreador = idCreador;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setVotacionCerrada(boolean votacionCerrada) {
        this.votacionCerrada = votacionCerrada;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setDenegado(boolean denegado) {
        this.denegado = denegado;
    }

}
