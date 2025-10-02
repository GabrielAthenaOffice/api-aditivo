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
    private String unidadeNome; // ter um campo pra selecionar apenas o nome da unidade
    private String unidadeCnpj; // com o nome selecionado, o CNPJ e a unidadeEndereco serão automaticamente
    private String unidadeEndereco;

    // Pessoa física (cliente)
    private String pessoaFisicaNome; // não necessário digitar o nome e nem endereço da pessoa porque vai ser
    private String pessoaFisicaCpf; // puxado automatico do conexa, mas o CPF vamos ter que digitar manualmente
    private String pessoaFisicaEndereco; //

    private LocalDate dataInicioContrato; // necessario digitar manualmente

    // Pessoa jurídica contratante
    private String pessoaJuridicaNome; // necessario digitar manualmente
    private String pessoaJuridicaCnpj; // necessario digitar manualmente
    private String pessoaJuridicaEndereco; // puxado automatico do conexa, ja que é nosso

    private String status; // vai ser preenchido depois que o forms for enviado
    private String localData; // necessario ser preenchido automaticamente
    private LocalDateTime criadoEm = LocalDateTime.now();

    private byte[] documentoBytes;
}
