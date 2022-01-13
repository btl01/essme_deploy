package org.vietsearch.essme.model.expert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Properties {
    @JsonProperty("street")
    public String street;
}
