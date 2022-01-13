package org.vietsearch.essme.model.corporate_title;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document("corporate_titles")
public class Corporate {

	@Id
	@JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
	private String _id;

	@JsonProperty("types")
	private List<String> types;

	@JsonProperty("names")
	@NotBlank
	private Names names;

	@JsonProperty("synonyms")
	private Synonyms synonyms;

	@JsonProperty("name")
	@NotNull
	@Valid
	private String name;

	@JsonProperty("weight")
	private Integer weight;

	@JsonProperty("id")
	private String id;

	@JsonProperty("source")
	private String source;
}
