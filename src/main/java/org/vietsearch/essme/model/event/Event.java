package org.vietsearch.essme.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document("events")
public class Event {

    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty("img")
    private String img;

    @JsonProperty("web")
    private String web;

    @Field("name_event")
    @JsonProperty("name_event")
    private String nameEvent;

    @JsonProperty("location")
    private String location;

    @JsonProperty("time")
    private String time;

    @Field("tags_en")
    @JsonProperty("tags_en")
    private List<String> tagsEn;

    @Field("tags_vn")
    @JsonProperty("tags_vn")
    private List<String> tagsVn;

    @Field("type_en")
    @JsonProperty("type_en")
    private List<String> typeEn;

    @Field("type_vn")
    @JsonProperty("type_vn")
    private List<String> typeVn;

    @JsonProperty("desc")
    private String desc;

    private Geojson geojson;
}