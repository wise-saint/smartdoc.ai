package ai.smartdoc.garage.qna.internal;

import ai.smartdoc.garage.cohere.CoherePort;
import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.dto.CohereRerankResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.huggingface.HuggingFacePort;
import ai.smartdoc.garage.qdrant.QdrantPort;
import ai.smartdoc.garage.qna.QnAPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class QnAService implements QnAPort {

    @Autowired
    HuggingFacePort huggingFacePort;

    @Autowired
    QdrantPort qdrantPort;

    @Autowired
    CoherePort coherePort;

    @Override
    public String askQuestion(String docId, String question) {
        List<Float> embeddingVector = huggingFacePort.getEmbeddingVectors(question);
        List<Chunk> chunkList = qdrantPort.queryPoints(embeddingVector, docId, 20);
        List<String> documents = new ArrayList<>();
        for (Chunk chunk: chunkList) {
            if (chunk != null && chunk.getText() != null && !chunk.getText().isEmpty()) {
                documents.add(chunk.getText());
            }
        }
        List<CohereRerankResponse> cohereRerankResponseList = coherePort.cohereRerank(question, documents, 5);

        String context = "";
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < cohereRerankResponseList.size(); i++) {
            CohereRerankResponse response = cohereRerankResponseList.get(i);
            if (response.getScore()  > 0.5 && response.getDocument() != null && !response.getDocument().isEmpty()) {
                contextBuilder.append("Chunk ").append(i + 1).append(": ")
                        .append(response.getDocument()).append("\n");
                if (contextBuilder.length() == 3) break;
            }
        }
        context = contextBuilder.toString();

        String answer = null;
        if (!context.isEmpty()) {
            answer = huggingFacePort.completeChat(context, question);
        }
        if (answer == null || answer.isEmpty()) {
            throw new GarageException("Failed to generate answer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return answer;
    }
}
