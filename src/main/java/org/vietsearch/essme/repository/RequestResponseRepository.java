package org.vietsearch.essme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.request_response.Request;

import java.util.List;

public interface RequestResponseRepository extends MongoRepository<Request, String> {
    List<Request> findBy(TextCriteria criteria);

    Page<Request> findByTopic(String topic, PageRequest of);

    List<Request> findAllByCustomerId(String customerID);

    Page<Request> findByUid(String uid, Pageable pageable);
}
