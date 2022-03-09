package org.vietsearch.essme.repository.customer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.customer.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
}
