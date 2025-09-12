package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AditivoSimpleResponseDTO {
    private Long empresaId;
    private String nomeEmpresa;
    private String status;
    private String mensagem;
    private String aditivoId;
}
