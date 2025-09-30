package ai.smartdoc.garage.qna.internal;

import ai.smartdoc.garage.common.dto.QdrantSearchPoint;
import ai.smartdoc.garage.embedding.EmbeddingPort;
import ai.smartdoc.garage.qdrant.QdrantPort;
import ai.smartdoc.garage.qna.QnAPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class QnAService implements QnAPort {

    @Autowired
    EmbeddingPort embeddingPort;

    @Autowired
    QdrantPort qdrantPort;

    @Override
    public Object askQuestion(String docId, String question) {
        List<Float> embeddingVector = embeddingPort.getEmbeddingVectors(question);
        List<QdrantSearchPoint> qdrantSearchPoints = qdrantPort.queryPoints(embeddingVector, docId);
        return qdrantSearchPoints;
    }
}
