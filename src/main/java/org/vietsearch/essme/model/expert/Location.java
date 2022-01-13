package org.vietsearch.essme.model.expert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Location {

    @JsonProperty("features")
    private List<FeaturesItem> features;

    @JsonProperty("type")
    private String type = "FeatureCollection";
}
