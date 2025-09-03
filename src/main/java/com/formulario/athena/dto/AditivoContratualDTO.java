package com.formulario.athena.dto;

import lombok.Data;

@Data
public class AditivoContratualDTO {
    private String contratante;                  // nome que será usado para buscar no Conexa
    private String endereco;                     // preenchido pelo usuário
    private String dataInicioContrato;
    private String contratantePessoaJuridica;    // preenchido pelo usuário
}

