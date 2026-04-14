package com.pagape.api.model;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String nombre;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Transient
    private ArrayList<Grupo> grupos; //Hace falta terminar hibernate de esta parte

    @Column(name = "numero_tokens", columnDefinition = "INT DEFAULT 0")
    private int numTokens;

    @Column(name = "url_foto_perfil", length = 255)
    private String urlFotoPerfil;

    @Column(name = "contraseña_hash", length = 255)
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

    public Integer getId() {
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

    public void setId(Integer id) {
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
