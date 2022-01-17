package org.vietsearch.essme.model.Event;

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

    @Field("event_name")
    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("location")
    private String location;

    @JsonProperty("time")
    private String time;

    @JsonProperty("type")
    private List<String> type;

    @JsonProperty("desc")
    private String desc;

    private Geojson geojson;
}