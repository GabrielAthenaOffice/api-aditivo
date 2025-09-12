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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AditivoServiceImpl implements AditivoService {

    @Autowired
    private AditivoRepository aditivoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Override
    @Transactional
    public AditivoResponseDTO createAditivo(AditivoRequestDTO dto) {
        // Converte DTO em entidade
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
    public AditivoSimpleResponseDTO listarTodosAditivos(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
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
                .map(aditivoContratual -> AditivoMapper.toSimpleResponse(aditivoContratual))
        return null;
    }

    @Override
    public AditivoSimpleResponseDTO listarPorNomeEmpresa(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String nomeEmpresa) {
        return null;
    }


}
