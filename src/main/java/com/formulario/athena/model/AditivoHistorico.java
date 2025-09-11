package com.formulario.athena.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "aditivo_historico")
@Data
public class AditivoHistorico {

    @Id
    private String id;

    private String empresaId;
    private String empresaNome;
    private String aditivoId;

    private String status; // RECEBIDO, PROCESSADO, ENVIADO, ERRO
    private String mensagem;

    private LocalDateTime criadoEm = LocalDateTime.now();
}

