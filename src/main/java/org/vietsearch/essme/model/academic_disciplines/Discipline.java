package org.vietsearch.essme.model.academic_disciplines;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("academic_disciplines")
public class Discipline {
    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String _id;

    @JsonProperty("names")
    private Names names;

    @Field(name = "parent_id")
    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("synonyms")
    private Synonyms synonyms;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;
}