package ai.smartdoc.garage.qdrant.internal;

import lombok.Data;
import java.util.List;

@Data
class QdrantPoint {
    private String id;
    private List<Float> vector;
    private QdrantPayload payload;

    @Data
    public static class QdrantPayload {
        private String chatId;
        private String docId;
        private Integer chunkIndex;
        private Long createdAt;
    }
}

