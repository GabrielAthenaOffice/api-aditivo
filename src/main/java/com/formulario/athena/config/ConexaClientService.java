package com.formulario.athena.config;

import com.formulario.athena.dto.CustomerResponse;
import com.formulario.athena.model.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Component
public class ConexaClientService {

    @Autowired
    private WebClient conexaClient;

    public Optional<ClientData> buscarClientePorNome(String nome) {
        try {
            // Chama o endpoint e recebe a lista completa
            List<CustomerResponse> lista = conexaClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/conexa/buscar-por-nome") // ajuste se necessário
                            .queryParam("nome", nome)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CustomerResponse>>() {})
                    .block();

            // Se lista vazia, retorna Optional.empty()
            if (lista == null || lista.isEmpty()) {
                System.out.println("[ConexaClientService] Nenhum cliente encontrado para: " + nome);
                return Optional.empty();
            }

            // Mapeia o primeiro elemento da lista para ClientData
            return Optional.of(mapToClientData(lista.getFirst()));
        } catch (Exception e) {
            System.err.println("[ConexaClientService] Erro ao buscar cliente: " + e.getMessage());
            return Optional.empty();
        }
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

        String cnpj = null;
        if (r.getLegalPerson() != null) {
            cnpj = r.getLegalPerson().getCnpj();
        }


        return ClientData.builder()
                .nome(r.getName())
                .cpf(r.getCpf())            // pode vir do seu formulário
                .cnpj(cnpj)           // idem
                .email(r.getEmailsMessage() != null && !r.getEmailsMessage().isEmpty() ? r.getEmailsMessage().get(0) : null)
                .telefone(r.getPhones() != null && !r.getPhones().isEmpty() ? r.getPhones().get(0) : null)
                .endereco(endereco)
                .build();
    }

    private String safe(String v) { return v == null ? "" : v; }
}