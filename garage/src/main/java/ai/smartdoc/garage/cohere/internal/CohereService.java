package ai.smartdoc.garage.cohere.internal;

import ai.smartdoc.garage.cohere.CoherePort;
import ai.smartdoc.garage.common.dto.CohereRerankResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import com.cohere.api.resources.v2.types.V2RerankResponse;
import com.cohere.api.resources.v2.types.V2RerankResponseResultsItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class CohereService implements CoherePort {

    @Autowired
    CohereClient cohereClient;

    @Override
    public List<CohereRerankResponse> cohereRerank(String query, List<String> documents, int topN) {
        if (documents == null || documents.isEmpty()) {
            throw new GarageException("Cannot call cohere-rerank as no documents provided", HttpStatus.PRECONDITION_FAILED);
        }
        V2RerankResponse response = cohereClient.rerank(query, documents, Math.min(documents.size(), topN));
        List<CohereRerankResponse> topDocuments = new ArrayList<>();
        if (response != null && response.getResults() != null) {
            for (V2RerankResponseResultsItem item: response.getResults()) {
                topDocuments.add(new CohereRerankResponse(item.getRelevanceScore(), documents.get(item.getIndex())));
            }
        }
        return topDocuments;
    }
}
