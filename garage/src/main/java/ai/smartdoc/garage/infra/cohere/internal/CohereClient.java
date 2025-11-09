package ai.smartdoc.garage.infra.cohere.internal;

import com.cohere.api.Cohere;
import com.cohere.api.resources.v2.requests.V2RerankRequest;
import com.cohere.api.resources.v2.types.V2RerankResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class CohereClient {

    private final Cohere cohere;

    CohereClient(@Value("${cohere.api-key}") String apiKey) {
        this.cohere = Cohere.builder()
                .token(apiKey)
                .clientName("smartdoc")
                .timeout(30000)
                .maxRetries(2)
                .build();
    }

    public V2RerankResponse rerank(String query, List<String> documents, int topN) {
        V2RerankRequest request = V2RerankRequest.builder()
                .model("rerank-v3.5")
                .query(query)
                .documents(documents)
                .topN(topN)
                .build();
        return cohere.v2().rerank(request);
    }
}
