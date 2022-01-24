package org.vietsearch.essme.service.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vietsearch.essme.model.Event;

public interface EventService {
    Page<Event> search(String what, Pageable pageable);
}
