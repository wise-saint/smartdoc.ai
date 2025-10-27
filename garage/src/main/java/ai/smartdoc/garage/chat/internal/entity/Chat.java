package ai.smartdoc.garage.chat.internal.entity;

import ai.smartdoc.garage.common.utils.IdCreator;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Data
@Document("chats")
public class Chat {

    @Id
    private String id;

    @Indexed
    @Field(name = "chat_id")
    private String chatId;

    @Field(name = "chat_title")
    private String chatTitle;

    @Indexed
    @Field(name = "user_id")
    private String userId;

    @Field(name = "documents")
    private List<Document> documents;

    @CreatedDate
    @Field(name = "created_at")
    private Long createdAt;

    @LastModifiedDate
    @Field(name = "updated_at")
    private Long updatedAt;

    public Chat(String userId) {
        this.chatId = IdCreator.createId(Chat.class);
        this.userId = userId;
        this.chatTitle = "New Chat";
    }

    @Data
    @Builder
    public static class Document {
        @Field(name = "doc_id")
        private String docId;

        @Field(name = "doc_name")
        private String docName;

        @Field(name = "doc_path")
        private String docPath;

        @Field(name = "uploaded_at")
        private Long uploadedAt;
    }
}
