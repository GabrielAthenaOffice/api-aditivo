package com.formulario.athena.controller;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.model.TemplateType;
import com.formulario.athena.service.AditivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private AditivoService aditivoService;

    // 1) Contato: EMAIL
    @PostMapping("/contato/email/pf")
    public ResponseEntity<AditivoResponseDTO> criarEmailPf(@RequestBody @Valid AditivoRequestDTO dto) {
        return created(aditivoService.criarContato(dto, TemplateType.TROCA_EMAIL_PF));
    }

    @PostMapping("/contato/email/pj")
    public ResponseEntity<AditivoResponseDTO> criarEmailPj(@RequestBody @Valid AditivoRequestDTO dto) {
        return created(aditivoService.criarContato(dto, TemplateType.TROCA_EMAIL_PJ));
    }

    // 2) Contato: TELEFONE
    @PostMapping("/contato/telefone/pf")
    public ResponseEntity<AditivoResponseDTO> criarTelefonePf(@RequestBody @Valid AditivoRequestDTO dto) {
        return created(aditivoService.criarContato(dto, TemplateType.TROCA_TEL_PF));
    }

    @PostMapping("/contato/telefone/pj")
    public ResponseEntity<AditivoResponseDTO> criarTelefonePj(@RequestBody @Valid AditivoRequestDTO dto) {
        return created(aditivoService.criarContato(dto, TemplateType.TROCA_TEL_PJ));
    }

    // 3) Contratual (PF + PJ)
    @PostMapping("/contratual")
    public ResponseEntity<AditivoResponseDTO> criarContratual(@RequestBody @Valid AditivoRequestDTO dto) {
        return created(aditivoService.criarContratual(dto, TemplateType.ADITIVO_CONTRATUAL));
    }

    // 4) Contratual com dois fiadores (usa “socio*” do DTO)
    @PostMapping("/contratual/dois-fiadores")
    public ResponseEntity<AditivoResponseDTO> criarContratualDoisFiadores(@RequestBody @Valid AditivoRequestDTO dto) {
        return created(aditivoService.criarContratual(dto, TemplateType.ADITIVO_CONTRATUAL_DOIS_FIADORES));
    }

    private ResponseEntity<AditivoResponseDTO> created(AditivoResponseDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }
}

