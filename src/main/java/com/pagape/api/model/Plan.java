package com.pagape.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "planes")
@Data
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;

    @Column(length = 100)
    private String titulo;

    @Column(length = 200)
    private String descripcion;

    @Column(name = "votacion_cerrada")
    private boolean votacionCerrada;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "plan_denegado")
    private boolean denegado;

    @Column(name = "fecha_propuesta")
    private LocalDateTime fechaPropuesta;

    public Plan(Grupo grupo, Usuario creador, String titulo, String descripcion, LocalDateTime fechaPropuesta) {
        this.grupo = grupo;
        this.creador = creador;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaPropuesta = fechaPropuesta;
        this.fechaCreacion = LocalDate.now();
    }
}
