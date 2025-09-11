package com.formulario.athena.service;

import com.formulario.athena.dto.AditivoRequestDTO;
import com.formulario.athena.dto.AditivoResponseDTO;
import com.formulario.athena.model.AditivoContratual;
import com.formulario.athena.repository.AditivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AditivoServiceImpl implements AditivoService {

    @Autowired
    private AditivoRepository aditivoRepository;

    @Override
    @Transactional
    public AditivoResponseDTO createAditivo(AditivoRequestDTO request) {
        AditivoContratual aditivo = AditivoContratual.builder()
                .empresaId(request.getEmpresaId())
                .unidadeNome(request.getUnidadeNome())
                .unidadeCnpj(request.getUnidadeCnpj())
                .unidadeEndereco(request.getUnidadeEndereco())
                .pessoaFisicaNome(request.getPessoaFisicaNome())
                .pessoaFisicaCpf(request.getPessoaFisicaCpf())
                .pessoaFisicaEndereco(request.getPessoaFisicaEndereco())
                .dataInicioContrato(request.getDataInicioContrato())
                .pessoaJuridicaNome(request.getPessoaJuridicaNome())
                .pessoaJuridicaCnpj(request.getPessoaJuridicaCnpj())
                .pessoaJuridicaEndereco(request.getPessoaJuridicaEndereco())
                .status("PENDENTE")
                .dataCriacao(LocalDateTime.now())
                .build();


        AditivoContratual saved = aditivoRepository.save(aditivo);


        return AditivoResponseDTO.builder()
                .id(saved.getId())
                .empresaId(saved.getEmpresaId())
                .status(saved.getStatus())
                .dataCriacao(saved.getDataCriacao())
                .documentoPath(saved.getDocumentoPath())
                .build();
    }
}
