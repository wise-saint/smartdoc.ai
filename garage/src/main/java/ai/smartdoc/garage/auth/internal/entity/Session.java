package ai.smartdoc.garage.auth.internal.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("sessions")
public class Session {

    @Id
    private String id;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "refresh_token_hash")
    private String refreshTokenHash;

    @Field(name = "device_info")
    private String deviceInfo;

    @Field(name = "ip_address")
    private String ipAddress;

    @CreatedDate
    @Field(name = "created_at")
    private String createdAt;

    @LastModifiedDate
    @Field(name = "last_used_at")
    private String lastUsedAt;

    @Indexed(expireAfter = "0s")
    @Field(name = "expires_at")
    private String expiresAt;

    @Field(name = "rotation_counter")
    private Integer rotationCounter;

}
