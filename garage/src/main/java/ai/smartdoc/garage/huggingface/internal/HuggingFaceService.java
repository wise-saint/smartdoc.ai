package ai.smartdoc.garage.huggingface.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.huggingface.HuggingFacePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class HuggingFaceService implements HuggingFacePort {

    @Autowired
    HuggingFaceClient huggingFaceClient;

    @Override
    public List<List<Float>> getEmbeddingVectors(List<Chunk> chunks) {
        List<String> chunkTextList = new ArrayList<>();
        for (Chunk chunk: chunks) {
            chunkTextList.add(chunk.getText());
        }
        return huggingFaceClient.getEmbeddingVectors(chunkTextList);
    }

    @Override
    public List<Float> getEmbeddingVectors(String text) {
        List<List<Float>> embeddingVectors = huggingFaceClient.getEmbeddingVectors(List.of(text));
        if (embeddingVectors.isEmpty()) {
            throw new GarageException("Failed to get embedding vector", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return embeddingVectors.get(0);
    }

    @Override
    public String completeChat(String context, String question) {
        ChatCompletionResponse response = huggingFaceClient.completeChat(context, question);
        String content = null;
        if (response.getChoices() != null && !response.getChoices().isEmpty()) {
            ChatCompletionResponse.Choice choice = response.getChoices().get(0);
            if (choice.getMessage() != null) {
                content = choice.getMessage().getContent();
            }
        }
        return content;
    }
}
