package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AditivoRequestDTO {

    private String empresaId;

    private String unidadeNome;
    private String unidadeCnpj;
    private String unidadeEndereco;

    private String pessoaFisicaNome;
    private String pessoaFisicaCpf;
    private String pessoaFisicaEndereco;

    private String dataInicioContrato;

    private String pessoaJuridicaNome;
    private String pessoaJuridicaCnpj;
    private String pessoaJuridicaEndereco;

    private String localData;

    //APENAS DTO
    private String templateNome;
    private String email;
    private String telefone;

    private String socio;
    private String socioCpf;
    private String socioEndereco;
}
