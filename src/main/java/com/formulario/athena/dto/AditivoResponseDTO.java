package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AditivoResponseDTO {
    private String status;
    private String mensagem;
    private String aditivoId;
}
