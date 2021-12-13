package org.vietsearch.essme.repository.companies;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.vietsearch.essme.model.companies.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String> {
    List<Company> findBy(TextCriteria textCriteria);

    @Query("{$or: [" +
            "{'names.de': {$regex: /^?0/, $options : 'i'}}," +
            "{'names.en': {$regex: /^?0/, $options : 'i'}}," +
            "{'names.fr': {$regex: /^?0/, $options : 'i'}}," +
            "{'names.vi': {$regex: /^?0/, $options : 'i'}}," +
            "{'industries': {$regex: /^?0/, $options : 'i'}}" +
            "]}")
    List<Company>findByNamesOrIndustriesStartsWithIgnoreCase(String name);

    Optional<Company> findByNameIgnoreCase(String name);
}
