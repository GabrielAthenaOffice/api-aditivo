package com.formulario.athena.repository;

import com.formulario.athena.model.ContractAddendum;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContractAddendumRepository extends MongoRepository<ContractAddendum, String> {
}
