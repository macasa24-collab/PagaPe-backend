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
public class PerfilUsuarioGrupoId implements Serializable {

    private Integer idUsuario;
    private Integer idGrupo;
}
