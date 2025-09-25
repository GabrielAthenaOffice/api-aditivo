package com.formulario.athena.service;

import com.formulario.athena.config.exceptions.APIExceptions;
import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoResponseList;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.mapper.AditivoMapper;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.model.AditivoHistorico;
import com.formulario.athena.repository.AditivoRepository;
import com.formulario.athena.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AditivoServiceImpl implements AditivoService {

    @Autowired
    private AditivoRepository aditivoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Override
    public AditivoResponseDTO createAditivo(AditivoRequestDTO dto) {
        // Converte DTO em entidade
        AditivoContratual aditivo = AditivoMapper.toEntity(dto);
        aditivo.setStatus("ADITIVO CRIADO / AGUARDANDO RESPOSTA");

        System.out.println(">>> Salvando aditivo: " + aditivo);

        AditivoContratual salvo = aditivoRepository.save(aditivo);

        // Salva histórico
        AditivoHistorico historico = new AditivoHistorico();
        historico.setEmpresaId(String.valueOf(salvo.getEmpresaId()));
        historico.setEmpresaNome(salvo.getPessoaJuridicaNome());
        historico.setAditivoId(salvo.getId());
        historico.setStatus("RECEBIDO");
        historico.setMensagem("Aditivo registrado com sucesso");

        historicoRepository.save(historico);

        // Retorno padronizado
        return new AditivoResponseDTO("SUCESSO",
                "Aditivo registrado com sucesso", salvo.getId());
    }

    @Override
    public AditivoResponseList listarTodosAditivos(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<AditivoContratual> aditivoPage = aditivoRepository.findAll(pageDetails);

        List<AditivoContratual> aditivos = aditivoPage.getContent();

        if(aditivos.isEmpty()) {
            throw new APIExceptions("Nenhum aditivo criado até o momento");
        }

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
        List<AditivoContratual> aditivoOpt = aditivoRepository.findByPessoaJuridicaNomeIgnoreCase(nomeEmpresa);

        if (aditivoOpt.isEmpty()) {
            throw new APIExceptions("Aditivo não encontrado para a empresa: " + nomeEmpresa);
        }


        List<AditivoSimpleResponseDTO> aditivoSimpleResponseDTO = aditivoOpt.stream()
                .map(AditivoMapper::toSimpleResponse)
                .toList();

        return aditivoSimpleResponseDTO;
    }

    @Override
    public AditivoSimpleResponseDTO deleteAditivo(Long aditivoId) {
        Optional<AditivoContratual> aditivoContratual1 = aditivoRepository.findById(String.valueOf(aditivoId));

        AditivoContratual aditivoContratual = aditivoContratual1.get();

        if(aditivoContratual == null) {
            throw new APIExceptions("Não existe um aditivo com esse ID");
        }

        aditivoRepository.delete(aditivoContratual);

        return AditivoMapper.toSimpleResponse(aditivoContratual);
    }

}
