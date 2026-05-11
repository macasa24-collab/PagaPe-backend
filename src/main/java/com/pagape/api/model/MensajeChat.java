package com.pagape.api.model;

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
@Table(name = "mensajes_chat")
@Data
@NoArgsConstructor
public class MensajeChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(length = 1000, nullable = false)
    private String texto;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime timestamp;

    public MensajeChat(Grupo grupo, Usuario usuario, String texto) {
        this.grupo     = grupo;
        this.usuario   = usuario;
        this.texto     = texto;
        this.timestamp = LocalDateTime.now();
    }
}