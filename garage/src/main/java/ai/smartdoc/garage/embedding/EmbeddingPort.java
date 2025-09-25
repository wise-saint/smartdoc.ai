package ai.smartdoc.garage.embedding;

import ai.smartdoc.garage.common.dto.Chunk;

import java.io.IOException;
import java.util.List;

public interface EmbeddingPort {

    List<List<Float>> getEmbeddingVectors(List<Chunk> chunks) throws IOException;
}
