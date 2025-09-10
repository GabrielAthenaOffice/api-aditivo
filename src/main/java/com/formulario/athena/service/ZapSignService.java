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

    /**
     * Cria um documento via modelo DOCX do ZapSign.
     */
    public ZapSignCreateDocResponse criarDocumentoViaModelo(AditivoContratualDTO dto) {

        // Buscar dados do cliente via Conexa
        Optional<ClientData> clientData = conexaClientService.buscarClientePorNome(dto.getContratante());
        if (clientData == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + dto.getContratante());
        }

        // Montar lista de placeholders
        List<Map<String, String>> data = new ArrayList<>();
        addIfNotNull(data, "{{CONTRATANTE}}", clientData.get().getNome());
        addIfNotNull(data, "{{CPF}}", clientData.get().getCpf());
        addIfNotNull(data, "{{ENDEREÇO COMPLETO}}", clientData.get().getEndereco() != null ? clientData.get().getEndereco() : dto.getEndereco());
        addIfNotNull(data, "{{DATA DE INÍCIO DO CONTRATO}}", dto.getDataInicioContrato());
        addIfNotNull(data, "{{CONTRATANTE PESSOA JURÍDICA}}", dto.getContratantePessoaJuridica());
        addIfNotNull(data, "{{CNPJ}}", clientData.get().getCnpj());

        // Montar body para enviar ao ZapSign
        Map<String, Object> body = new HashMap<>();
        body.put("template_id", templateId);
        body.put("data", data);

        // Campos obrigatórios do signatário
        String signerName = Optional.ofNullable(clientData.get().getNome()).orElse("Sem Nome");
        String signerEmail = Optional.ofNullable(clientData.get().getEmail()).orElse(null);
        if (signerEmail == null) {
            throw new IllegalArgumentException("E-mail do signatário não pode ser nulo para o ZapSign.");
        }

        body.put("signer_name", signerName);
        body.put("signer_email", signerEmail);
        body.put("lang", "pt-br");
        body.put("send_automatic_email", true);

        // Chamada ao endpoint do ZapSign
        return zapSignClient.post()
                .uri("/models/create-doc/")
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
        if (para != null && !para.isBlank()) {
            data.add(Map.of("de", de, "para", para));
        } else {
            System.out.println("[ZapSignService] Placeholder ignorado: " + de + " (valor nulo ou vazio)");
        }
    }
}
