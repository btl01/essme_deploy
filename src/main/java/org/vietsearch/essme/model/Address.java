package org.vietsearch.essme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address{

	@JsonProperty("displayed")
	private String displayed;
}