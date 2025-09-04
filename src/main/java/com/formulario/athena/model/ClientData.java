package com.formulario.athena.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientData {
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String dataInicio;
}

