package org.vietsearch.essme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.News;

import java.util.List;

public interface NewsRepository extends MongoRepository<News, String> {
    List<News> findBy(TextCriteria criteria);
    Page<News> findBy(TextCriteria criteria, PageRequest request);
}
