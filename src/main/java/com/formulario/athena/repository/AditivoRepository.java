package com.formulario.athena.repository;

import com.formulario.athena.model.AditivoContratual;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AditivoRepository extends MongoRepository<AditivoContratual, String> {
    List<AditivoContratual> findByEmpresaId(String empresaId);
    List<AditivoContratual> findByPessoaJuridicaNomeIgnoreCase(String pessoaJuridicaNome);
}
