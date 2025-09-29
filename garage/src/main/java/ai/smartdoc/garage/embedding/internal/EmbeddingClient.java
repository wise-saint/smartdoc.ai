package ai.smartdoc.garage.embedding.internal;

import ai.smartdoc.garage.common.exception.GarageException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class EmbeddingClient {

    private final OkHttpClient client;
    private final String URL;
    private final String API_KEY;
    private static final Gson GSON = new Gson();

    EmbeddingClient(@Value("${huggingface.url}") String url,
                    @Value("${huggingface.api-key}") String apiKey) {
        this.client = new OkHttpClient();
        this.URL = url;
        this.API_KEY = apiKey;
    }

    List<List<Float>> getEmbeddingVectors(List<String> chunks) {
        String jsonBody = new Gson().toJson(Map.of("inputs", chunks));
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(URL)
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
            throw new GarageException("Error connecting with Hugging Face embedding API", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
