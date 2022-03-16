package org.vietsearch.essme.model.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Document(collection = "customer")
public class Customer {
    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String _id;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("birth")
    private String birth;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("email")
    @Field("email")
    private String email;

    @JsonProperty("image")
    private String image;

    @JsonProperty("interest")
    private String interest;

    @JsonProperty("website")
    private String website;

    @JsonProperty("facebook")
    private String facebook;

    @JsonProperty("linkedIn")
    private String linkedIn;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("role")
    private String role;
}
