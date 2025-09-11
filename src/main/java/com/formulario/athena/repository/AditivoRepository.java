package com.formulario.athena.repository;

import com.formulario.athena.model.AditivoContratual;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AditivoRepository extends MongoRepository<AditivoContratual, String> {
}
