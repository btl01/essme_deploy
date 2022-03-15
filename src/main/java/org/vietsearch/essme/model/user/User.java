package org.vietsearch.essme.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@Data
@Document("users")
public class User {
    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    String uid;

    @JsonProperty("email")
    @Field("email")
    @NotBlank(message = "{vi=\"nội dung trống\", en=\"content is empty\"}")
    String email;

    @JsonProperty("displayName")
    @Field("displayName")
    @NotBlank(message = "{vi=\"nội dung trống\", en=\"content is empty\"}")
    String displayName;

    @JsonProperty("photoURL")
    @Field("photoURL")
    String photoURL;

    @JsonProperty("phoneNumber")
    @Field("phoneNumber")
    String phoneNumber;

    @JsonProperty(value = "role")
    @Field("role")
    Role role;
}
