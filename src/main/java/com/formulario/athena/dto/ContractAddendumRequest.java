package com.formulario.athena.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAddendumRequest {

    @NotBlank
    private String nome;
    @NotBlank
    private String cpf;
    @NotBlank
    private String cnpj;
    @NotBlank
    private String email;

    // futuramente multipart para contrato social
    // Novo campo: nome da empresa na API de correspondências
    private String nomeEmpresa;
}
