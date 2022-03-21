package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.event.Event;
import org.vietsearch.essme.repository.event.EventCustomRepository;
import org.vietsearch.essme.repository.event.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventCustomRepository eventCustomRepository;

    @GetMapping("/location")
    public List<Event> searchByLocation(@RequestParam String location) {
        return eventRepository.findByLocationContainsIgnoreCase(location);
    }

    @GetMapping("/type")
    public List<Object> getAllType(@RequestParam(defaultValue = "vi") String lang) {
        return eventCustomRepository.countType(lang);
    }

    @GetMapping("/tag")
    public List<Object> getAllTag(@RequestParam(defaultValue = "vi") String lang) {
        return eventCustomRepository.countTag(lang);
    }

    @GetMapping("/search")
    public Page<Event> searchEvents(@RequestParam(value = "what", required = false) String what,
                                    @RequestParam(value = "where", required = false) String where,
                                    @RequestParam(value = "types", required = false) List<String> types,
                                    @RequestParam(value = "tags", required = false) List<String> tags,
                                    @RequestParam(value = "lang", defaultValue = "vi") String lang,
                                    @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "20", required = false) int size
    ) {
        return eventCustomRepository.searchEvents(what, where, types, tags, lang, PageRequest.of(page, size));
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
