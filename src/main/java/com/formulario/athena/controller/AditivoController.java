package com.formulario.athena.controller;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.model.AditivoHistorico;
import com.formulario.athena.service.AditivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aditivos")
public class AditivoController {

    @Autowired
    private AditivoService aditivoService;

    @PostMapping
    public ResponseEntity<AditivoResponseDTO> criar(@Valid @RequestBody AditivoRequestDTO dto) {
        AditivoResponseDTO resposta = aditivoService.createAditivo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<AditivoContratual>> listarPorEmpresaId(@PathVariable String empresaId) {
        return ResponseEntity.ok(aditivoRepository.findByEmpresaId(empresaId));
    }

    @GetMapping("/empresa/nome/{nome}")
    public ResponseEntity<List<AditivoContratual>> listarPorEmpresaNome(@PathVariable String nome) {
        return ResponseEntity.ok(aditivoRepository.findByEmpresaNomeIgnoreCase(nome));
    }

    @GetMapping("/historico/empresa/{empresaId}")
    public ResponseEntity<List<AditivoHistorico>> historicoPorEmpresaId(@PathVariable String empresaId) {
        return ResponseEntity.ok(historicoRepository.findByEmpresaId(empresaId));
    }

    @GetMapping("/historico/empresa/nome/{nome}")
    public ResponseEntity<List<AditivoHistorico>> historicoPorEmpresaNome(@PathVariable String nome) {
        return ResponseEntity.ok(historicoRepository.findByEmpresaNomeIgnoreCase(nome));
    }

}
