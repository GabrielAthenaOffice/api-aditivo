package com.formulario.athena.service;

import com.formulario.athena.config.CorrespondenciaClient;
import com.formulario.athena.config.PlaceholderMapper;
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
    private CorrespondenciaClient correspondenciaClient;

    public ContractAddendumResponse criarAddendum(ContractAddendumRequest request) {
        // Busca dados do cliente na API de correspondências
        Map<String, Object> dadosCorrespondencia = null;
        if (request.getNomeEmpresa() != null) {
            dadosCorrespondencia = correspondenciaClient.buscarDadosEmpresa(request.getNomeEmpresa());
        }

        // gera placeholders com base no request
        Map <String, String> placeholders = placeholderMapper.gerarPlaceholders(request, dadosCorrespondencia);
        System.out.println("Placeholders montados: " + placeholders);

        // aqui ainda só salva no banco com status inicial
        ContractAddendum addendum = ContractAddendum.builder()
                .cliente(ClientData.builder()
                        .nome(request.getNome())
                        .cpf(request.getCpf())
                        .cnpj(request.getCnpj())
                        .email(request.getEmail())
                        .build())
                .status("aguardando_envio_zapsign")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        ContractAddendum saved = repository.save(addendum);

        return ContractAddendumResponse.builder()
                .id(saved.getId())
                .status(saved.getStatus())
                .build();
    }
}
