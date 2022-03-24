package org.vietsearch.essme.model.expert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Document(collection = "experts_vn")
public class Expert {

    @Id
    @JsonProperty(value = "_id", access = JsonProperty.Access.READ_ONLY)
    private String _id;

    @JsonProperty("img")
    private String img;

    @Field(name = "other link")
    @JsonProperty("other_link")
    private String otherLink;

    @JsonProperty("address")
    private List<String> address;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("degree")
    private String degree;

    private Double score;

    @JsonProperty("birth")
    private String birth;

    @NotBlank
    @JsonProperty("phone")
    private String phone;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @Field(name = "research area")
    @JsonProperty("research_area")
    private List<String> researchArea;

    @JsonProperty("company")
    private String company;

    @JsonProperty(value = "location", access = JsonProperty.Access.READ_ONLY)
    private Location location;

    @NotBlank
    @JsonProperty("email")
    private String email;

    @Field(name = "link profile")
    @JsonProperty("link_profile")
    private String linkProfile;

    @JsonProperty("uid")
    @Field("uid")
    // uid is id provided by firebase
    private String uid;

}