package ai.smartdoc.garage.embedding.internal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class EmbeddingClient {

    private final OkHttpClient client;
    private final String URL;
    private final String API_KEY;

    EmbeddingClient(@Value("${huggingface.url}") String url,
                    @Value("${huggingface.api-key}") String apiKey) {
        this.client = new OkHttpClient();
        this.URL = url;
        this.API_KEY = apiKey;
    }

    List<List<Float>> getEmbeddingVectors(List<String> chunks) throws IOException {
        String jsonBody = new Gson().toJson(Map.of("inputs", chunks));
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " +  response);
            }
            String responseBody = response.body().string();
            JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
            List<List<Float>> embeddingVectors = new ArrayList<>();
            for (JsonElement jsonElement: jsonArray) {
                List<Float> embeddingVector = new ArrayList<>();
                JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                for (JsonElement jsonElement1: jsonArray1) {
                    embeddingVector.add(jsonElement1.getAsFloat());
                }
                embeddingVectors.add(embeddingVector);
            }
            response.close();
            return embeddingVectors;
        } catch (Exception e) {
            throw e;
        }
    }
}
