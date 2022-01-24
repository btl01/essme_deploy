package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.event.Event;
import org.vietsearch.essme.repository.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/search")
    public Page<Event> searchEvents(@RequestParam(value = "what", required = false) String what,
                                    @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        if (what == null) {
            return eventRepository.findAll(PageRequest.of(page, size));
        }
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(what);
        return eventRepository.findBy(criteria, PageRequest.of(page, size));
    }

    @GetMapping
    public List<Event> getEvents(@RequestParam(value = "limit", required = false) Integer limit) {
        if (limit == null) {
            return eventRepository.findAll();
        }
        Page<Event> eventPage = eventRepository.findAll(PageRequest.of(0, limit));
        return eventPage.getContent();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable String id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String id) {
        eventRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Event update(@PathVariable String id, @RequestBody Event event) {
        eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        event.setId(id);
        return eventRepository.save(event);
    }
}
