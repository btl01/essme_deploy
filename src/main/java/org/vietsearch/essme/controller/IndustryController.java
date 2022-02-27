package org.vietsearch.essme.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.industry.Industry;
import org.vietsearch.essme.repository.IndustryRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/industries")
public class IndustryController {

    @Autowired
    private IndustryRepository industryRepository;

    @GetMapping("/search")
    public List<Industry> searchIndustries(@RequestParam("name") String name) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(name);
        List<Industry> list = industryRepository.findBy(criteria);
        if (list.isEmpty())
            return industryRepository.findByNameStartsWithIgnoreCase(name);
        return list;
    }

    @GetMapping
    public List<Industry> getIndustries(@RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "20") int size,
                                        @RequestParam(name = "sortBy", defaultValue = "name") @Parameter(description = "name | rank") String sortBy,
                                        @RequestParam(name = "lang", defaultValue = "en") String lang,
                                        @RequestParam(name = "asc", defaultValue = "true") boolean asc) {
        Sort sort = Sort.by("names." + lang);
        if ("level".equals(sortBy))
            sort = Sort.by("level");
        if (!asc) sort.descending();
        return industryRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @GetMapping("/{id}")
    public Industry getIndustryById(@PathVariable String id) {
        return industryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Industry not found"));
    }

    @GetMapping("/{id}/sub")
    public List<Industry> getSubIndustry(@PathVariable String id) {
        Industry industry = industryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Industry not found"));
        return industryRepository.findBySourceParentId(industry.getSourceId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Industry createIndustry(@Valid @RequestBody Industry industry) {
        checkExistsByName(industry.getName());
        return industryRepository.save(industry);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id) {
        industryRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Industry update(@PathVariable("id") String id, @Valid @RequestBody Industry industry) {
        industryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Industry not found"));
        Industry optional = industryRepository.findByNameIgnoreCase(industry.getName());
        if (optional == null) {
            industry.set_id(id);
            return industryRepository.save(industry);
        } else {
            if (Objects.equals(optional.get_id(), id)) {
                industry.set_id(id);
                return industryRepository.save(industry);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Industry name already exists");
            }
        }
    }

    private void checkExistsByName(String name) {
        if (industryRepository.findByNameIgnoreCase(name) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Industry name already exists");
        }
    }
}
