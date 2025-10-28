package ai.smartdoc.garage.huggingface;

import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.entity.Message;

import java.util.List;

public interface HuggingFacePort {

    List<List<Float>> getEmbeddingVectors(List<Chunk> chunks);

    List<Float> getEmbeddingVectors(String text);

    String completeChat(String context, String question, List<Message> chatHistory);
}
