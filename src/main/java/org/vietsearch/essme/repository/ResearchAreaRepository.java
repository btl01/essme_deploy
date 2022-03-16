package org.vietsearch.essme.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.ResearchArea;

import java.util.Optional;

public interface ResearchAreaRepository extends MongoRepository<ResearchArea, String> {
    Optional<ResearchArea> findByNameVnOrNameEn(String nameVn, String nameEn);
}
