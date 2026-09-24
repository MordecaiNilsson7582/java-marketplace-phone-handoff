package marketplace;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

public final class InfraiClient {
    private static final String SEND_CODE_CAPABILITY = "auth.phone.send_code";
    private final HttpClient http;
    private final String baseUrl;
    private final String apiKey;

    public InfraiClient(String baseUrl, String apiKey) {
        this.http = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public String sendCode(String phone) throws IOException, InterruptedException {
        if (SEND_CODE_CAPABILITY.isEmpty()) throw new IllegalStateException("capability not configured");
        return post("/v1/auth/phone/send_code", "{\"phone\":\"" + esc(phone) + "\",\"purpose\":\"login\",\"locale\":\"en-US\"}");
    }

    public String verifyCode(String phone, String code) throws IOException, InterruptedException {
        return post("/v1/auth/phone/verify", "{\"phone\":\"" + esc(phone) + "\",\"code\":\"" + esc(code) + "\",\"login\":true}");
    }

    public String sendBuyerUpdate(String phone, String message, String idempotencyKey) throws IOException, InterruptedException {
        return post("/v1/sms/otp", "{\"to\":\"" + esc(phone) + "\",\"template\":\"" + esc(message) + "\",\"idempotency_key\":\"" + esc(idempotencyKey) + "\"}");
    }

    private String post(String path, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (!body.contains("\"ok\":true")) throw new IllegalStateException("Infrai request rejected: " + body);
        return body;
    }

    private static String esc(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
