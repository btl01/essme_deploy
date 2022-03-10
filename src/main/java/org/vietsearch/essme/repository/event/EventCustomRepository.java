package org.vietsearch.essme.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vietsearch.essme.model.event.Event;

import java.util.List;

public interface EventCustomRepository {
    Page<Event> searchByTextAndLocationAndType(String text, String location, List<String> types, Pageable pageable);

    List<Object> countType();
}
