package com.formulario.athena.mapper;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.dto.AditivoSimpleResponseDTO;
import com.formulario.athena.model.AditivoContratual;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        return AditivoContratual.builder()
                .empresaId(parseLongSafe(dto.getEmpresaId()))
                .unidadeNome(dto.getUnidadeNome())
                .unidadeCnpj(dto.getUnidadeCnpj())
                .unidadeEndereco(dto.getUnidadeEndereco())

                .pessoaFisicaNome(dto.getPessoaFisicaNome())
                .pessoaFisicaCpf(dto.getPessoaFisicaCpf())
                .pessoaFisicaEndereco(dto.getPessoaFisicaEndereco())

                .dataInicioContrato(parseDateSafe(dto.getDataInicioContrato())) // << AQUI

                .pessoaJuridicaNome(dto.getPessoaJuridicaNome())
                .pessoaJuridicaCnpj(dto.getPessoaJuridicaCnpj())
                .pessoaJuridicaEndereco(dto.getPessoaJuridicaEndereco())

                .localData(dto.getLocalData())
                .build();
    }

    private static Long parseLongSafe(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Long.parseLong(v); } catch (NumberFormatException e) { return null; }
    }

    // aceita "yyyy-MM-dd" (input <input type="date"> do front) e "dd/MM/yyyy"
    private static LocalDate parseDateSafe(String s) {
        if (s == null || s.isBlank()) return null; // não obriga para templates de contato
        try {
            return LocalDate.parse(s); // ISO: yyyy-MM-dd
        } catch (Exception e) {
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ex) {
                throw new RuntimeException("dataInicioContrato inválida. Use yyyy-MM-dd ou dd/MM/yyyy");
            }
        }
    }
}

