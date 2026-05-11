package com.pagape.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChatDTO {

    private Integer    idUsuario;
    private String     nombreUsuario;
    private Integer    idGrupo;
    private String     texto;
    private LocalDateTime timestamp;
}