package org.vietsearch.essme.repository.academic_rank;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.vietsearch.essme.model.academic_rank.AcademicRank;

import java.util.List;
import java.util.Optional;

public interface AcademicRankRepository extends MongoRepository<AcademicRank, String> {
    List<AcademicRank> findBy(TextCriteria textCriteria);

    @Query("{$or: [" +
            "{'name': {$regex : /^?0/, $options : 'i'}}," +
            "{'synonyms.vi': {$regex: /^?0/, $options : 'i'}}," +
            "{'synonyms.en': {$regex: /^?0/, $options : 'i'}}" +
            "]}")
    List<AcademicRank> findByNameOrSynonymsStartsWithIgnoreCase(String name);

    Optional<AcademicRank> findByNameIgnoreCase(String name);
}
