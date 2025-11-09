package ai.smartdoc.garage.infra.qdrant.internal;

import lombok.Data;

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
