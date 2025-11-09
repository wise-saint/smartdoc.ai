package ai.smartdoc.garage.infra.qdrant.internal;

import lombok.Data;

import java.util.List;

@Data
class SearchResponse {

    private String status;
    private Result result;

    @Data
    public static class Result {
        private List<QdrantSearchPoint> points;
    }
}
