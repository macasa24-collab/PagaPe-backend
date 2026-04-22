package com.pagape.api.model;

import java.math.BigDecimal;

import com.pagape.api.model.auxiliar_id.PerfilUsuarioGrupoId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_grupo")
@Data
@NoArgsConstructor
public class PerfilUsuarioGrupo {

    @EmbeddedId
    private PerfilUsuarioGrupoId id;

    @ManyToOne
    @MapsId("idUsuario") // Conecta con el campo de la llave compuesta
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("idGrupo") // Conecta con el campo de la llave compuesta
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;

    @Column(name = "puntuacion_karma")
    private int puntosKarma;

    @Column(name = "contador_planes_propuestos")
    private int contPlanesPropuestos;

    @Column(name = "es_admin")
    private boolean esAdmin;

    @Column(name = "balance_actual")
    private BigDecimal balanceActual;

    public PerfilUsuarioGrupo(Usuario usuario, Grupo grupo) {
        this.id = new PerfilUsuarioGrupoId(usuario.getId(), grupo.getId());
        this.usuario = usuario;
        this.grupo = grupo;
    }

}
