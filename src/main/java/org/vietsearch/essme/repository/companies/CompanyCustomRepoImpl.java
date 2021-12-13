package org.vietsearch.essme.repository.companies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.vietsearch.essme.model.companies.Company;

import java.util.List;

@Repository
public class CompanyCustomRepoImpl implements CompanyCustomRepo {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Company> getCompaniesByCountryIndustryAndRank(String country, String industry, String rank, boolean asc) {
        Sort sort =  Sort.by("ranks." + rank);
        sort = asc ? sort.ascending() : sort.descending();
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("country").regex(country, "i")
                                .and("industries").regex(industry , "i")
                ).with(sort),
                Company.class);
    }
}
