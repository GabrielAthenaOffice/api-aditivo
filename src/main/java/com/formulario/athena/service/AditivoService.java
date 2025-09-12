package com.formulario.athena.service;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;

public interface AditivoService {
    AditivoResponseDTO createAditivo(AditivoRequestDTO request);
    AditivoSimpleResponseDTO listarTodosAditivos(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    AditivoSimpleResponseDTO listarPorNomeEmpresa(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String nomeEmpresa);
}
