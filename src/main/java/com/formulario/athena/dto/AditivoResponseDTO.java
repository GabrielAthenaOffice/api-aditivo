package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AditivoResponseDTO {
    private String status;
    private String mensagem;
    private String aditivoId;
    private String caminhoDocumentoDocx;
    private String urlDownload; // Nova campo - URL para download

    public AditivoResponseDTO(String status, String mensagem, String aditivoId) {
        this.status = status;
        this.mensagem = mensagem;
        this.aditivoId = aditivoId;
    }
}
