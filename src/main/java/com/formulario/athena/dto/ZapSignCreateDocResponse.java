package com.formulario.athena.dto;

import lombok.Data;

import java.util.List;

@Data
public class ZapSignCreateDocResponse {
    private String token;          // ID do documento na ZapSign
    private String status;         // "pending", etc.
    private String original_file;  // link temporário
    private String signed_file;    // pode vir null
    private List<Signer> signers;

    @Data
    public static class Signer {
        private String token;
        private String sign_url; // use este para enviar ao cliente
        private String status;
        private String name;
        private String email;
        private String phone_country;
        private String phone_number;
    }
}
