package com.formulario.athena.controller;

import com.formulario.athena.dto.AditivoContratualDTO;
import com.formulario.athena.service.ZapSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/aditivos")
public class AditivoController {

    @Autowired
    private ZapSignService zapSignService;

    @PostMapping
    public ResponseEntity<Mono<String>> criarAditivo(@RequestBody AditivoContratualDTO dto) {
        Mono<String> jsonResponse = zapSignService.criarDocumentoViaModelo(dto);

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

}
