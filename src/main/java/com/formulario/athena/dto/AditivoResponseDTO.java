package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AditivoResponseDTO {
    private String id;
    private Long empresaId;
    private String status;
    private LocalDateTime dataCriacao;
    private String documentoPath;
}
