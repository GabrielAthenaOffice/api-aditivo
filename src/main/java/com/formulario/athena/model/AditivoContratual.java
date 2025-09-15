package com.formulario.athena.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "aditivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AditivoContratual {
    @Id
    private String id;

    private Long empresaId; // referência à empresa da API de correspondências

    // Unidade Athena Office
    private String unidadeNome;
    private String unidadeCnpj;
    private String unidadeEndereco;

    // Pessoa física (cliente)
    private String pessoaFisicaNome;
    private String pessoaFisicaCpf;
    private String pessoaFisicaEndereco;

    private LocalDate dataInicioContrato;

    // Pessoa jurídica contratante
    private String pessoaJuridicaNome;
    private String pessoaJuridicaCnpj;
    private String pessoaJuridicaEndereco;

    private String status;
    private String localData;
    private LocalDateTime criadoEm = LocalDateTime.now();
}
