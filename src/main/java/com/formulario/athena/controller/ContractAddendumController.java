package com.formulario.athena.controller;

import com.formulario.athena.dto.ContractAddendumRequest;
import com.formulario.athena.dto.ContractAddendumResponse;
import com.formulario.athena.service.ContractAddendumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addendums")
public class ContractAddendumController {

    @Autowired
    private ContractAddendumService contractService;

    /**
     * Cria um novo aditivo contratual.
     * Fluxo:
     * 1. Salva no Mongo
     * 2. Cria documento via ZapSign
     * 3. Atualiza Mongo com ID, link e status
     */
    @PostMapping
    public ResponseEntity<ContractAddendumResponse> criar(@Valid @RequestBody ContractAddendumRequest request) {
        ContractAddendumResponse response = contractService.criarAddendum(request);
        return ResponseEntity.ok(response);
    }

}