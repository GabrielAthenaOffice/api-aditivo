package com.formulario.athena.service;

import com.formulario.athena.config.exceptions.APIExceptions;
import com.formulario.athena.dto.HistoricoResponseDTO;
import com.formulario.athena.mapper.AditivoResponseHistoricoDTO;
import com.formulario.athena.mapper.HistoricoMapper;
import com.formulario.athena.model.AditivoHistorico;
import com.formulario.athena.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;


    public AditivoResponseHistoricoDTO listarHistoricos(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<AditivoHistorico> aditivoHistoricoPage = historicoRepository.findAll(pageDetails);

        List<AditivoHistorico> aditivoHistoricos = aditivoHistoricoPage.getContent();

        if(aditivoHistoricos.isEmpty()) {
            throw new APIExceptions("Nenhum aditivo criado até o momento");
        }

        List<HistoricoResponseDTO> historicoResponseDTOS = aditivoHistoricos.stream()
                .map(HistoricoMapper::fromEntityToDTO)
                .toList();

        AditivoResponseHistoricoDTO aditivoResponseHistoricoDTO = new AditivoResponseHistoricoDTO();
        aditivoResponseHistoricoDTO.setContent(historicoResponseDTOS);
        aditivoResponseHistoricoDTO.setPageNumber(aditivoHistoricoPage.getNumber());
        aditivoResponseHistoricoDTO.setPageSize(aditivoHistoricoPage.getSize());
        aditivoResponseHistoricoDTO.setTotalElements(aditivoResponseHistoricoDTO.getTotalElements());
        aditivoResponseHistoricoDTO.setTotalPages(aditivoResponseHistoricoDTO.getTotalPages());
        aditivoResponseHistoricoDTO.setLastPage(aditivoResponseHistoricoDTO.isLastPage());

        return aditivoResponseHistoricoDTO;

    }


    public List<HistoricoResponseDTO> listarHistoricoPorNome(String nomeDaEmpresa) {
        List<AditivoHistorico> aditivoHistorico = historicoRepository.findByEmpresaNomeIgnoreCase(nomeDaEmpresa);

        if(aditivoHistorico.isEmpty()) {
            throw new APIExceptions("Nenhum aditivo criado para esta empresa: " + nomeDaEmpresa);
        }

        List<HistoricoResponseDTO> historicoResponseDTOS = aditivoHistorico.stream()
                .map(HistoricoMapper::fromEntityToDTO)
                .toList();

        return historicoResponseDTOS;
    }
}
