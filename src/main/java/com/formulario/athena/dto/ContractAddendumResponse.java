package com.formulario.athena.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAddendumResponse {

    private String id;                // ID gerado no Mongo
    private String zapsignDocumentId; // ID do documento na ZapSign
    private String zapsignLink;       // Link para assinatura
    private String status;            // aguardando_assinatura, assinado, etc.

    // Informações básicas do cliente
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;

    // Dados adicionais
    private String endereco;
    private String dataInicioContrato;
    private String contratantePessoaJuridica;

    // Metadados
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

