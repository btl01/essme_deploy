package org.vietsearch.essme.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.filter.AuthenticatedRequest;
import org.vietsearch.essme.model.expert.Expert;
import org.vietsearch.essme.repository.experts.ExpertCustomRepositoryImpl;
import org.vietsearch.essme.repository.experts.ExpertRepository;
import org.vietsearch.essme.service.expert.ExpertService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/experts")
public class ExpertController {
    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private ExpertCustomRepositoryImpl expertCustomRepository;

    @Autowired
    ExpertService expertService;

    @GetMapping("/top")
    public List<Expert> getTop() {
        return expertService.getTop9ExpertDistinctByRA();
    }

    @GetMapping
    public List<Expert> getWithLimit(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        return expertRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    @GetMapping("/search")
    public Page<Expert> searchExperts(@RequestParam(value = "what", required = false) String what,
                                      @RequestParam(value = "where", required = false) String where,
                                      @RequestParam(value = "radius", required = false, defaultValue = "5") @Parameter(description = "radius is kilometer") @NumberFormat double radius,
                                      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size) {

        // return all if blank
        if (what == null && where == null) {
            return expertRepository.findAll(PageRequest.of(page, size));
        }

        return expertCustomRepository.searchByLocationAndText(what, where, radius, PageRequest.of(page, size));
    }

    @GetMapping(path = "/page")
    public Page<Expert> getExperts(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "20") int size,
                                   @RequestParam(value = "sort", defaultValue = "degree index") @Parameter(description = "sort by 'name' or 'dergee index'") String sortAttr,
                                   @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        Sort sort = Sort.by(sortAttr);
        sort = desc ? sort.descending() : sort.ascending();

        Page<Expert> expertPage = expertRepository.findAll(PageRequest.of(page, size, sort));
        return expertPage;
    }

    @GetMapping("/{id}")
    public Expert findById(@PathVariable("id") String id) {
        return expertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expert not found"));
    }

    @GetMapping("/field")
    public List<Object> getNumberOfExpertsInEachField() {
        return expertCustomRepository.getNumberOfExpertsInEachField();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Expert update(AuthenticatedRequest request, @PathVariable("id") String id, @Valid @RequestBody Expert expert) {
        String uuid = request.getUserId();
        expertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expert not found"));
        if (!matchExpert(uuid, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        expert.setUid(uuid);
        expert.set_id(id);
        return expertRepository.save(expert);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expert createUser(AuthenticatedRequest request, @Valid @RequestBody Expert expert) {
        expert.setUid(request.getUserId());
        return expertRepository.save(expert);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(AuthenticatedRequest authenticatedRequest, @PathVariable("id") String id) {
        String uuid = authenticatedRequest.getUserId();
        if (!expertRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        if (!matchExpert(uuid, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        expertRepository.deleteById(id);
    }

    private boolean matchExpert(String uuid, String expertChangedId) {
        return uuid.equals(expertChangedId);
    }
}
