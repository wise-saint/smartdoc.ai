package ai.smartdoc.garage.huggingface.internal;

import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.common.exception.GarageException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
class HuggingFaceClient {

    private final OkHttpClient client;
    private final String EMBEDDING_URL;
    private final String CHAT_COMPLETION_URL;
    private final String CHAT_COMPLETION_MODEL;
    private final String API_KEY;
    private static final Gson GSON = new Gson();

    HuggingFaceClient(@Value("${huggingface.embedding-url}") String embeddingUrl,
                      @Value("${huggingface.chat-completion-url}") String chatCompletionUrl,
                      @Value("${huggingface.chat-completion-model}") String chatCompletionModel,
                      @Value("${huggingface.api-key}") String apiKey) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
        this.EMBEDDING_URL = embeddingUrl;
        this.CHAT_COMPLETION_URL = chatCompletionUrl;
        this.CHAT_COMPLETION_MODEL = chatCompletionModel;
        this.API_KEY = apiKey;
    }

    public List<List<Float>> getEmbeddingVectors(List<String> chunks) {
        String jsonBody = new Gson().toJson(Map.of("inputs", chunks));
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(EMBEDDING_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonArray jsonArray = GSON.fromJson(response.body().string(), JsonArray.class);
                List<List<Float>> embeddingVectors = new ArrayList<>();
                for (JsonElement jsonElement: jsonArray) {
                    List<Float> embeddingVector = new ArrayList<>();
                    JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                    for (JsonElement jsonElement1: jsonArray1) {
                        embeddingVector.add(jsonElement1.getAsFloat());
                    }
                    embeddingVectors.add(embeddingVector);
                }
                return embeddingVectors;
            } else {
                String error = response.body() != null ? response.body().string() : "Unknown error";
                throw new GarageException("Failed to get embedding vectors: " + error, HttpStatus.BAD_GATEWAY);
            }
        } catch (Exception e) {
            throw new GarageException("Error computing embedding vector: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ChatCompletionResponse completeChat(String context, String question, List<Message> chatHistory) {
        JsonArray messages = new JsonArray();
        messages.add(getSystemMessage());
        messages.addAll(getPastMessages(chatHistory));
        messages.add(getUserMessage(context, question));

        JsonObject payload = new JsonObject();
        payload.addProperty("model", CHAT_COMPLETION_MODEL);
        payload.add("messages", messages);

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(CHAT_COMPLETION_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return GSON.fromJson(response.body().string(), ChatCompletionResponse.class);
            } else {
                String error = response.body() != null ? response.body().toString() : "Unknown error";
                throw new GarageException("Failed to generate answer: " + error, HttpStatus.BAD_GATEWAY);
            }
        } catch (Exception e) {
            throw new GarageException("Error generating answer: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JsonObject getSystemMessage() {
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are an AI assistant. " +
                "Do not use any external knowledge or assumptions. " +
                "Generate answers strictly using the provided context. " +
                "If context is insufficient to answer the questions, don't generate answer.");
        return systemMessage;
    }

    private JsonArray getPastMessages(List<Message> messageList) {
        JsonArray pastMessages = new JsonArray();
        if (messageList != null) {
            for (Message message: messageList) {
                JsonObject pastMessage = new JsonObject();
                pastMessage.addProperty("role", message.getSender());
                pastMessage.addProperty("content", message.getMessage());
                pastMessages.add(pastMessage);
            }
        }
        return pastMessages;
    }

    private JsonObject getUserMessage(String context, String message) {
        String finalPrompt = "Use ONLY the following context to answer:\n\n"
                + context + "\nQuestion: " + message;
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", finalPrompt);
        return userMessage;
    }
}
