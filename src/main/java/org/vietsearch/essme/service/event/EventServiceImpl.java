package org.vietsearch.essme.service.event;

import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import org.vietsearch.essme.model.Event;
import org.vietsearch.essme.model.expert.Expert;
import org.vietsearch.essme.repository.EventRepository;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;


    @Override
    public Page<Event> search(String what, Pageable pageable) {
        // text search
        if (what != null) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(what);
            List<Event> list = eventRepository.findBy(criteria);
            return new PageImpl<Event>(list, pageable, list.size());
        }
        return eventRepository.findAll(pageable);
    }
}
