package org.vietsearch.essme.model.request_response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.*;

@Data
@Document("direct_request")
public class DirectRequest {
    @Id
    @JsonProperty(value = "_id", access = READ_ONLY)
    private String _id;

    @CreatedDate
    @JsonProperty(value = "create_at", access = READ_ONLY)
    @Field("create_at")
    private Date createAt;

    @LastModifiedDate
    @JsonProperty(value = "last_updated_at", access = READ_WRITE)
    @Field("last_updated_at")
    private Date lastUpdatedAt;

    @JsonProperty("topic")
    private List<String> topic;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "{vi=\"nội dung trống\", en=\"content is empty\"}")
    private String content;

    @JsonProperty("responses")
    private List<String> responses;

    @JsonProperty("expert_id")
    @Field("expert_id")
    private String expertId;

    @JsonProperty("expert_email")
    @Field("expert_email")
    private String expertEmail;

    @JsonProperty("customer_id")
    @Field("customer_id")
    private String customerId;

    @JsonProperty("status")
    private Status status;

    public enum Status{
        ACCEPTED,
        DENIED,
        CONSIDERING
    }
}
