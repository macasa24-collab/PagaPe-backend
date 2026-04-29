package com.pagape.api.model;

import com.pagape.api.model.auxiliar_id.VotoPlanId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "votos_planes")
@Data
@NoArgsConstructor
public class VotoPlan {

    @EmbeddedId
    private VotoPlanId id;

    // Mapeamos el Enum para que coincida con 'A favor' / 'En contra'
    @Column(name = "voto", nullable = false)
    private String voto;

    // Relación con Plan
    @ManyToOne
    @MapsId("idPlan") // Esto le dice a JPA que use el campo idPlan de VotoPlanId
    @JoinColumn(name = "id_plan")
    @ToString.Exclude // Evita recursión infinita en toString
    private Plan plan;

    @ManyToOne
    @MapsId("idUsuario") // Esto le dice a JPA que use el campo idUsuario de VotoPlanId
    @JoinColumn(name = "id_usuario")
    @ToString.Exclude // Evita recursión infinita en toString
    private Usuario usuario;

    // Constructor para facilitar la creación de votos
    public VotoPlan(Plan plan, Usuario usuario, String tipoVoto) {
        this.id = new VotoPlanId(plan.getId(), usuario.getId());
        this.plan = plan;
        this.usuario = usuario;
        this.voto = tipoVoto;
    }
}
