package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.qdrant.QdrantPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
class QdrantService implements QdrantPort {

    @Autowired
    QdrantClient qdrantClient;

    @Override
    public String upsertPoints(List<Chunk> chunks, List<List<Float>> embeddingVectors) {
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

        UpsertResponse response = qdrantClient.upsertPoints(qdrantPoints);
        if (!response.getStatus().equals("ok")) {
            throw new GarageException("Failed to upsert points in Qdrant", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return docId;
    }

    @Override
    public List<Chunk> queryPoints(List<Float> queryVector, String docId, Integer topK) {
        SearchResponse searchResponse = qdrantClient.queryPoints(queryVector, topK, docId);
        List<Chunk> chunkList = new ArrayList<>();
        if (searchResponse.getStatus().equals("ok") && searchResponse.getResult() != null) {
            SearchResponse.Result result = searchResponse.getResult();
            if (result.getPoints() != null) {
                for (QdrantSearchPoint point: result.getPoints()) {
                    if (point.getPayload() != null && point.getPayload().getChunk() != null) {
                        chunkList.add(point.getPayload().getChunk());
                    }
                }
            }
        }
        return chunkList;
    }
}
