package com.formulario.athena.mapper;

import com.formulario.athena.dto.HistoricoResponseDTO;
import com.formulario.athena.model.AditivoHistorico;

public class HistoricoMapper {
    private HistoricoMapper(){}

    public static HistoricoResponseDTO fromEntityToDTO(AditivoHistorico aditivoHistorico) {
        return new HistoricoResponseDTO(
                aditivoHistorico.getId(),
                aditivoHistorico.getEmpresaId(),
                aditivoHistorico.getEmpresaNome(),
                aditivoHistorico.getAditivoId(),
                aditivoHistorico.getStatus(),
                aditivoHistorico.getMensagem()
        );
    }
}
