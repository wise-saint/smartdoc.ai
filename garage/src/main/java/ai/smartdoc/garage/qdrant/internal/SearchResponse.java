package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.dto.QdrantSearchPoint;
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
