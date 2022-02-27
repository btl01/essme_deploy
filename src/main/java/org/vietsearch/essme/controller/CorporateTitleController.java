package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.corporate_title.Corporate;
import org.vietsearch.essme.repository.CorporateRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/corporates")
public class CorporateTitleController {
    @Autowired
    private CorporateRepository corporateRepository;

    @GetMapping("/search")
    public List<Corporate> search(@RequestParam("name") String name) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(name);
        List<Corporate> list = corporateRepository.findBy(criteria);
        if (list.isEmpty())
            return corporateRepository.findByNameStartsWithIgnoreCase(name);
        return list;
    }

    @GetMapping
    public List<Corporate> getCorporates(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "size", defaultValue = "20") int size,
                                         @RequestParam(name = "lang", defaultValue = "en") String lang,
                                         @RequestParam(name = "asc", defaultValue = "true") boolean asc) {
        Sort sort = Sort.by("names." + lang);
        if (!asc)
            sort.descending();
        return corporateRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @GetMapping("/{id}")
    public Corporate findById(@PathVariable("id") String id) {
        return corporateRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Corporate not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Corporate create(@Valid @RequestBody Corporate corporate) {
        return corporateRepository.save(corporate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id) {
            corporateRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Corporate update(@PathVariable("id") String id, @Valid @RequestBody Corporate corporate) {
        corporateRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Corporate not found"));
        Corporate optional = corporateRepository.findByNameIgnoreCase(corporate.getName());
        if (optional == null) {
            corporate.set_id(id);
            return corporateRepository.save(corporate);
        } else {
            if (Objects.equals(optional.get_id(), id)) {
                corporate.set_id(id);
                return corporateRepository.save(corporate);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corporate name already exists");
            }
        }
    }

    private void checkExistsByName(String name) {
        if (this.corporateRepository.findByNameIgnoreCase(name) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corporate already exists");
        }
    }
}
