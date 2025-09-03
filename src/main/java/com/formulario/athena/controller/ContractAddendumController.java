package com.formulario.athena.controller;

import com.formulario.athena.config.ConexaClientService;
import com.formulario.athena.dto.AditivoContratualDTO;
import com.formulario.athena.dto.ContractAddendumRequest;
import com.formulario.athena.dto.ContractAddendumResponse;
import com.formulario.athena.model.ClientData;
import com.formulario.athena.service.ContractAddendumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/addendums")
public class ContractAddendumController {

    @Autowired
    private ContractAddendumService contractService;

    @Autowired
    private ConexaClientService conexaClient;

    @PostMapping
    public ResponseEntity<ContractAddendumResponse> criar(@Valid @RequestBody ContractAddendumRequest request) {
        ContractAddendumResponse formsPreenchido = contractService.criarAddendum(request);

        return ResponseEntity.ok(formsPreenchido);
    }

    @PostMapping("/criar")
    public Mono<String> criarAditivo(@RequestBody AditivoContratualDTO dto) {
        // Busca cliente no Conexa
        ClientData clientData = conexaClient.buscarClientePorNome(dto.getContratante());

        // Cria documento via modelo na ZapSign
        return zapSignService.criarDocumentoViaModelo(dto, clientData);
    }
}
