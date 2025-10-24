package com.formulario.athena.model;

import lombok.Getter;

import java.util.Set;

@Getter
public enum TemplateType {
    ADITIVO_CONTRATUAL("AditivoContratual",
            Set.of(), // só base
            Set.of("unidadeNome","unidadeCnpj","unidadeEndereco",
                    "pessoaFisicaNome","pessoaFisicaCpf","pessoaFisicaEndereco",
                    "pessoaJuridicaNome","pessoaJuridicaCnpj","pessoaJuridicaEndereco",
                    "dataInicioContrato","localData","dataAtual")),

    ADITIVO_CONTRATUAL_DOIS_FIADORES("AditivoContratualDoisFiadores",
            Set.of("socio","socioCpf","socioEndereco"),
            Set.of(/* base + */ "socio","socioCpf","socioEndereco",
                    "unidadeNome","unidadeCnpj","unidadeEndereco",
                    "pessoaFisicaNome","pessoaFisicaCpf","pessoaFisicaEndereco",
                    "pessoaJuridicaNome","pessoaJuridicaCnpj","pessoaJuridicaEndereco",
                    "dataInicioContrato","localData","dataAtual")),

    TROCA_TEL_PF("AditivoContratualTrocaTelefoneCpf",
            Set.of("telefone","pessoaFisicaNome","pessoaFisicaCpf"),
            Set.of("unidadeNome","unidadeCnpj","unidadeEndereco",
                    "pessoaFisicaNome","pessoaFisicaCpf","pessoaFisicaEndereco","telefone",
                    "dataAtual","localData")),

    TROCA_TEL_PJ("AditivoContratualTrocaTelefoneCnpj",
            Set.of("telefone","pessoaJuridicaNome","pessoaJuridicaCnpj"),
            Set.of("unidadeNome","unidadeCnpj","unidadeEndereco",
                    "pessoaJuridicaNome","pessoaJuridicaCnpj","pessoaJuridicaEndereco","telefone",
                    "dataAtual","localData")),

    TROCA_EMAIL_PF("AditivoTrocaEmailCpf",
            Set.of("email","pessoaFisicaNome","pessoaFisicaCpf"),
            Set.of("unidadeNome","unidadeCnpj","unidadeEndereco",
                    "pessoaFisicaNome","pessoaFisicaCpf","pessoaFisicaEndereco","email",
                    "dataAtual","localData")),

    TROCA_EMAIL_PJ("AditivoTrocaEmailCnpj",
            Set.of("email","pessoaJuridicaNome","pessoaJuridicaCnpj"),
            Set.of("unidadeNome","unidadeCnpj","unidadeEndereco",
                    "pessoaJuridicaNome","pessoaJuridicaCnpj","pessoaJuridicaEndereco","email",
                    "dataAtual","localData"));

    private final String fileBase;
    private final Set<String> required;   // deve existir e vir preenchido
    private final Set<String> allowed;    // whitelist para linter (opcional)

    TemplateType(String f, Set<String> req, Set<String> allowed) {
        this.fileBase = f; this.required = req; this.allowed = allowed;
    }

    public boolean needsPF() {
        return this == TROCA_EMAIL_PF || this == TROCA_TEL_PF
                || this == ADITIVO_CONTRATUAL || this == ADITIVO_CONTRATUAL_DOIS_FIADORES;
    }
    public boolean needsPJ() {
        return this == TROCA_EMAIL_PJ || this == TROCA_TEL_PJ
                || this == ADITIVO_CONTRATUAL || this == ADITIVO_CONTRATUAL_DOIS_FIADORES;
    }
}


