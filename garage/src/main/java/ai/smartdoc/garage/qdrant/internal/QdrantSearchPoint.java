package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

@Data
class QdrantSearchPoint {
    private Double score;
    private SearchPayload payload;

    @Data
    public static class SearchPayload {
        private String chatId;
        private String docId;
        private Integer chunkIndex;
        private Long createdAt;
    }
}
