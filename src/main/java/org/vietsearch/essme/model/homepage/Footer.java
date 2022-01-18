package org.vietsearch.essme.model.homepage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Footer {

    @JsonProperty("twitter")
    private String twitter;

    @JsonProperty("terms")
    private String terms;

    @JsonProperty("facebook")
    private String facebook;

    @JsonProperty("contact")
    private String contact;

    @JsonProperty("team")
    private String team;

    @JsonProperty("linkedin")
    private String linkedin;

    @JsonProperty("slogan")
    private String slogan;
}