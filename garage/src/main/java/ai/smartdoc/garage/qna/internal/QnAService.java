package ai.smartdoc.garage.qna.internal;

import ai.smartdoc.garage.GarageApplication;
import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.dto.QdrantSearchPoint;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.huggingface.HuggingFacePort;
import ai.smartdoc.garage.qdrant.QdrantPort;
import ai.smartdoc.garage.qna.QnAPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class QnAService implements QnAPort {

    @Autowired
    HuggingFacePort huggingFacePort;

    @Autowired
    QdrantPort qdrantPort;

    @Override
    public String askQuestion(String docId, String question) {
        List<Float> embeddingVector = huggingFacePort.getEmbeddingVectors(question);
        List<QdrantSearchPoint> qdrantSearchPoints = qdrantPort.queryPoints(embeddingVector, docId);

        String context = "";
        if (qdrantSearchPoints != null) {
            StringBuilder contextBuilder = new StringBuilder();
            for (int i = 0; i < qdrantSearchPoints.size(); i++) {
                QdrantSearchPoint point = qdrantSearchPoints.get(i);
                if (point.getScore() != null && point.getScore() > 0.6 &&
                        point.getPayload() != null && point.getPayload().getChunk() != null) {
                    Chunk chunk = point.getPayload().getChunk();
                    if (chunk.getText() != null && !chunk.getText().isEmpty()) {
                        contextBuilder.append("Chunk ").append(i + 1).append(": ")
                                .append(chunk.getText()).append("\n");
                        if (contextBuilder.length() == 3) break;
                    }
                }
            }
            context = contextBuilder.toString();
        }

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
