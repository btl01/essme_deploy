package org.vietsearch.essme.model.expert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeaturesItem {

    @JsonProperty("geometry")
    private Geometry geometry;

    @JsonProperty("type")
    private String type = "Feature";
}
