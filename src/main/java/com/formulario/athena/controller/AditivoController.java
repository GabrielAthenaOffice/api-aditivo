package com.formulario.athena.controller;

import com.formulario.athena.config.AppConstantes;
import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.dto.HistoricoResponseDTO;
import com.formulario.athena.mapper.AditivoResponseHistoricoDTO;
import com.formulario.athena.service.AditivoService;
import com.formulario.athena.service.HistoricoService;
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

    @Autowired
    private HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<AditivoResponseDTO> criar(@Valid @RequestBody AditivoRequestDTO dto) {
        AditivoResponseDTO resposta = aditivoService.createAditivo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/empresa/nome/{nome}")
    public ResponseEntity<List<AditivoSimpleResponseDTO>> listarPorEmpresaNome(@PathVariable String nome) {
        List<AditivoSimpleResponseDTO> aditivoSimpleResponseDTO = aditivoService.listarPorNomeEmpresa(nome);

        return new ResponseEntity<>(aditivoSimpleResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/historico")
    public ResponseEntity<AditivoResponseHistoricoDTO> listarTodoOHistorico(@RequestParam(name = "pageNumber", defaultValue = AppConstantes.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                            @RequestParam(name = "pageSize", defaultValue = AppConstantes.PAGE_SIZE, required = false) Integer pageSize,
                                                                            @RequestParam(name = "sortBy", defaultValue = AppConstantes.SORT_HISTORICO_BY) String sortBy,
                                                                            @RequestParam(name = "sortOrder", defaultValue = AppConstantes.SORT_DIR, required = false) String sortOrder) {
        AditivoResponseHistoricoDTO aditivoResponseHistoricoDTO = historicoService.listarHistoricos(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(aditivoResponseHistoricoDTO, HttpStatus.FOUND);
    }

    @GetMapping("/historico/empresa/nome/{nome}")
    public ResponseEntity<List<HistoricoResponseDTO>> historicoPorEmpresaNome(@PathVariable String nome) {
        List<HistoricoResponseDTO> historicoResponseDTOS = historicoService.listarHistoricoPorNome(nome);

        return new ResponseEntity<>(historicoResponseDTOS, HttpStatus.FOUND);
    }

}
