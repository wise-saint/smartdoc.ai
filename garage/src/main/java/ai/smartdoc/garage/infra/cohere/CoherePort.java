package ai.smartdoc.garage.infra.cohere;

import ai.smartdoc.garage.common.dto.CohereRerankResponse;

import java.util.List;

public interface CoherePort {

    List<CohereRerankResponse> cohereRerank(String query, List<String> documents, int topN);
}
