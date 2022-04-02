package org.vietsearch.essme.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.event.OnSendResponseEvent;
import org.vietsearch.essme.filter.AuthenticatedRequest;
import org.vietsearch.essme.model.customer.Customer;
import org.vietsearch.essme.model.expert.Expert;
import org.vietsearch.essme.model.request_response.DirectRequest;
import org.vietsearch.essme.model.request_response.Request;
import org.vietsearch.essme.model.request_response.Response;
import org.vietsearch.essme.repository.RequestResponseRepository;
import org.vietsearch.essme.repository.UserRepository;
import org.vietsearch.essme.repository.customer.CustomerRepository;
import org.vietsearch.essme.repository.direct_request.DirectRequestRepository;
import org.vietsearch.essme.repository.experts.ExpertRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
public class RequestResponseController {
    @Autowired
    private RequestResponseRepository requestRepository;

    @Autowired
    private DirectRequestRepository directRequestRepository;

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearer-key")
    public Request addRequest(AuthenticatedRequest httpRequest, @Valid @RequestBody Request customRequest) {
        customRequest.setUid(httpRequest.getUserId());
        requestRepository.save(customRequest);
        return requestRepository.save(customRequest);
    }

    @PostMapping("/{requestId}/responses")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearer-key")
    public Request addResponse(AuthenticatedRequest httpRequest,
                               @PathVariable("requestId") String requestID,
                               @Valid @RequestBody Response response) {

        // get main request
        Request request = requestRepository.findById(requestID).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null));
        if (request.getResponses() == null)
            request.setResponses(new ArrayList<>());

        // update response
        Date now = new Date();
        response.set_id(new ObjectId().toString());
        response.setUid(httpRequest.getUserId());
        response.setUpdatedAt(now);
        response.setCreatedAt(now);

        // save
        request.getResponses().add(response);
        return requestRepository.save(request);
    }


    @GetMapping
    public Page<Request> getRequests(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "20") int size,
                                     @RequestParam(value = "sort", defaultValue = "createdAt") String sortAttr,
                                     @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        Sort sort = Sort.by(sortAttr);
        if (desc)
            sort = sort.descending();

        Page<Request> requestPage = requestRepository.findAll(PageRequest.of(page, size, sort));
        return requestPage;
    }


    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable("id") String id) {
        return requestRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
    }

    @GetMapping("/search")
    public List<Request> searchRequests(@RequestParam("text") String text) {
        if (text == null || "".equals(text)) {
            return requestRepository.findAll();
        }
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(text);
        return requestRepository.findBy(criteria);
    }

    @GetMapping("/topic/{topic}")
    public Page<Request> getRequestByTopic(@PathVariable("topic") String topic,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size,
                                           @RequestParam(value = "sort", defaultValue = "createdAt") String sortAttr,
                                           @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        Sort sort = Sort.by(sortAttr);
        if (desc)
            sort = sort.descending();

        Page<Request> requestPage = requestRepository.findByTopic(topic, PageRequest.of(page, size, sort));
        return requestPage;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public Request updateRequest(AuthenticatedRequest authenticatedRequest,
                                 @PathVariable("id") String id,
                                 @Valid @RequestBody Request request) {
        String uuid = authenticatedRequest.getUserId();

        // find old request
        Request oldRequest = requestRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request cannot found!"));

        // check permission
        if (!matchUserRequest(uuid, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }

        // update
        request.setUid(uuid);
        request.set_id(id);
        request.setUpdatedAt(new Date());
        request.setCreatedAt(oldRequest.getCreatedAt()); // keep createAt unchangeable

        // save
        requestRepository.save(request);
        return request;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public String deleteRequest(AuthenticatedRequest authenticatedRequest, @PathVariable("id") String id) {
        String uuid = authenticatedRequest.getUserId();
        if (requestRepository.existsById(id)) {
            // check
            if (!matchUserRequest(uuid, id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
            }

            // delete
            requestRepository.deleteById(id);
            return "Deleted";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found", null);
        }
    }

    @GetMapping("/{requestId}/responses")
    public List<Response> getResponse(@PathVariable("requestId") String requestId) {
        Request request = requestRepository.findById(requestId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found", null));
        return request.getResponses();
    }

    @PutMapping("/{requestId}/responses/{responsesId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public Response updateResponse(AuthenticatedRequest authenticatedRequest,
                                   @PathVariable("requestId") String requestId,
                                   @PathVariable("responsesId") String responsesId,
                                   @Valid @RequestBody Response response) {
        String uuid = authenticatedRequest.getUserId();
        Request request = requestRepository.findById(requestId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found", null));
        if (request.getResponses() != null) {
            for (Response res : request.getResponses()) {
                if (matchExpertResponse(uuid, responsesId, res)) {
                    res.setUid(uuid);
                    res.setExpertId(response.getExpertId());
                    res.setContent(response.getContent());
                    res.setUpdatedAt(new Date());
                    requestRepository.save(request);
                    return res;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response not found", null);
    }

    @DeleteMapping("/{requestId}/responses/{responsesId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public String deleteResponse(AuthenticatedRequest authenticatedRequest,
                                 @PathVariable("requestId") String requestId,
                                 @PathVariable("responsesId") String responseId) {
        String uuid = authenticatedRequest.getUserId();
        Request request = requestRepository.findById(requestId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found", null));
        if (request.getResponses() != null) {
            for (Response res : request.getResponses()) {
                if (matchExpertResponse(uuid, responseId, res)) {
                    request.getResponses().remove(res);
                    requestRepository.save(request);
                    return "Deleted";
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response not found", null);
    }


    /*
     * uuid : firebase uuid
     * return true if uuid created request
     */
    private boolean matchUserRequest(String uuid, String requestId) {
        Optional<Request> optional = requestRepository.findById(requestId);
        return optional.map(res -> res.getUid().equals(uuid)).orElse(false);
    }

    /*
     * uuid : firebase uuid
     * return true if uuid created response
     */
    private boolean matchExpertResponse(String uuid, String responseChangedId, Response response) {
        if (!response.get_id().equals(responseChangedId))
            return false;
        if (!response.getUid().equals(uuid))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        return response.getUid().equals(uuid);
    }


    @GetMapping("/direct/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public DirectRequest findDirectRequestById(@PathVariable("requestId") String id) {
        return directRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Direct request not found"));

    }

    @GetMapping("/direct/expert/{expertId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DirectRequest> findDirectRequestByExpertId(@PathVariable("expertId") String id) {
        return directRequestRepository.findDirectRequestByExpertId(id);
    }

    @GetMapping("/direct/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DirectRequest> findDirectRequestByCustomerId(@PathVariable("customerId") String id) {
        return directRequestRepository.findDirectRequestByCustomerId(id);
    }

    @PostMapping("/direct")
    @ResponseStatus(HttpStatus.CREATED)
    public DirectRequest addDirectRequest(@Valid @RequestBody DirectRequest directRequest) {
        Date createAt = new Date();
        directRequest.setCreateAt(createAt);
        directRequest.setLastUpdatedAt(createAt);
        directRequest.setStatus(DirectRequest.Status.CONSIDERING);

        // get information
        Customer customer = customerRepository.findById(directRequest.getExpertId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be customer"));
        String expertEmail = directRequest.getExpertEmail();
        String customerEmail = customer.getEmail();

        // add expertId if exist expert
        Expert expert = expertRepository.findById(directRequest.getExpertId()).orElse(null);
        directRequest.setExpertId(expert != null ? expert.get_id() : null);

        // public event
        eventPublisher.publishEvent(new OnSendResponseEvent(expertEmail, customerEmail));
        return directRequestRepository.insert(directRequest);
    }

    @PutMapping("/direct/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public DirectRequest updateDirectRequest(AuthenticatedRequest authenticatedRequest,
                                             @PathVariable("requestId") String id,
                                             @Valid @RequestBody DirectRequest request) {
        // get old request
        DirectRequest oldRequest = directRequestRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not fount"));

        // update request
        request.setLastUpdatedAt(new Date());
        request.set_id(id);
        request.setCreateAt(oldRequest.getCreateAt()); // keep createAt not changeable

        return directRequestRepository.save(request);
    }

    @DeleteMapping("/direct/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public String deleteDirectRequest(AuthenticatedRequest authenticatedRequest,
                                      @PathVariable("requestId") String id) {
        directRequestRepository.deleteById(id);
        return "Deleted request: " + id;
    }

    @GetMapping("/uid/{uid}")
    public Page<Request> getResponseByUid(@PathVariable("uid") String uid, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return requestRepository.findByUid(uid, PageRequest.of(page, size));
    }
}
