package ai.smartdoc.garage.infra.huggingface.internal;

import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.infra.huggingface.HuggingFacePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Service
class HuggingFaceService implements HuggingFacePort {

    @Autowired
    ExecutorService executor;

    @Autowired
    HuggingFaceClient huggingFaceClient;

    @Override
    public List<List<Float>> getEmbeddingVectors(List<Chunk> chunks) {
        int batchSize = 16;
        List<List<Float>> embeddingVectors = new ArrayList<>(Collections.nCopies(chunks.size(), null));
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i += batchSize) {
            int start = i;
            int end = Math.min(i + batchSize, chunks.size());
            List<Chunk> batch = chunks.subList(start, end);
            Future<?> f = executor.submit(() -> {
                List<String> texts = batch.stream().map(Chunk::getPreprocessedText).toList();
                List<List<Float>> batchVectors = huggingFaceClient.getEmbeddingVectors(texts);
                for (int k = 0; k < batch.size(); k++) {
                    embeddingVectors.set(start + k, batchVectors.get(k));
                }
            });
            futures.add(f);
        }
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                throw new GarageException("Embedding batch failed " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return embeddingVectors;
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
    public String completeChat(String context, String question, List<Message> chatHistory) {
        ChatCompletionResponse response = huggingFaceClient.completeChat(context, question, chatHistory);
        String content = null;
        if (response.getChoices() != null && !response.getChoices().isEmpty()) {
            ChatCompletionResponse.Choice choice = response.getChoices().get(0);
            if (choice.getMessage() != null) {
                content = choice.getMessage().getContent();
                content = content.replaceAll("(?s)<think>.*?</think>", "").trim();
            }
        }
        return content;
    }
}
