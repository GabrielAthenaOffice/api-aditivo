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
    public Mono<ResponseEntity<String>> criarAditivo(@RequestBody AditivoContratualDTO dto) {
        return zapSignService.criarDocumentoViaModelo(dto)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()))
                )
                .onErrorResume(Exception.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar aditivo: " + ex.getMessage()))
                );
    }

}
