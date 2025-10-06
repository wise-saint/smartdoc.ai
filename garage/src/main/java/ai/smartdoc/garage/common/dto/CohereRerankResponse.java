package ai.smartdoc.garage.common.dto;

import lombok.Data;

@Data
public class CohereRerankResponse {

    private float score;
    private String document;

    public CohereRerankResponse(float score, String document) {
        this.score = score;
        this.document = document;
    }

}
