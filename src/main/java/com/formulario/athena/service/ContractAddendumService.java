package com.formulario.athena.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulario.athena.config.ConexaClientService;
import com.formulario.athena.config.PlaceholderMapper;
import com.formulario.athena.dto.AditivoContratualDTO;
import com.formulario.athena.dto.ContractAddendumRequest;
import com.formulario.athena.dto.ContractAddendumResponse;
import com.formulario.athena.model.ClientData;
import com.formulario.athena.model.ContractAddendum;
import com.formulario.athena.repository.ContractAddendumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ContractAddendumService {

    @Autowired
    private ContractAddendumRepository repository;

    @Autowired
    private PlaceholderMapper placeholderMapper;

    @Autowired
    private ConexaClientService conexaClient;

    @Autowired
    private ZapSignService zapSignService;

    /**
     * Cria um aditivo contratual:
     * 1. Salva no Mongo
     * 2. Cria documento via modelo na ZapSign
     * 3. Atualiza Mongo com zapsignDocumentId, link e status
     */
    public ContractAddendumResponse criarAddendum(ContractAddendumRequest request) {

        // Gera placeholders para o documento
        Map<String, String> placeholders = placeholderMapper.gerarPlaceholders(request);

        // Monta o objeto inicial para salvar no Mongo
        ContractAddendum addendum = ContractAddendum.builder()
                .cliente(ClientData.builder()
                        .nome(request.getNome())
                        .cpf(request.getCpf())
                        .cnpj(request.getCnpj())
                        .email(request.getEmail())
                        .build())
                .contratoSocialPath(null) // futuramente arquivo PDF
                .zapsignDocumentId(null)
                .zapsignLink(null)
                .status("aguardando_envio_zapsign")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Salva inicial no Mongo
        ContractAddendum saved = repository.save(addendum);

        // Monta DTO para enviar à ZapSign
        AditivoContratualDTO dto = new AditivoContratualDTO();
        dto.setContratante(placeholders.get("CONTRATANTE"));
        dto.setEndereco(placeholders.get("ENDERECO"));
        dto.setDataInicioContrato(placeholders.get("DATA_INICIO"));
        dto.setContratantePessoaJuridica(placeholders.get("CONTRATANTE_PJ"));

        // Cria documento via ZapSign
        String zapsignResponse = zapSignService.criarDocumentoViaModelo(dto).block();

        // Extrai dados da resposta (ID e link do documento)
        String documentId = extrairCampo(zapsignResponse, "id");
        String link = extrairCampo(zapsignResponse, "url");

        // Atualiza Mongo com informações da ZapSign
        saved.setZapsignDocumentId(documentId);
        saved.setZapsignLink(link);
        saved.setStatus("aguardando_assinatura");
        saved.setAtualizadoEm(LocalDateTime.now());
        repository.save(saved);

        // Retorna resposta completa
        return ContractAddendumResponse.builder()
                .id(saved.getId())
                .zapsignDocumentId(saved.getZapsignDocumentId())
                .zapsignLink(saved.getZapsignLink())
                .status(saved.getStatus())
                .nome(saved.getCliente().getNome())
                .cpf(saved.getCliente().getCpf())
                .cnpj(saved.getCliente().getCnpj())
                .email(saved.getCliente().getEmail())
                .endereco(placeholders.get("ENDERECO"))
                .dataInicioContrato(placeholders.get("DATA_INICIO"))
                .contratantePessoaJuridica(placeholders.get("CONTRATANTE_PJ"))
                .criadoEm(saved.getCriadoEm())
                .atualizadoEm(saved.getAtualizadoEm())
                .build();
    }

    /**
     * Extrai campos simples de uma resposta JSON da ZapSign
     */
    private String extrairCampo(String json, String campo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            return node.has(campo) ? node.get(campo).asText() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
