package com.formulario.athena.service;

import com.formulario.athena.config.ConexaClientService;
import com.formulario.athena.dto.AditivoContratualDTO;
import com.formulario.athena.dto.ZapSignCreateDocResponse;
import com.formulario.athena.model.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class ZapSignService {

    @Autowired
    private WebClient zapSignClient;

    @Autowired
    private ConexaClientService conexaClientService;

    @Value("${zapsign.api.template-id}")
    private String templateId; // ID ou link do template

    public ZapSignCreateDocResponse criarDocumentoViaModelo(AditivoContratualDTO dto) {

        ClientData clientData = conexaClientService.buscarClientePorNome(dto.getContratante());

        // Usando lista mutável para evitar NPE do Map.of
        List<Map<String, String>> data = new ArrayList<>();

        addIfNotNull(data, "{{CONTRATANTE}}", clientData.getNome());
        addIfNotNull(data, "{{CPF}}", clientData.getCpf());
        addIfNotNull(data, "{{ENDEREÇO COMPLETO}}", dto.getEndereco());
        addIfNotNull(data, "{{DATA DE INÍCIO DO CONTRATO}}", dto.getDataInicioContrato());
        addIfNotNull(data, "{{CONTRATANTE PESSOA JURÍDICA}}", dto.getContratantePessoaJuridica());
        addIfNotNull(data, "{{CNPJ}}", clientData.getCnpj());

        Map<String, Object> body = new HashMap<>();
        body.put("template_id", templateId);
        body.put("data", data);
        body.put("signer_name", Optional.ofNullable(clientData.getNome()).orElse("Sem Nome"));
        body.put("signer_email", Optional.ofNullable(clientData.getEmail()).orElse("no-reply@athena.com"));
        body.put("signer_phone_country", "55");
        body.put("signer_phone_number", clientData.getTelefone());
        body.put("lang", "pt-br");
        body.put("send_automatic_email", true);

        return zapSignClient.post()
                .uri("/models/create-doc/") // <- endpoint correto
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ZapSignCreateDocResponse.class)
                .block();
    }

    /**
     * Adiciona um par de substituição no array de placeholders,
     * ignorando valores nulos para evitar NullPointerException.
     */
    private void addIfNotNull(List<Map<String, String>> data, String de, String para) {
        if (para != null) {
            data.add(Map.of("de", de, "para", para));
        } else {
            System.out.println("[ZapSignService] Placeholder ignorado: " + de + " (valor nulo)");
        }
    }
}
