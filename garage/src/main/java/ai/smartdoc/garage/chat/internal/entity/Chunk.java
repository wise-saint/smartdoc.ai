package ai.smartdoc.garage.chat.internal.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("chunks")
@Builder
@CompoundIndexes({
        @CompoundIndex(name = "doc_chunk_idx", def = "{'doc_id': 1, 'chunk_index': 1}")
})
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
}
