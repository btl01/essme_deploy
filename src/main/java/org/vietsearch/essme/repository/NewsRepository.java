package org.vietsearch.essme.repository;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.News;

import java.util.List;

public interface NewsRepository extends MongoRepository<News, String> {
    List<News> findBy(TextCriteria criteria);
}