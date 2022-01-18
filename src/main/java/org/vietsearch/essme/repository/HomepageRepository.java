package org.vietsearch.essme.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.homepage.Homepage;

public interface HomepageRepository extends MongoRepository<Homepage, String> {
    Homepage findFirstBy();
}
