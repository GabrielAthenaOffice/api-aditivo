package com.formulario.athena.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "aditivos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AditivoContratual {
    @Id
    private String id;

    private String contratante;               // {{CONTRATANTE}}
    private String cpf;                       // {{CPF}}
    private String enderecoCompleto;          // {{ENDEREÇO COMPLETO}}
    private String dataInicioContrato;        // {{DATA DE INÍCIO DO CONTRATO}}
    private String contratantePessoaJuridica; // {{CONTRATANTE PESSOA JURÍDICA}}
    private String cnpj;                      // {{CNPJ}}

    private String documentoGerado;           // Texto final com substituições
    private LocalDateTime criadoEm = LocalDateTime.now();
}
