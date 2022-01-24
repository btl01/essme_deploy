package org.vietsearch.essme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.event.Event;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findBy(TextCriteria criteria);

    Page<Event> findBy(TextCriteria criteria, Pageable pageable);
}
