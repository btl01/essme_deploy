package org.vietsearch.essme.model.university;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Ranks{

	@JsonProperty("the")
	private Integer the;

	@JsonProperty("qs")
	private Integer qs;

	@JsonProperty("arwu")
	private Integer arwu;
}