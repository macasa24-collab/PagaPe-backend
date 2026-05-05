package com.pagape.api.model.auxiliar_id;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartoDeudaId implements Serializable {

    private Integer idGasto;
    private Integer idUsuarioDeudor;
}