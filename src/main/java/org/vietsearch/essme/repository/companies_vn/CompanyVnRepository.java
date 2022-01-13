package org.vietsearch.essme.repository.companies_vn;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vietsearch.essme.model.companies_vn.CompanyVn;

import java.util.List;
import java.util.Optional;

public interface CompanyVnRepository extends MongoRepository<CompanyVn, String> {
    List<CompanyVn> findBy(TextCriteria textCriteria);
    Optional<CompanyVn> findByNameCompanyIgnoreCase(String name);
    Optional<CompanyVn> findByTaxCode(String taxCode);
    Optional<CompanyVn> findByTel(String tel);
    Optional<CompanyVn> findByFax(String fax);
}
