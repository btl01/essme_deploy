package org.vietsearch.essme.repository.customer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.customer.Customer;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByEmail(String email);
}
