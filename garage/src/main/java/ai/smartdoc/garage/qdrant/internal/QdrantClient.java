package ai.smartdoc.garage.qdrant.internal;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import okhttp3.*;

import java.io.IOException;
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

    public Object upsertPoints(List<QdrantPoint> qdrantPoints) throws IOException {
        String jsonBody = new Gson().toJson(Map.of("points", qdrantPoints));
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/pdfText/points")
                .put(body)
                .addHeader("api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Points upserted successfully: " + response.body().string());
            } else {
                System.err.println("Request failed: " + response.code() + " " + response.body().string());
            }
        }
        return null;
    }
}
