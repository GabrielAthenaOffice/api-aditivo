package com.formulario.athena.config;

import com.formulario.athena.model.ClientData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ConexaClientService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ClientData> buscarClientePorNome(String nome) {
        WebClient conexaClient = webClientBuilder.baseUrl("http://localhost:5000").build();

        return conexaClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/conexa/buscar-por-nome")
                        .queryParam("nome", nome)
                        .build())
                .retrieve()
                .bodyToMono(List.class)
                .flatMap(lista -> {

                    if (lista.isEmpty()) {
                        return Mono.error(new RuntimeException("Cliente não encontrado no Conexa"));
                    }

                    Map<String, Object> cliente = (Map<String, Object>) lista.get(0);

                    // Monta o ClientData a partir do JSON retornado
                    return Mono.just(ClientData.builder()
                            .nome((String) cliente.get("name"))
                            .email(((List<String>) cliente.get("emailsMessage")).get(0))
                            .telefone(((List<String>) cliente.get("phones")).get(0))
                            .cpf("123.456.789-00") // Ajustar depois conforme origem real
                            .cnpj("12.345.678/0001-99")
                            .dataInicio("01/09/2025")
                            .build());
                });
    }
}
