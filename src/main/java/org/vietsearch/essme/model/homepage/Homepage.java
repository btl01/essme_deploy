package org.vietsearch.essme.model.homepage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Document("homepage")
public class Homepage {

    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String _id;

    @NotNull
    private Footer footer;

    @NotEmpty
    @Size(min = 9)
    private List<Field> fields;
}
