package org.vietsearch.essme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document("research_area")
public class ResearchArea {

    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty("img")
    private String img;

    @JsonProperty("keys")
    private List<String> keys;

    @JsonProperty("des_vn")
    @Field("des_vn")
    private String desVn;

    @JsonProperty("name_vn")
    @Field("name_vn")
    private String nameVn;

    @JsonProperty("name_en")
    @Field("name_en")
    private String nameEn;

    @JsonProperty("des_en")
    @Field("des_en")
    private String desEn;

    @JsonProperty("status")
    private String status;
}