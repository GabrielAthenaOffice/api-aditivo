package com.formulario.athena.repository;

import com.formulario.athena.model.UserAthena;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserAthena, String> {
    Optional<UserAthena> findByEmail(String email);
    Optional<UserAthena> findByNome(String nome);
}

