package com.pagape.api.model;

import java.math.BigDecimal;

import com.pagape.api.model.auxiliar_id.RepartoDeudaId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reparto_deudas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartoDeuda {

    @EmbeddedId
    private RepartoDeudaId id;

    private BigDecimal cuotaDebe;

    // Constructor para facilitar creación
    public RepartoDeuda(Integer idGasto, Integer idUsuarioDeudor, BigDecimal cuotaDebe) {
        this.id = new RepartoDeudaId(idGasto, idUsuarioDeudor);
        this.cuotaDebe = cuotaDebe;
    }
}
