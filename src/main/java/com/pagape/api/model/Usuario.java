package com.pagape.api.model;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String email;
    private ArrayList<Grupo> grupos;
    private int numTokens;
    private String urlFotoPerfil;
    private String contraseñaHash;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String urlFotoPerfil, String contraseñaHash) {
        this.nombre = nombre;
        this.email = email;
        this.grupos = new ArrayList<>();
        this.urlFotoPerfil = urlFotoPerfil;
        this.contraseñaHash = contraseñaHash;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public int getNumTokens() {
        return numTokens;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public String getContraseñaHash() {
        return contraseñaHash;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGrupos(ArrayList<Grupo> grupos) {
        this.grupos = grupos;
    }

    public void setNumTokens(int numTokens) {
        this.numTokens = numTokens;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }

    public void setContraseñaHash(String contraseñaHash) {
        this.contraseñaHash = contraseñaHash;
    }

}
