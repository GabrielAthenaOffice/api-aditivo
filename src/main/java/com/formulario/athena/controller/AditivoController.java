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
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private GridFsTemplate gridFsTemplate;

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
    public ResponseEntity<AditivoSimpleResponseDTO> deletarAditivo(@PathVariable String id) {
        AditivoSimpleResponseDTO aditivoSimpleResponseDTO = aditivoService.deleteAditivo(id);

        return new ResponseEntity<>(aditivoSimpleResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/teste")
    public String teste() {
        System.out.println(">>> ✅ ENDPOINT /teste CHAMADO");
        return "TESTE OK";
    }

    @GetMapping(
            value = "/{id}/download",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
    public ResponseEntity<byte[]> downloadAditivo(@PathVariable String id) {
        AditivoContratual a = aditivoService.findById(id);

        // Se não existir arquivo (migração/legado), gera agora e sobe pro GridFS:
        if (a.getArquivoGridFsId() == null || a.getArquivoGridFsId().isBlank()) {
            byte[] novo = documentoService.gerarAditivoContratual(a);
            ObjectId gridId = gridFsTemplate.store(new ByteArrayInputStream(novo),
                    "aditivo_"+a.getId()+".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            a.setArquivoGridFsId(gridId.toHexString());
            aditivoRepository.save(a);
        }

        var fsFile = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(a.getArquivoGridFsId())))
        );

        var resource = gridFsTemplate.getResource(fsFile);
        byte[] bytes;
        try (var is = resource.getInputStream(); var bos = new ByteArrayOutputStream()) {
            is.transferTo(bos);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String nome = String.format("aditivo_%s_%s.docx",
                (a.getPessoaJuridicaNome()!=null ? a.getPessoaJuridicaNome().replaceAll("[^a-zA-Z0-9]", "_") : "documento"),
                a.getId());
        String ascii = "filename=\"" + nome + "\"";
        String utf8  = "filename*=UTF-8''" + URLEncoder.encode(nome, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; " + ascii + "; " + utf8)
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .header("Access-Control-Expose-Headers", "Content-Disposition, Content-Length, Content-Type")
                .body(bytes);
    }

}
