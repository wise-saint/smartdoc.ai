package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.qdrant.QdrantPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
class QdrantService implements QdrantPort {

    @Autowired
    QdrantClient qdrantClient;

    @Override
    public String upsertPoints(List<Chunk> chunks, List<List<Float>> embeddingVectors) throws IOException {
        if (chunks.size() != embeddingVectors.size()) {
            throw new GarageException("Chunks and Embedding Vectors size doesn't match", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String docId = UUID.randomUUID().toString();
        List<QdrantPoint> qdrantPoints = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            QdrantPoint.QdrantPayload qdrantPayload = new QdrantPoint.QdrantPayload();
            qdrantPayload.setDocId(docId);
            qdrantPayload.setChunk(chunks.get(i));
            qdrantPayload.setCreatedAt(System.currentTimeMillis());

            QdrantPoint qdrantPoint = new QdrantPoint();
            qdrantPoint.setId(UUID.randomUUID().toString());
            qdrantPoint.setVector(embeddingVectors.get(i));
            qdrantPoint.setPayload(qdrantPayload);

            qdrantPoints.add(qdrantPoint);
        }
        qdrantClient.upsertPoints(qdrantPoints);
        return docId;
    }
}
