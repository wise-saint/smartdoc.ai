package ai.smartdoc.garage.chat.internal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
//@Document("chunks") // Changed to chunks_128 and chunks_768
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chunk {

    @Id
    private String id;

    @Field(name = "chat_id")
    private String chatId;

    @Field(name = "doc_id")
    private String docId;

    @Field(name = "chunk_index") // Ordered position in its document.
    private Integer chunkIndex;

    @Field(name = "original_text")
    private String originalText;

    @Field(name = "preprocessed_text")
    private String preprocessedText;

    @Transient
    private Double score;
}
