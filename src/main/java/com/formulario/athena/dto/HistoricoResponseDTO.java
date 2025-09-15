package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoResponseDTO {

    private String id;
    private String empresaId;
    private String empresaNome;
    private String aditivoId;
    private String status; // RECEBIDO, PROCESSADO, ENVIADO, ERRO
    private String mensagem;
}
