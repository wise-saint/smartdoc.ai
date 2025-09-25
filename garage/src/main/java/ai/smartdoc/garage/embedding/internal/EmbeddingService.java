package ai.smartdoc.garage.embedding.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.embedding.EmbeddingPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
class EmbeddingService implements EmbeddingPort {

    @Autowired
    EmbeddingClient embeddingClient;

    @Override
    public List<List<Float>> getEmbeddingVectors(List<Chunk> chunks) throws IOException {
        List<String> chunkTextList = new ArrayList<>();
        for (Chunk chunk: chunks) {
            chunkTextList.add(chunk.getText());
        }
        return embeddingClient.getEmbeddingVectors(chunkTextList);
    }
}
