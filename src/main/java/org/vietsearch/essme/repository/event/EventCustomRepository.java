package org.vietsearch.essme.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vietsearch.essme.model.event.Event;

public interface EventCustomRepository {
    Page<Event> searchByTextAndLocation(String text, String location, Pageable pageable);
}
