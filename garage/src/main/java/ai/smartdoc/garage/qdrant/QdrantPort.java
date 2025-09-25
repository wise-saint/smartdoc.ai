package ai.smartdoc.garage.qdrant;

import ai.smartdoc.garage.common.dto.Chunk;

import java.io.IOException;
import java.util.List;

public interface QdrantPort {

    String upsertPoints(List<Chunk> chunks, List<List<Float>> embeddingVectors) throws IOException;
}
