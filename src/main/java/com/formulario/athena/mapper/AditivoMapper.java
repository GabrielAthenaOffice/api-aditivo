package com.formulario.athena.mapper;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.model.AditivoContratual;

public class AditivoMapper {

    private AditivoMapper() {
        // construtor privado para não instanciar
    }

    public static AditivoSimpleResponseDTO toSimpleResponse(AditivoContratual aditivo) {
        return new AditivoSimpleResponseDTO(
                aditivo.getEmpresaId(),
                aditivo.getPessoaJuridicaNome(),
                aditivo.getStatus(),
                aditivo.getId()
        );
    }

    public static AditivoResponseDTO toResponse(String status, String mensagem, String aditivoId) {
        return new AditivoResponseDTO(
                status,
                mensagem,
                aditivoId
        );
    }

    public static AditivoContratual toEntity(AditivoRequestDTO dto) {
        AditivoContratual aditivo = new AditivoContratual();
        aditivo.setEmpresaId(Long.valueOf(dto.getEmpresaId()));

        aditivo.setUnidadeNome(dto.getUnidadeNome());
        aditivo.setUnidadeCnpj(dto.getUnidadeCnpj());
        aditivo.setUnidadeEndereco(dto.getUnidadeEndereco());

        aditivo.setPessoaFisicaNome(dto.getPessoaFisicaNome());
        aditivo.setPessoaFisicaCpf(dto.getPessoaFisicaCpf());
        aditivo.setPessoaFisicaEndereco(dto.getPessoaFisicaEndereco());

        aditivo.setDataInicioContrato(dto.getDataInicioContrato());

        aditivo.setPessoaJuridicaNome(dto.getPessoaJuridicaNome());
        aditivo.setPessoaJuridicaCnpj(dto.getPessoaJuridicaCnpj());
        aditivo.setPessoaJuridicaEndereco(dto.getPessoaJuridicaEndereco());

        aditivo.setLocalData(dto.getLocalData());

        return aditivo;
    }
}

