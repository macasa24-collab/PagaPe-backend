package com.pagape.api.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String nombre;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "numero_tokens", columnDefinition = "INT DEFAULT 0")
    private int numTokens;

    @Column(name = "url_foto_perfil", length = 255)
    private String urlFotoPerfil;

    @Column(name = "contraseña_hash", length = 255)
    private String contraseñaHash;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Importante para evitar bucles infinitos con Lombok
    private List<PerfilUsuarioGrupo> perfiles = new ArrayList<>();

    public Usuario(String nombre, String email, String contraseñaHash) {
        this.nombre = nombre;
        this.email = email;
        this.urlFotoPerfil = null;
        this.contraseñaHash = contraseñaHash;
    }

}
