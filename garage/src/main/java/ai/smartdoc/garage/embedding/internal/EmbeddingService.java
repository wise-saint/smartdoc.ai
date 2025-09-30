package ai.smartdoc.garage.embedding.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.embedding.EmbeddingPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class EmbeddingService implements EmbeddingPort {

    @Autowired
    EmbeddingClient embeddingClient;

    @Override
    public List<List<Float>> getEmbeddingVectors(List<Chunk> chunks) {
        List<String> chunkTextList = new ArrayList<>();
        for (Chunk chunk: chunks) {
            chunkTextList.add(chunk.getText());
        }
        return embeddingClient.getEmbeddingVectors(chunkTextList);
    }

    @Override
    public List<Float> getEmbeddingVectors(String text) {
        List<List<Float>> embeddingVectors = embeddingClient.getEmbeddingVectors(List.of(text));
        if (embeddingVectors.isEmpty()) {
            throw new GarageException("Failed to get embedding vector", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return embeddingVectors.get(0);
    }
}
