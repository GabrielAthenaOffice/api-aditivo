package com.formulario.athena.service;

import com.formulario.athena.config.exceptions.APIExceptions;
import com.formulario.athena.documents.DocumentoService;
import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoResponseList;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.mapper.AditivoMapper;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.model.AditivoHistorico;
import com.formulario.athena.model.TemplateType;
import com.formulario.athena.repository.AditivoRepository;
import com.formulario.athena.repository.HistoricoRepository;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
public class AditivoServiceImpl implements AditivoService {

    @Autowired
    private AditivoRepository aditivoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Value("${app.public-base-url}")
    private String baseUrl;

    @PostConstruct
    void normalizeBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank() || baseUrl.contains("localhost")) {
            baseUrl = "https://api-aditivo.onrender.com";
        }
        baseUrl = baseUrl.replaceAll("/+$", "");
        System.out.println("PUBLIC_BASE_URL=" + baseUrl);
    }


    @Override
    public AditivoResponseList listarTodosAditivos(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<AditivoContratual> aditivoPage = aditivoRepository.findAll(pageDetails);

        List<AditivoContratual> aditivos = aditivoPage.getContent();

        List<AditivoSimpleResponseDTO> aditivoResponseLists = aditivos.stream()
                .map(AditivoMapper::toSimpleResponse)
                .toList();

        AditivoResponseList aditivoResponseList = new AditivoResponseList();
        aditivoResponseList.setContent(aditivoResponseLists);
        aditivoResponseList.setPageNumber(aditivoPage.getNumber());
        aditivoResponseList.setPageSize(aditivoPage.getSize());
        aditivoResponseList.setTotalElements(aditivoPage.getTotalElements());
        aditivoResponseList.setTotalPages(aditivoPage.getTotalPages());
        aditivoResponseList.setLastPage(aditivoPage.isLast());

        return aditivoResponseList;
    }

    @Override
    public List<AditivoSimpleResponseDTO> listarPorNomeEmpresa(String nomeEmpresa) {
        List<AditivoContratual> aditivoOpt = aditivoRepository.findByPessoaJuridicaNomeContainingIgnoreCase(nomeEmpresa);

        if (aditivoOpt.isEmpty()) {
            return Collections.emptyList();
        }

        List<AditivoSimpleResponseDTO> aditivoSimpleResponseDTO = aditivoOpt.stream()
                .map(AditivoMapper::toSimpleResponse)
                .toList();

        return aditivoSimpleResponseDTO;
    }

    @Override
    public AditivoSimpleResponseDTO deleteAditivo(String aditivoId) {
        Optional<AditivoContratual> aditivoContratual1 = aditivoRepository.findById(aditivoId);

        AditivoContratual aditivoContratual = aditivoContratual1.get();

        if(aditivoContratual == null) {
            throw new APIExceptions("Não existe um aditivo com esse ID");
        }

        aditivoRepository.delete(aditivoContratual);

        return AditivoMapper.toSimpleResponse(aditivoContratual);
    }

    @Override
    public AditivoContratual findById(String id) {
        return aditivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aditivo não encontrado com ID: " + id));
    }


    // ===== Rotas dedicadas

    // --------- contato (email/telefone) ----------
    @Override
    @Transactional
    public AditivoResponseDTO criarContato(AditivoRequestDTO dto, TemplateType tipo) {
        if (!(tipo == TemplateType.TROCA_EMAIL_PF || tipo == TemplateType.TROCA_EMAIL_PJ
                || tipo == TemplateType.TROCA_TEL_PF   || tipo == TemplateType.TROCA_TEL_PJ)) {
            throw new APIExceptions("Template inválido para contato");
        }
        validaPFouPJ(dto, tipo);
        if (tipo == TemplateType.TROCA_EMAIL_PF || tipo == TemplateType.TROCA_EMAIL_PJ)
            req(dto.getEmail(), "email é obrigatório");
        if (tipo == TemplateType.TROCA_TEL_PF || tipo == TemplateType.TROCA_TEL_PJ)
            req(dto.getTelefone(), "telefone é obrigatório");

        Map<String,Object> extras = new HashMap<>();
        if (dto.getEmail()!=null) extras.put("email", dto.getEmail());
        if (dto.getTelefone()!=null) extras.put("telefone", dto.getTelefone());

        return criarComTemplate(dto, tipo, extras);
    }

    // --------- contratual (simples e dois fiadores) ----------
    @Override
    @Transactional
    public AditivoResponseDTO criarContratual(AditivoRequestDTO dto, TemplateType tipo) {
        if (!(tipo == TemplateType.ADITIVO_CONTRATUAL || tipo == TemplateType.ADITIVO_CONTRATUAL_DOIS_FIADORES))
            throw new APIExceptions("Template inválido para contratual");

        // precisa PF + PJ
        req(dto.getPessoaFisicaNome(), "PF nome obrigatório");
        req(dto.getPessoaFisicaCpf(),  "PF CPF obrigatório");
        req(dto.getPessoaJuridicaNome(),"PJ nome obrigatório");
        req(dto.getPessoaJuridicaCnpj(),"PJ CNPJ obrigatório");

        Map<String,Object> extras = new HashMap<>();
        if (tipo == TemplateType.ADITIVO_CONTRATUAL_DOIS_FIADORES) {
            req(dto.getSocio(), "socio obrigatório");
            req(dto.getSocioCpf(), "socioCpf obrigatório");
            req(dto.getSocioEndereco(), "socioEndereco obrigatório");
            extras.put("socio", dto.getSocio());
            extras.put("socioCpf", dto.getSocioCpf());
            extras.put("socioEndereco", dto.getSocioEndereco());
        }
        return criarComTemplate(dto, tipo, extras);
    }

    // --------- núcleo comum com GridFS ----------
    private AditivoResponseDTO criarComTemplate(AditivoRequestDTO dto, TemplateType tipo, Map<String,Object> extras) {
        // DTO -> entidade (sem email/telefone/socio* na entidade)
        AditivoContratual ad = AditivoMapper.toEntity(dto);
        ad.setTemplateNome(tipo.getFileBase());
        ad.setStatus("RECEBIDO");

        // 1º save para obter ID
        ad = aditivoRepository.save(ad);

        // Mapa de placeholders (auditoria) e geração do DOCX
        Map<String,Object> ph = documentoService.montarPlaceholders(ad, tipo, extras);
        byte[] bytes = documentoService.gerarAditivoContratual(ad, tipo, extras);

        // Salva no GridFS
        ObjectId gridId = gridFsTemplate.store(
                new ByteArrayInputStream(bytes),
                "aditivo_" + ad.getId() + ".docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );

        // Atualiza entidade
        ad.setArquivoGridFsId(gridId.toHexString());
        ad.setPlaceholdersUsados(toStringMap(ph));
        ad.setStatus(tipo.getFileBase().toUpperCase());
        ad = aditivoRepository.save(ad);

        // Histórico
        var h = new AditivoHistorico();
        h.setEmpresaId(Optional.ofNullable(ad.getEmpresaId()).map(Object::toString).orElse(dto.getEmpresaId()));
        h.setEmpresaNome(ad.getPessoaJuridicaNome());
        h.setAditivoId(ad.getId());
        h.setStatus("DOCUMENTO_GERADO");
        h.setMensagem(tipo.getFileBase());
        historicoRepository.save(h);

        String url = String.format("%s/aditivos/%s/download", trimRight(baseUrl), ad.getId());
        return new AditivoResponseDTO("SUCESSO", "Aditivo gerado com sucesso", ad.getId(), null, url);
    }

    private Map<String,String> toStringMap(Map<String,Object> in) {
        Map<String,String> out = new HashMap<>();
        in.forEach((k,v) -> out.put(k, v==null? "": String.valueOf(v)));
        return out;
    }

    private void validaPFouPJ(AditivoRequestDTO dto, TemplateType tipo) {
        if (tipo.needsPF()) {
            req(dto.getPessoaFisicaNome(), "PF nome obrigatório");
            req(dto.getPessoaFisicaCpf(),  "PF CPF obrigatório");
        }
        if (tipo.needsPJ()) {
            req(dto.getPessoaJuridicaNome(),"PJ nome obrigatório");
            req(dto.getPessoaJuridicaCnpj(),"PJ CNPJ obrigatório");
        }
    }

    private void req(String v, String msg){ if (v==null || v.isBlank()) throw new APIExceptions(msg); }
    private String trimRight(String s){ return s!=null && s.endsWith("/") ? s.substring(0, s.length()-1) : s; }

}
