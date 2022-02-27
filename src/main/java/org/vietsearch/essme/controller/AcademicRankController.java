package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.academic_rank.AcademicRank;
import org.vietsearch.essme.repository.academic_rank.AcademicRankRepository;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/academic-rank")
public class AcademicRankController {
    @Autowired
    private AcademicRankRepository rankRepository;

    @GetMapping
    public List<AcademicRank> getAcademicRanks(@RequestParam(name = "page", defaultValue = "0") int page,
                                               @RequestParam(name = "size", defaultValue = "20") int size,
                                               @RequestParam(name = "asc", defaultValue = "true") boolean asc) {
        Sort sort = Sort.by("name");
        sort = asc ? sort.ascending() : sort.descending();

        return rankRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }
    @GetMapping("/{_id}")
    public AcademicRank getById(@PathVariable("_id") String _id) {
        return rankRepository.findById(_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rank not found"));
    }

    @GetMapping("/search")
    public List<AcademicRank> searchAcademicRank(@RequestParam(name = "text") String text) {
        if (text == null || "".equals(text)) {
            return rankRepository.findAll();
        }

        List<AcademicRank> list = rankRepository.findBy(
                TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(text)
        );
        if (list.isEmpty()) list = rankRepository.findByNameOrSynonymsStartsWithIgnoreCase(text);
        return list;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicRank addAcademicRank(@RequestBody AcademicRank academicRank) {
        checkExistsByName(academicRank.getName());
        return rankRepository.insert(academicRank);
    }

    @DeleteMapping("/{_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("_id") String _id) {
        rankRepository.deleteById(_id);
    }

    @PutMapping("/{_id}")
    @ResponseStatus(HttpStatus.OK)
    public AcademicRank updateById(@PathVariable("_id") String _id, @RequestBody AcademicRank academicRank) {
        rankRepository.findById(_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rank not found"));
        AcademicRank optional = rankRepository.findByNameIgnoreCase(academicRank.getName());
        if (optional == null) {
            academicRank.set_id(_id);
            return rankRepository.save(academicRank);
        } else {
            if (Objects.equals(optional.get_id(), _id)) {
                academicRank.set_id(_id);
                return rankRepository.save(academicRank);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rank name already exists");
            }
        }
    }

    private void checkExistsByName(String name) {
        if (rankRepository.findByNameIgnoreCase(name) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is already used");
    }
}
