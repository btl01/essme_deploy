package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.customer.Customer;
import org.vietsearch.essme.repository.customer.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private final CustomerRepository customerRepository;

    // should use contractor instead of @Autowire
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Customer> getAllCustomer(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "size", defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page,size);
        return customerRepository.findAll(pageable).getContent();
    }


    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable("id") String id){
        return customerRepository.
                findById(id).
                orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer does not exist"));
    }
}
