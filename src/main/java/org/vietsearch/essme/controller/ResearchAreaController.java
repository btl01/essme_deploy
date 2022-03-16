package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.ResearchArea;
import org.vietsearch.essme.repository.ResearchAreaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/research_area")
public class ResearchAreaController {

    @Autowired
    ResearchAreaRepository researchAreaRepository;

    @GetMapping
    public List<ResearchArea> get() {
        return researchAreaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResearchArea getById(@PathVariable String id) {
        return researchAreaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public ResearchArea getByName(@RequestParam String name) {
        return researchAreaRepository.findByNameVnOrNameEn(name, name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResearchArea post(@RequestBody ResearchArea data) {
        return researchAreaRepository.save(data);
    }

    @PutMapping("/{id}")
    public ResearchArea put(@PathVariable String id, @RequestBody ResearchArea data) {
        researchAreaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        data.setId(id);
        return researchAreaRepository.save(data);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        researchAreaRepository.deleteById(id);
    }
}
