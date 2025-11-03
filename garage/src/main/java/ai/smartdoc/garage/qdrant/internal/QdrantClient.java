package ai.smartdoc.garage.qdrant.internal;

import ai.smartdoc.garage.common.exception.GarageException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
class QdrantClient {

    private final OkHttpClient client;
    private final String BASE_URL;
    private final String API_KEY;

    QdrantClient(@Value("${qdrant.url}") String baseUrl,
                 @Value("${qdrant.api-key}") String apiKey) {
        this.client = new OkHttpClient();
        this.BASE_URL = baseUrl;
        this.API_KEY = apiKey;
    }

    public UpsertResponse upsertPoints(List<QdrantPoint> qdrantPoints) {
        String jsonBody = new Gson().toJson(Map.of("points", qdrantPoints));
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/knowledge-base/points")
                .put(body)
                .addHeader("api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return new Gson().fromJson(response.body().string(), UpsertResponse.class);
            } else {
                String error = response.body() != null ? response.body().string() : "Unknown error";
                throw new GarageException("Failed to upsert point in Qdrant: " + error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new GarageException("Error connecting with Qdrant", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SearchResponse queryPoints(List<Float> queryVector, int limit, String chatId) {
        Map<String, Object> body = new HashMap<>();
        body.put("query", queryVector);
        body.put("limit", limit);
        body.put("with_payload", true);
        body.put("with_vector", false);
        body.put("filter", Map.of(
                "must", List.of(Map.of(
                        "key", "chatId",
                        "match", Map.of("value", chatId)
                ))
        ));

        String jsonBody = new Gson().toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/knowledge-base/points/query")
                .post(requestBody)
                .addHeader("api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return new Gson().fromJson(response.body().string(), SearchResponse.class);
            } else {
                throw new GarageException("Qdrant query failed: " + response.code(), HttpStatus.BAD_GATEWAY);
            }
        } catch (Exception e) {
            throw new GarageException("Error querying points: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
