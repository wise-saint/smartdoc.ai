package ai.smartdoc.garage.qdrant.internal;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UpsertResponse {
    private String status;
    private Result result;

    @Data
    public static class Result {
        private String status;

        @SerializedName("operation_id")
        private long operationId;
    }

    public UpsertResponse() {
        this.status = "failed";
    }
}
