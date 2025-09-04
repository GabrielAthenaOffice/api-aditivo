package com.formulario.athena.service;

import com.formulario.athena.config.ConexaClientService;
import com.formulario.athena.config.ZapSignConfig;
import com.formulario.athena.dto.AditivoContratualDTO;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.model.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ZapSignService {

    @Autowired
    private WebClient zapSignWebClient;

    @Autowired
    private ConexaClientService conexaClientService;

    @Value("${zapsign.api.token.model")
    private String templateId;

    public ZapSignService(WebClient.Builder builder,
                          @Value("${zapsign.api.url}") String zapSignUrl,
                          @Value("${zapsign.api.token}") String token,
                          ConexaClientService conexaClientService) {

        this.zapSignWebClient = builder
                .baseUrl(zapSignUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Token " + token)
                .build();
        this.conexaClientService = conexaClientService;
    }

    public Mono<String> criarDocumentoViaModelo(AditivoContratualDTO contratualDTO) {
        // Buscar dados do cliente no Conexa
        ClientData clientData = conexaClientService.buscarClientePorNome(contratualDTO.getContratante());

        if (clientData == null) {
            return Mono.error(new IllegalArgumentException("Cliente não encontrado no Conexa: " + contratualDTO.getContratante()));
        }

        Map<String, Object> requestBody = Map.of(
                "template_id", templateId,
                "signers", List.of(
                        Map.of(
                                "name", clientData.getNome(),
                                "email", clientData.getEmail(),
                                "phone_number", clientData.getTelefone()
                        )
                ),
                "variables", Map.of(
                        "CONTRATANTE", clientData.getNome(),
                        "CPF", clientData.getCpf(),
                        "ENDEREÇO COMPLETO", clientData.getEndereco(),
                        "DATA DE INÍCIO DO CONTRATO", contratualDTO.getDataInicioContrato(),
                        "CONTRATANTE PESSOA JURÍDICA", contratualDTO.getContratantePessoaJuridica(),
                        "CNPJ", clientData.getCnpj()
                )
        );

        return zapSignWebClient.post()
                .uri("/docs")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}