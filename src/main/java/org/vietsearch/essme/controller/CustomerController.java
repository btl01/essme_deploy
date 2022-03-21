package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.filter.AuthenticatedRequest;
import org.vietsearch.essme.model.customer.Customer;
import org.vietsearch.essme.model.user.User;
import org.vietsearch.essme.repository.UserRepository;
import org.vietsearch.essme.repository.customer.CustomerRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    private final UserRepository userRepository;

    // should use contractor instead of @Autowire
    public CustomerController(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Customer> getAllCustomer(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Customer getCustomerById(@PathVariable("id") String id) {
        return customerRepository.
                findById(id).
                orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer does not exist"));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(AuthenticatedRequest request, @Valid @RequestBody Customer customer) {
        customer.setUid(request.getUserId());
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Customer update(AuthenticatedRequest request, @PathVariable("id") String id, @Valid @RequestBody Customer customer) {
        String uuid = request.getUserId();
        Customer customerDB = customerRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Customer %s not found", id), null));

        if(!matchCustomer(uuid, customerDB.getUid())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        customer.setUid(uuid);
        customer.set_id(id);

        return customerRepository.save(customer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(AuthenticatedRequest request, @PathVariable("id") String id){
        String uuid = request.getUserId();
        Customer customerDB = customerRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Customer %s not found", id), null));
        if(!matchCustomer(uuid, customerDB.getUid())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        customerRepository.deleteById(id);
    }

    @GetMapping("/uid/{uid}")
    public Customer findByUid(@PathVariable("uid") String uid){
        User user = userRepository.findById(uid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "not found customer"));
        return customerRepository.findByEmail(user.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "not found customer"));
    }

    private boolean matchCustomer(String uuid, String customerChangedId){
        return uuid.equals(customerChangedId);
    }
}
