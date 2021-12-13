package org.vietsearch.essme.repository.academic_disciplines;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.vietsearch.essme.model.academic_disciplines.Discipline;

import java.util.List;
import java.util.Optional;

public interface DisciplineRepository extends MongoRepository<Discipline, String> {
    List<Discipline> findBy(TextCriteria criteria);

    List<Discipline> findByParentIdStartsWithIgnoreCase(String parentId);

    @Query("{$or:[" +
            "{'names.en': {$regex: /^?0/, $options : 'i'}}," +
            "{'names.vi': {$regex: /^?0/, $options : 'i'}}," +
            "{'synonyms.vi': {$regex: /^?0/, $options : 'i'}}," +
            "{'synonyms.en': {$regex: /^?0/, $options : 'i'}}" +
            "]}")
    List<Discipline> findByNamesOrSynonymsStartsWithIgnoreCase(String text);

    Optional<Discipline> findByNameIgnoreCase(String text);
}
