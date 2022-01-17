package org.vietsearch.essme.repository;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.Event.Event;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findBy(TextCriteria criteria);
}
