package ai.smartdoc.garage.embedding;

import ai.smartdoc.garage.common.dto.Chunk;

import java.util.List;

public interface EmbeddingPort {

    List<List<Float>> getEmbeddingVectors(List<Chunk> chunks);
}
