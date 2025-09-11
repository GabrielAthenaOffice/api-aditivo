package com.formulario.athena.repository;

import com.formulario.athena.model.AditivoHistorico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRepository extends MongoRepository<AditivoHistorico, String> {
    List<AditivoHistorico> findByEmpresaId(String empresaId);
    List<AditivoHistorico> findByEmpresaNomeIgnoreCase(String empresaNome);
}
