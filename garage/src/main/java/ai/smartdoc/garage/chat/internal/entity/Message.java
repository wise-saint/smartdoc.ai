package ai.smartdoc.garage.chat.internal.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("messages")
public class Message {

    @Id
    private String id;

    @Indexed
    @Field(name = "chat_id")
    private String chatId;

    @Field(name = "message_id")
    private String messageId;

    @Field(name = "sender")
    private String sender;

    @CreatedDate
    @Field(name = "sent_at")
    private Long sentAt;

    @Field(name = "message")
    private String message;
}
