package ai.smartdoc.garage.huggingface;

import ai.smartdoc.garage.common.dto.Chunk;

import java.util.List;

public interface HuggingFacePort {

    List<List<Float>> getEmbeddingVectors(List<Chunk> chunks);

    List<Float> getEmbeddingVectors(String text);

    String completeChat(String context, String question);
}
