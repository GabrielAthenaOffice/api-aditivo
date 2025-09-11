package com.formulario.athena.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AditivoRequestDTO {
    @NotNull
    private Long empresaId;

    @NotBlank
    private String unidadeNome;
    @NotBlank
    private String unidadeCnpj;
    @NotBlank
    private String unidadeEndereco;

    @NotBlank
    private String pessoaFisicaNome;
    @NotBlank
    private String pessoaFisicaCpf;
    @NotBlank
    private String pessoaFisicaEndereco;

    @NotNull
    private LocalDate dataInicioContrato;

    @NotBlank
    private String pessoaJuridicaNome;
    @NotBlank
    private String pessoaJuridicaCnpj;
    private String pessoaJuridicaEndereco;
}
