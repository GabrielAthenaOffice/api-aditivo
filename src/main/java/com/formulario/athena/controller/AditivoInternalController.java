package com.formulario.athena.controller;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.model.TemplateType;
import com.formulario.athena.service.AditivoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aditivos/internal")
@Slf4j
public class AditivoInternalController {

    @Autowired
    private AditivoService aditivoService;

    // 🔥 ENDPOINT INTERNO PARA 1 FIADOR
    @PostMapping("/contratual")
    public ResponseEntity<AditivoResponseDTO> criarContratualInterno(
            @RequestBody @Valid AditivoRequestDTO dto) {

        log.info("📥 Recebida requisição INTERNA - Aditivo Contratual (1 fiador)");
        log.info("🏢 Empresa: {}, Unidade: {}", dto.getEmpresaId(), dto.getUnidadeNome());

        AditivoResponseDTO response = aditivoService.criarContratual(dto, TemplateType.ADITIVO_CONTRATUAL);

        log.info("✅ Aditivo contratual criado via serviço interno: {}", response.getAditivoId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔥 ENDPOINT INTERNO PARA 2 FIADORES
    @PostMapping("/contratual/dois-fiadores")
    public ResponseEntity<AditivoResponseDTO> criarContratualDoisFiadoresInterno(
            @RequestBody @Valid AditivoRequestDTO dto) {

        log.info("📥 Recebida requisição INTERNA - Aditivo Contratual (2 fiadores)");
        log.info("🏢 Empresa: {}, Unidade: {}", dto.getEmpresaId(), dto.getUnidadeNome());
        log.info("👥 Sócio incluído: {}", dto.getSocio());

        AditivoResponseDTO response = aditivoService.criarContratual(dto, TemplateType.ADITIVO_CONTRATUAL_DOIS_FIADORES);

        log.info("✅ Aditivo com 2 fiadores criado via serviço interno: {}", response.getAditivoId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
