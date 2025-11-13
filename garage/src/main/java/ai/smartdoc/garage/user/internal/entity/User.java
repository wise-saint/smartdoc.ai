package ai.smartdoc.garage.user.internal.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("users")
public class User {

    @Id
    private String id;

    @NotNull
    @Field(name = "user_id")
    private String userId;

    @NotNull
    @Field(name = "full_name")
    private String fullName;

    @NotNull
    @Indexed(unique = true)
    @Email
    @Field(name = "email_id")
    private String emailId;

    @Field(name = "is_verified")
    private String isVerified;

    @Field(name = "is_active")
    private String isActive;

    @CreatedDate
    @Field(name = "created_at")
    private Long createdAt;

    @LastModifiedDate
    @Field(name = "updated_at")
    private Long updatedAt;

}
