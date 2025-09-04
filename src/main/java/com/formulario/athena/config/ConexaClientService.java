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
    private WebClient webClient;

    public ClientData buscarClientePorNome(String nome) {
        List<CustomerResponse> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/conexa/buscar-por-nome")
                        .queryParam("nome", nome)
                        .build())
                .retrieve()
                .bodyToFlux(CustomerResponse.class)
                .collectList()
                .block(); // <- você pode trocar por reativo depois

        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado no Conexa: " + nome);
        }

        CustomerResponse customer = response.getFirst(); // pega o primeiro

        return ClientData.builder()
                .nome(customer.getName())
                .cpf(extrairCpf(customer))
                .cnpj(extrairCnpj(customer))
                .email(extrairEmail(customer))
                .telefone(extrairTelefone(customer))
                .endereco(extrairEnderecoCompleto(customer))
                .build();
    }

    private String extrairCpf(CustomerResponse customer) {
        // Por enquanto não existe campo de CPF explícito no retorno
        // Pode estar em outro endpoint ou cadastro do Conexa
        if (customer.getLegalPerson() == null) {
            return "CPF-DESCONHECIDO"; // placeholder
        }
        return null; // Se for PJ não terá CPF
    }

    private String extrairCnpj(CustomerResponse customer) {
        return customer.getLegalPerson() != null
                ? customer.getLegalPerson().getCnpj()
                : null;
    }

    private String extrairEmail(CustomerResponse customer) {
        return (customer.getEmailsMessage() != null && !customer.getEmailsMessage().isEmpty())
                ? customer.getEmailsMessage().getFirst()
                : null;
    }

    private String extrairTelefone(CustomerResponse customer) {
        return (customer.getPhones() != null && !customer.getPhones().isEmpty())
                ? customer.getPhones().getFirst()
                : null;
    }

    private String extrairEnderecoCompleto(CustomerResponse customer) {
        if (customer.getAddress() == null) {
            return null;
        }

        CustomerResponse.Address addr = customer.getAddress();

        return String.format("%s, %s - %s, %s/%s, CEP: %s",
                safe(addr.getStreet()),
                safe(addr.getNumber()),
                safe(addr.getNeighborhood()),
                safe(addr.getCity()),
                addr.getState() != null ? addr.getState().getAbbreviation() : "",
                safe(addr.getZipCode()));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
