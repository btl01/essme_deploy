package org.vietsearch.essme.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.companies_vn.CompanyVn;
import org.vietsearch.essme.repository.companies_vn.CompanyVnRepository;

import java.util.List;

@RestController
@RequestMapping(path = "/api/company-vn")
public class CompanyVnController {
    @Autowired
    private CompanyVnRepository companyVnRepository;

    @GetMapping
    public List<CompanyVn> getCompanies(@RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "20") int size,
                                        @RequestParam(name = "asc", defaultValue = "true")
                                        @Parameter(description = "Sort result in alphabetical order, set with false for reverse one") boolean asc) {
        Sort sort = Sort.by("name_company").ascending();
        if (!asc) {
            sort = sort.descending();
        }
        return companyVnRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @GetMapping(path = "/{id}")
    public CompanyVn getById(@PathVariable(name = "id") String id) {
        return companyVnRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found company with id " + id));
    }

    @GetMapping(path = "/search")
    public List<CompanyVn> searchByName(@RequestParam(name = "text") String text) {
        return companyVnRepository.findBy(
                TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(text)
        );

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyVn addNewCompanyVn(@RequestBody CompanyVn companyVn) {
        checkIfNameOrTaxCodeOrTelOrFaxExists(companyVn);
        return companyVnRepository.insert(companyVn);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyVn updateById(@PathVariable(name = "id") String id, @RequestBody CompanyVn companyVn) {
        companyVnRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found company with id " + id)
        );
        companyVn.set_id(id);
        return companyVnRepository.save(companyVn);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteById(@PathVariable(name = "id") String id) {
        companyVnRepository.deleteById(id);
        return "Delete company with id " + id;
    }

    private void checkIfNameOrTaxCodeOrTelOrFaxExists(CompanyVn companyVn) {
        String companyName = companyVn.getNameCompany();
        String taxCode = companyVn.getTaxCode();
        String tel = companyVn.getTel();
        String fax = companyVn.getFax();

        boolean nameExists = companyVnRepository.findByNameCompanyIgnoreCase(companyName).isPresent();
        boolean taxCodeExists = companyVnRepository.findByTaxCode(taxCode).isPresent();
        boolean telExists = companyVnRepository.findByTel(tel).isPresent();
        boolean faxExists = companyVnRepository.findByFax(fax).isPresent();

        if (nameExists || taxCodeExists || telExists || faxExists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Company with name %s, tax code %s, tel %s or fax %s has already existed", companyName, taxCode, tel, fax));
        }
    }

}
