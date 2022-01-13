package org.vietsearch.essme.model.companies_vn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
@Document(collection = "companies_vn")
@Data
public class CompanyVn{
	@Id
	@JsonProperty("_id")
	private String _id;

	@Field(name = "name_company")
	@JsonProperty("name_company")
	@NotBlank
	private String nameCompany;

	@Field(name = "tax_code")
	@JsonProperty("tax_code")
	@NotBlank
	private String taxCode;

	@JsonProperty("website")
	private String website;

	@Field(name = "founded_year")
	@JsonProperty("founded_year")
	private String foundedYear;

	@JsonProperty("location")
	@NotBlank
	private String location;

	@JsonProperty("tel")
	@NotBlank
	private String tel;

	@Field(name = "tax")
	@JsonProperty("fax")
	@NotBlank
	private String fax;

	@JsonProperty("email")
	@NotBlank
	private String email;

	@JsonProperty("desc")
	private String desc;
}