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
import org.vietsearch.essme.model.expert.Location;
import org.vietsearch.essme.model.user.User;
import org.vietsearch.essme.repository.UserRepository;
import org.vietsearch.essme.repository.experts.ExpertCustomRepositoryImpl;
import org.vietsearch.essme.repository.experts.ExpertRepository;
import org.vietsearch.essme.service.expert.ExpertService;
import org.vietsearch.essme.utils.OpenStreetMapUtils;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/experts")
public class ExpertController {
    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private ExpertCustomRepositoryImpl expertCustomRepository;

    @Autowired
    private ExpertService expertService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/top")
    public List<Expert> getTop() {
        return expertService.getTop9ExpertDistinctByRA();
    }

    @GetMapping
    public List<Expert> getWithLimit(@RequestParam(name = "limit", defaultValue = "20") int limit,
                                     @RequestParam(name = "sortBy", required = false) String sortBy,
                                     @RequestParam(name = "asc", defaultValue = "true") boolean asc) {
        Sort sort = Sort.by("score");
        if (sortBy != null) {
            sort = Sort.by(sortBy);
            if (!asc) {
                sort.descending();
            }
        }
        return expertRepository.findAll(PageRequest.of(0, limit, sort)).getContent();
    }

    @GetMapping("/suggest")
    public List<Expert> getRelateByField(@RequestParam String field,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam(defaultValue = "0") int skip) {
        return expertCustomRepository.relatedExpertsByField(field, limit, skip);
    }

    @GetMapping("/{id}/related")
    public List<Expert> getRelatedExpertsById(@PathVariable String id,
                                          @RequestParam(defaultValue = "20") int limit,
                                          @RequestParam(defaultValue = "0") int skip) {
        Expert expert = expertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return expertCustomRepository.relatedExpertsByExpert(expert, limit, skip);
    }

    @GetMapping("/search")
    public Page<Expert> searchExperts(@RequestParam(value = "what", required = false) String what,
                                      @RequestParam(value = "where", required = false) String where,
                                      @RequestParam(value = "radius", required = false, defaultValue = "5") @Parameter(description = "radius is kilometer") @NumberFormat double radius,
                                      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                      @RequestParam(name = "sortBy", required = false) String sortBy,
                                      @RequestParam(name = "asc", defaultValue = "true") boolean asc) {

        Sort sort = Sort.by("degree index");
        if (sortBy != null) {
            sort = Sort.by(sortBy);
            if (!asc) {
                sort.descending();
            }
        }
        if (Objects.equals(what, "")) {
            what = null;
        }

        if (Objects.equals(where, "")) {
            where = null;
        }

        // return all if blank
        if (what == null && where == null) {
            return expertRepository.findAll(PageRequest.of(page, size, sort));
        }

        return expertCustomRepository.searchByLocationAndText(what, where, radius, PageRequest.of(page, size, sort));
    }

    @GetMapping(path = "/page")
    public Page<Expert> getExperts(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "20") int size,
                                   @RequestParam(value = "sort", defaultValue = "degree index") @Parameter(description = "sort by 'name' or 'dergee index'") String sortAttr,
                                   @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        Sort sort = Sort.by(sortAttr);
        sort = desc ? sort.descending() : sort.ascending();

        return expertRepository.findAll(PageRequest.of(page, size, sort));
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
        Expert expertDB = expertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expert not found"));
        if (!matchExpert(uuid, expertDB.getUid())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        expert.setUid(uuid);
        expert.set_id(id);

        // add location
        if (expert.getAddress() != null){
            Location location = OpenStreetMapUtils.getInstance().addressToLocation(expert.getAddress().get(0));
            expert.setLocation(location);
        }

        return expertRepository.save(expert);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expert createUser(AuthenticatedRequest request, @Valid @RequestBody Expert expert) {
        expert.setUid(request.getUserId());

        // add location
        if (expert.getAddress() != null){
            Location location = OpenStreetMapUtils.getInstance().addressToLocation(expert.getAddress().get(0));
            expert.setLocation(location);
        }

        return expertRepository.save(expert);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(AuthenticatedRequest authenticatedRequest, @PathVariable("id") String id) {
        String uuid = authenticatedRequest.getUserId();
        Expert expertDB = expertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expert not found"));
        if (!expertRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        if (!matchExpert(uuid, expertDB.getUid())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        expertRepository.deleteById(id);
    }

    @GetMapping("/uid/{uid}")
    public Expert findByUid(@PathVariable("uid") String uid ){
        User user = userRepository.findById(uid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expert not found"));
        return expertRepository.findByEmail(user.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expert not found"));
    }

    private boolean matchExpert(String uuid, String expertChangedId) {
        return uuid.equals(expertChangedId);
    }
}
