package ai.smartdoc.garage.qdrant;



import ai.smartdoc.garage.chat.internal.entity.Chunk;

import java.util.List;

public interface QdrantPort {

    String upsertPoints(List<Chunk> chunks, List<List<Float>> embeddingVectors, String docId, String chatId);

    List<Chunk> queryPoints(List<Float> queryVector, String chatId, Integer topK);
}
