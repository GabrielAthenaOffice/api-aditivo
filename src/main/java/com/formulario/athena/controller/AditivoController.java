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
    public ResponseEntity<AditivoSimpleResponseDTO> deletarAditivo(@PathVariable Long id) {
        AditivoSimpleResponseDTO aditivoSimpleResponseDTO = aditivoService.deleteAditivo(id);

        return new ResponseEntity<>(aditivoSimpleResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/teste")
    public String teste() {
        System.out.println(">>> ✅ ENDPOINT /teste CHAMADO");
        return "TESTE OK";
    }

    @CrossOrigin(
            origins = { "http://localhost:5173", "http://127.0.0.1:5173", "https://app.athenaoffice.com.br" },
            allowCredentials = "false",
            exposedHeaders = { "Content-Disposition","Content-Length","Content-Type" }
    )
    @GetMapping(
            value = "/{id}/download",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
    public ResponseEntity<byte[]> downloadAditivo(@PathVariable String id) {
        try {
            AditivoContratual aditivo = aditivoService.findById(id);

            byte[] documentoBytes = aditivo.getDocumentoBytes();

            // Se não existe OU veio vazio do DB, regenere agora
            if (documentoBytes == null || documentoBytes.length == 0) {
                log.info("Documento ausente/vazio no DB. Gerando novamente...");
                documentoBytes = documentoService.gerarAditivoContratual(aditivo);
                aditivo.setDocumentoBytes(documentoBytes);
                aditivoRepository.save(aditivo);
            }

            // Nome do arquivo
            String nomeArquivo = String.format("aditivo_%s_%s.docx",
                    aditivo.getPessoaJuridicaNome() != null
                            ? aditivo.getPessoaJuridicaNome().replaceAll("[^a-zA-Z0-9]", "_")
                            : "documento",
                    aditivo.getId());

            String ascii = "filename=\"" + nomeArquivo + "\"";
            String utf8  = "filename*=UTF-8''" + java.net.URLEncoder.encode(nomeArquivo, java.nio.charset.StandardCharsets.UTF_8);

            // NÃO definir manualmente contentLength — deixe o Spring setar
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; " + ascii + "; " + utf8)
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .header("Access-Control-Expose-Headers", "Content-Disposition, Content-Length, Content-Type")
                    .body(documentoBytes);

        } catch (Exception e) {
            log.severe("Erro no download: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
