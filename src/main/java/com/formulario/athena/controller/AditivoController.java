package com.formulario.athena.controller;

import com.formulario.athena.config.AppConstantes;
import com.formulario.athena.documents.DocumentoService;
import com.formulario.athena.dto.*;
import com.formulario.athena.mapper.AditivoResponseHistoricoDTO;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.repository.AditivoRepository;
import com.formulario.athena.service.AditivoService;
import com.formulario.athena.service.HistoricoService;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aditivos")
@Log
public class AditivoController {

    @Autowired
    private AditivoService aditivoService;

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private AditivoRepository aditivoRepository;

    @Autowired
    private DocumentoService documentoService;

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

        return new ResponseEntity<>(aditivoResponseHistoricoDTO, HttpStatus.OK);
    }

    @GetMapping("/historico/empresa/nome/{nome}")
    public ResponseEntity<List<HistoricoResponseDTO>> historicoPorEmpresaNome(@PathVariable String nome) {
        List<HistoricoResponseDTO> historicoResponseDTOS = historicoService.listarHistoricoPorNome(nome);

        return new ResponseEntity<>(historicoResponseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("empresa/deletar-aditivo/{id}")
    public ResponseEntity<AditivoSimpleResponseDTO> deletarAditivo(Long id) {
        AditivoSimpleResponseDTO aditivoSimpleResponseDTO = aditivoService.deleteAditivo(id);

        return new ResponseEntity<>(aditivoSimpleResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadAditivo(@PathVariable String id) {
        try {
            AditivoContratual aditivo = aditivoService.findById(id);

            byte[] documentoBytes = aditivo.getDocumentoBytes();

            // ✅ VERIFICA se o campo existe ou é vazio
            if (documentoBytes == null || documentoBytes.length == 0) {
                // ✅ GERA o documento na hora se não existir
                log.info("Documento não encontrado no banco, gerando agora...");
                documentoBytes = documentoService.gerarAditivoContratual(aditivo);

                // ✅ SALVA no banco para futuras requisições
                aditivo.setDocumentoBytes(documentoBytes);
                aditivoRepository.save(aditivo);
            }

            // Gera nome do arquivo
            String nomeArquivo = String.format("aditivo_%s_%s.docx",
                    aditivo.getPessoaJuridicaNome() != null ?
                            aditivo.getPessoaJuridicaNome().replaceAll("[^a-zA-Z0-9]", "_") : "documento",
                    aditivo.getId());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + nomeArquivo + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .contentLength(documentoBytes.length)
                    .body(documentoBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
