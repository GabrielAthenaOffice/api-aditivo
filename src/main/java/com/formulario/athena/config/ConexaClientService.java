package com.formulario.athena.config;

import com.formulario.athena.dto.CustomerResponse;
import com.formulario.athena.model.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ConexaClientService {

    @Autowired
    private WebClient conexaClient;

    public ClientData buscarClientePorNome(String nome) {
        // seu endpoint retorna uma lista; pegue o primeiro ou trate conforme sua regra
        return conexaClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/conexa/buscar-por-nome")
                        .queryParam("nome", nome)
                        .build())
                .retrieve()
                .bodyToFlux(CustomerResponse.class)
                .map(this::mapToClientData)
                .next()
                .block();
    }

    private ClientData mapToClientData(CustomerResponse r) {
        String endereco = null;
        if (r.getAddress() != null) {
            endereco = String.format(
                    "%s, %s - %s, %s - %s, %s",
                    safe(r.getAddress().getStreet()),
                    safe(r.getAddress().getNumber()),
                    safe(r.getAddress().getNeighborhood()),
                    safe(r.getAddress().getCity()),
                    r.getAddress().getState() != null ? safe(r.getAddress().getState().getAbbreviation()) : "",
                    safe(r.getAddress().getZipCode())
            ).replaceAll(",\\s*,", ", ");
        }
        return ClientData.builder()
                .nome(r.getName())
                .cpf(null)            // pode vir do seu formulário
                .cnpj(null)           // idem
                .email(r.getEmailsMessage() != null && !r.getEmailsMessage().isEmpty() ? r.getEmailsMessage().get(0) : null)
                .telefone(r.getPhones() != null && !r.getPhones().isEmpty() ? r.getPhones().get(0) : null)
                .endereco(endereco)
                .build();
    }

    private String safe(String v) { return v == null ? "" : v; }
}