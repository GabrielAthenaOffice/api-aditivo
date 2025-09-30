package com.formulario.athena.controller;

import com.formulario.athena.config.AppConstantes;
import com.formulario.athena.dto.*;
import com.formulario.athena.mapper.AditivoResponseHistoricoDTO;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.repository.AditivoRepository;
import com.formulario.athena.service.AditivoService;
import com.formulario.athena.service.HistoricoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/aditivos")
public class AditivoController {

    @Autowired
    private AditivoService aditivoService;

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private AditivoRepository aditivoRepository;

    @PostMapping
    public ResponseEntity<AditivoResponseDTO> criar(@Valid @RequestBody AditivoRequestDTO dto) {
        System.out.println(">>> Recebido DTO: " + dto);

        AditivoResponseDTO resposta = aditivoService.createAditivo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/empresa/nome/{nome}")
    public ResponseEntity<List<AditivoSimpleResponseDTO>> listarPorEmpresaNome(@PathVariable String nome) {
        List<AditivoSimpleResponseDTO> aditivoSimpleResponseDTO = aditivoService.listarPorNomeEmpresa(nome);

        return new ResponseEntity<>(aditivoSimpleResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/empresa/todas-as-empresas")
    public ResponseEntity<AditivoResponseList> listarTodasAsEmpresas(@RequestParam(name = "pageNumber", defaultValue = AppConstantes.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                     @RequestParam(name = "pageSize", defaultValue = AppConstantes.PAGE_SIZE, required = false) Integer pageSize,
                                                                     @RequestParam(name = "sortBy", defaultValue = AppConstantes.SORT_ADITIVOS_BY) String sortBy,
                                                                     @RequestParam(name = "sortOrder", defaultValue = AppConstantes.SORT_DIR, required = false) String sortOrder){
        AditivoResponseList aditivoResponseList = aditivoService.listarTodosAditivos(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(aditivoResponseList, HttpStatus.OK);
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

    @DeleteMapping("empresa/deletar-aditivo/{id}")
    public ResponseEntity<AditivoSimpleResponseDTO> deletarAditivo(Long id) {
        AditivoSimpleResponseDTO aditivoSimpleResponseDTO = aditivoService.deleteAditivo(id);

        return new ResponseEntity<>(aditivoSimpleResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAditivo(@PathVariable String id) {
        try {
            AditivoContratual aditivo = aditivoService.findById(id);

            File file = new File(aditivo.getCaminhoDocumento());

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(file.getAbsolutePath());
            Resource resource = new InputStreamResource(new FileInputStream(file));

            // Determina o tipo de conteúdo
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"aditivo_" + aditivo.getPessoaJuridicaNome() + ".docx\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
