package com.pagape.api.model;

import java.time.LocalDate;
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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "grupos")
@Data
@NoArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 60)
    private String nombre;

    @Column(name = "codigo_unico", length = 12, unique = true, nullable = false)
    private String codigoUnico;

    @Column(name = "clave_acceso", length = 12)
    private String claveAcceso;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Evita bucles infinitos
    private List<PerfilUsuarioGrupo> miembros = new ArrayList<>();

    @Transient
    private ArrayList<Plan> planes; // Hace falta mapeo, pero no es necesario para la lógica actual, se puede implementar más adelante

    @Column(name = "es_premium")
    private boolean esPremium;

    @Column(name = "fecha_fin_premium")
    private LocalDate fechaFinPremium;

    @Column(name = "url_foto_grupo", length = 255)
    private String urlFotoGrupo;

    public Grupo(String nombre, String codigoUnico, String claveAcceso) {
        this.nombre = nombre;
        this.codigoUnico = codigoUnico;
        this.claveAcceso = claveAcceso;
        this.urlFotoGrupo = null;
    }

}
