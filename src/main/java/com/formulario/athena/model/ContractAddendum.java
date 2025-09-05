package com.formulario.athena.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "contract_addendums")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAddendum {
    @Id
    private String id;

    private ClientData cliente;
    private String contratoSocialPath;
    private String zapsignDocumentId;
    private String zapsignLink;
    private String status;

    // novos campos
    private String dataInicioContrato;
    private String contratantePessoaJuridica;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}


