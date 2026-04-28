package com.pagape.api.model.auxiliar_id;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@Data
@NoArgsConstructor
public class VotoPlanId implements Serializable {

    private int idPlan;
    private int idUsuario;
}
