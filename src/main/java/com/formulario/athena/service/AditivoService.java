package com.formulario.athena.service;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;

public interface AditivoService {
    AditivoResponseDTO createAditivo(AditivoRequestDTO request);
}
