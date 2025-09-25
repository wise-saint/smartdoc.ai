package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import lombok.Data;
import java.util.List;

@Data
class QdrantPoint {
    private String id;
    private List<Float> vector;
    private QdrantPayload payload;

    @Data
    public static class QdrantPayload {
        private String docId;
        private Chunk chunk;
        private Long createdAt;
    }
}

