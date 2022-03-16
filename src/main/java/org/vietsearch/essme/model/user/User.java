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

    @JsonProperty(value = "email")
    @Field("email")
    @NotBlank(message = "{vi=\"nội dung trống\", en=\"content is empty\"}")
    String email;

    @JsonProperty(value = "displayName", access = JsonProperty.Access.READ_ONLY)
    @Field("displayName")
    @NotBlank(message = "{vi=\"nội dung trống\", en=\"content is empty\"}")
    String displayName;

    @JsonProperty(value = "photoURL", access = JsonProperty.Access.READ_ONLY)
    @Field("photoURL")
    String photoURL;

    @JsonProperty(value = "phoneNumber", access = JsonProperty.Access.READ_ONLY)
    @Field("phoneNumber")
    String phoneNumber;

    @JsonProperty(value = "role")
    @Field("role")
    Role role;
}
