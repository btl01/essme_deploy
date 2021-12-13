package org.vietsearch.essme.repository.companies;

import org.vietsearch.essme.model.companies.Company;

import java.util.List;

public interface CompanyCustomRepo {
    List<Company> getCompaniesByCountryIndustryAndRank(String country, String industry, String rank, boolean asc);
}
