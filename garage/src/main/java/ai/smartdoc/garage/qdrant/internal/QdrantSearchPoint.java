package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import lombok.Data;

@Data
public class QdrantSearchPoint {
    private Double score;
    private SearchPayload payload;

    @Data
    public static class SearchPayload {
        private String docId;
        private Chunk chunk;
        private Long createdAt;
    }
}
