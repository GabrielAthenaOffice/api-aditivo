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
import java.util.Optional;

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
     * 1. Busca dados do cliente no Conexa
     * 2. Salva no Mongo
     * 3. Cria documento via modelo na ZapSign
     * 4. Atualiza Mongo com zapsignDocumentId, link e status
     */
    public ContractAddendumResponse criarAddendum(ContractAddendumRequest request) {

        // 🔹 Buscar dados do cliente no Conexa
        Optional<ClientData> clientData = conexaClient.buscarClientePorNome(request.getNome());

        // 🔹 Gera placeholders para o documento (se ainda precisar deles)
        Map<String, String> placeholders = placeholderMapper.gerarPlaceholders(request);

        // 🔹 Monta o objeto inicial para salvar no Mongo
        ContractAddendum addendum = ContractAddendum.builder()
                .cliente(ClientData.builder()
                        .nome(request.getNome())
                        .cpf(request.getCpf())
                        .cnpj(request.getCnpj())
                        .email(request.getEmail())
                        .telefone(clientData.get().getTelefone())
                        .endereco(clientData.get().getEndereco())
                        .build())
                .contratoSocialPath(null) // futuramente arquivo PDF
                .zapsignDocumentId(null)
                .zapsignLink(null)
                .status("aguardando_envio_zapsign")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        ContractAddendum saved = repository.save(addendum);

        // 🔹 Monta DTO para enviar à ZapSign
        AditivoContratualDTO dto = new AditivoContratualDTO();
        dto.setContratante(placeholders.get("CONTRATANTE"));
        dto.setEndereco(placeholders.get("ENDERECO"));
        dto.setDataInicioContrato(placeholders.get("DATA_INICIO"));
        dto.setContratantePessoaJuridica(placeholders.get("CONTRATANTE_PJ"));

        // 🔹 Cria documento via ZapSign
        var zapResponse = zapSignService.criarDocumentoViaModelo(dto);

        // 🔹 Extrai dados da resposta
        String documentId = zapResponse.getToken(); // ID do documento
        String link = zapResponse.getSigners() != null && !zapResponse.getSigners().isEmpty()
                ? zapResponse.getSigners().getFirst().getSign_url()
                : null;

        // 🔹 Atualiza Mongo com informações da ZapSign
        saved.setZapsignDocumentId(documentId);
        saved.setZapsignLink(link);
        saved.setStatus("aguardando_assinatura");
        saved.setAtualizadoEm(LocalDateTime.now());
        repository.save(saved);

        // 🔹 Retorna resposta completa
        return ContractAddendumResponse.builder()
                .id(saved.getId())
                .zapsignDocumentId(saved.getZapsignDocumentId())
                .zapsignLink(saved.getZapsignLink())
                .status(saved.getStatus())
                .nome(saved.getCliente().getNome())
                .cpf(saved.getCliente().getCpf())
                .cnpj(saved.getCliente().getCnpj())
                .email(saved.getCliente().getEmail())
                .endereco(saved.getCliente().getEndereco())
                .dataInicioContrato(dto.getDataInicioContrato())
                .contratantePessoaJuridica(dto.getContratantePessoaJuridica())
                .criadoEm(saved.getCriadoEm())
                .atualizadoEm(saved.getAtualizadoEm())
                .build();
    }

    /**
     * Busca um aditivo por ID e retorna o DTO de resposta (opcional).
     */
    public Optional<ContractAddendumResponse> buscarPorId(String id) {
        return repository.findById(id).map(saved -> {
            ClientData cliente = saved.getCliente();

            return ContractAddendumResponse.builder()
                    .id(saved.getId())
                    .zapsignDocumentId(saved.getZapsignDocumentId())
                    .zapsignLink(saved.getZapsignLink())
                    .status(saved.getStatus())
                    .nome(cliente != null ? cliente.getNome() : null)
                    .cpf(cliente != null ? cliente.getCpf() : null)
                    .cnpj(cliente != null ? cliente.getCnpj() : null)
                    .email(cliente != null ? cliente.getEmail() : null)
                    .endereco(cliente != null ? cliente.getEndereco() : null)
                    // Note: dataInicioContrato / contratantePessoaJuridica só aparecem se
                    // você as salvou na entidade. Se não, retornamos null aqui.
                    .dataInicioContrato(saved.getDataInicioContrato()) // pode ser null
                    .contratantePessoaJuridica(saved.getContratantePessoaJuridica()) // pode ser null
                    .criadoEm(saved.getCriadoEm())
                    .atualizadoEm(saved.getAtualizadoEm())
                    .build();
        });
    }
}
