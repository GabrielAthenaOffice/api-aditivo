package com.formulario.athena.service;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoResponseList;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.model.TemplateType;

import java.util.List;

public interface AditivoService {
    //AditivoResponseDTO createAditivo(AditivoRequestDTO request);
    AditivoResponseList listarTodosAditivos(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    List<AditivoSimpleResponseDTO> listarPorNomeEmpresa(String nome);
    AditivoSimpleResponseDTO deleteAditivo(String aditivoId);

    AditivoContratual findById(String id);

    AditivoResponseDTO criarContato(AditivoRequestDTO dto, TemplateType tipo);            // email/telefone PF/PJ
    AditivoResponseDTO criarContratual(AditivoRequestDTO dto, TemplateType tipo);         // contratual / dois-fiadores
}
