package org.vietsearch.essme.model.companies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Ranks {
    @Field("Forbes")
    @JsonProperty("Forbes")
    private Integer forbes;

    @Field("Fortune")
    @JsonProperty("Fortune")
    private Integer fortune;

    @Field("ValueToday")
    @JsonProperty("ValueToday")
    private Integer valueToday;
}