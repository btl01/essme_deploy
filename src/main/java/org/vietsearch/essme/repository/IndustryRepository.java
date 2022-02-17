package org.vietsearch.essme.repository;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.industry.Industry;

import java.util.List;

public interface IndustryRepository extends MongoRepository<Industry, String> {
    List<Industry> findBy(TextCriteria criteria);
    List<Industry> findByNameStartsWithIgnoreCase(String name);
    Industry findByNameIgnoreCase(String name);
    List<Industry> findBySourceParentId(String id);
}
