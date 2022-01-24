package org.vietsearch.essme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class News {

    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty("img")
    private String img;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("title")
    private String title;

    @JsonProperty("url")
    private String url;

    @JsonProperty("content")
    private String content;

    @JsonProperty("status")
    private String status;
}