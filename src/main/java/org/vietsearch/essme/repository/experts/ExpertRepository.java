package org.vietsearch.essme.repository.experts;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.expert.Expert;

import java.util.List;
import java.util.Optional;

public interface ExpertRepository extends MongoRepository<Expert, String> {
    List<Expert> findBy(TextCriteria criteria);
    // get Expert form firebase uid
    Optional<Expert> findByEmail(String email);
    List<Expert> findByOrderByScoreDesc();
    Optional<Expert> findByUid(String uid);
}
