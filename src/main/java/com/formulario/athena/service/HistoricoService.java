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
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);
        Page<AditivoHistorico> page = historicoRepository.findAll(pageDetails);

        List<HistoricoResponseDTO> content = page.getContent().stream()
                .map(HistoricoMapper::fromEntityToDTO)
                .toList();

        AditivoResponseHistoricoDTO dto = new AditivoResponseHistoricoDTO();
        dto.setContent(content);
        dto.setPageNumber(page.getNumber());
        dto.setPageSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setLastPage(page.isLast());
        return dto;
    }



    public List<HistoricoResponseDTO> listarHistoricoPorNome(String nomeDaEmpresa) {
        List<AditivoHistorico> aditivoHistorico = historicoRepository.findByEmpresaNomeContainingIgnoreCase(nomeDaEmpresa);


        List<HistoricoResponseDTO> historicoResponseDTOS = aditivoHistorico.stream()
                .map(HistoricoMapper::fromEntityToDTO)
                .toList();

        return historicoResponseDTOS;
    }
}
