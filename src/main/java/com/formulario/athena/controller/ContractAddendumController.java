package com.formulario.athena.controller;

import com.formulario.athena.dto.ContractAddendumRequest;
import com.formulario.athena.dto.ContractAddendumResponse;
import com.formulario.athena.service.ContractAddendumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addendums")
public class ContractAddendumController {

    @Autowired
    private ContractAddendumService contractService;

    /**
     * Cria um novo aditivo contratual:
     * - Salva no Mongo
     * - Cria documento na ZapSign
     * - Atualiza status
     */
    @PostMapping
    public ResponseEntity<ContractAddendumResponse> criar(@Valid @RequestBody ContractAddendumRequest request) {
        ContractAddendumResponse response = contractService.criarAddendum(request);
        return ResponseEntity.ok(response);
    }

    /**
     * (Opcional) Endpoint para buscar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractAddendumResponse> buscarPorId(@PathVariable String id) {
        // Se quiser, podemos montar um mapper que converte a entidade para Response.
        // Aqui só um esqueleto:
        return contractService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}