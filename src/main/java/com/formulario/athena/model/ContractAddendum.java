package com.formulario.athena.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contract_addendums")
public class ContractAddendum {

    @Id
    private String id;

    private ClientData cliente;

    private String contratoSocialPath; // caminho do PDF enviado
    private String zapsignDocumentId;
    private String zapsignLink;
    private String status; // aguardando_assinatura, assinado, etc.

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

