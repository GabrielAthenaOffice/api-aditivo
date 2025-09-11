package com.formulario.athena.controller;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
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
@RequestMapping("/aditivos")
public class AditivoController {

    @Autowired
    private AditivoService aditivoService;

    @PostMapping
    public ResponseEntity<AditivoResponseDTO> criar(@Valid @RequestBody AditivoRequestDTO dto) {
        AditivoResponseDTO resposta = aditivoService.createAditivo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
