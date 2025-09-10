package com.formulario.athena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long customerId;
    private String name;
    private String firstName;
    private String cpf;
    private Boolean isActive;
    private List<String> emailsMessage;
    private List<String> phones;
    private Address address;
    private LegalPerson legalPerson;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String number;
        private String neighborhood;
        private String city;
        private State state;
        private String zipCode;
        private String additionalDetails;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class State {
            private Long id;
            private String name;
            private String abbreviation;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LegalPerson {
        private String cnpj;
        private String companyName;
    }
}

