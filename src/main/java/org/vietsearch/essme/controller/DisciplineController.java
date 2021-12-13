package org.vietsearch.essme.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.academic_disciplines.Discipline;
import org.vietsearch.essme.repository.academic_disciplines.DisciplineRepository;

import java.util.List;

@RestController
@RequestMapping("/api/discipline")
public class DisciplineController {
    @Autowired
    private DisciplineRepository disciplineRepository;

    @GetMapping
    public List<Discipline> getDisciplines(@RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "20") int size,
                                           @RequestParam(name = "lang", defaultValue = "en") @Parameter(description = "en | vi") String lang,
                                           @RequestParam(name = "asc", defaultValue = "true") boolean asc) {
        Sort sort = Sort.by("names." + lang);
        sort = asc ? sort.ascending() : sort.descending();

        return disciplineRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @GetMapping("/id/{_id}")
    public Discipline getDisciplineById(@PathVariable("_id") String _id) {
        return disciplineRepository.findById(_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found"));
    }

    @GetMapping("/search")
    public List<Discipline> searchDisciplines(@RequestParam("text") String text) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(text);
        List<Discipline> list = disciplineRepository.findBy(criteria);
        if (list.isEmpty()) {
            list = disciplineRepository.findByNamesOrSynonymsStartsWithIgnoreCase(text);
        }
        return list;
    }

    @GetMapping("/{parent_id}")
    public Object parent(@PathVariable("parent_id") String parentId) {
       return disciplineRepository.findByParentIdStartsWithIgnoreCase(parentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Discipline addDiscipline(@RequestBody Discipline discipline) {
        checkExistsByName(discipline.getName());
        return disciplineRepository.insert(discipline);
    }

    @PutMapping("/{_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Discipline updateById(@PathVariable("_id") String _id, @RequestBody Discipline discipline) {
        disciplineRepository.findById(_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found"));
        checkExistsByName(discipline.getName());
        discipline.set_id(_id);
        return disciplineRepository.save(discipline);
    }

    @DeleteMapping("/{_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteById(@PathVariable("_id") String _id) {
        disciplineRepository.deleteById(_id);
    }

    private void checkExistsByName(String name) {
        if (disciplineRepository.findByNameIgnoreCase(name).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name " + name + " is already used");
    }
}
