package com.formulario.athena.controller;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.service.AditivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aditivos")
public class AditivoController {

    @Autowired
    private AditivoService aditivoService;

    @PostMapping
    public ResponseEntity<AditivoResponseDTO> createAditivo(@Valid @RequestBody AditivoRequestDTO request) {
        AditivoResponseDTO response = aditivoService.createAditivo(request);
        return ResponseEntity.ok(response);
    }
}
