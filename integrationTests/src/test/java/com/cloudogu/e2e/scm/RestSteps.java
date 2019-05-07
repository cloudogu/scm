package com.cloudogu.e2e.scm;

import com.cloudogu.e2e.Config;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.gauge.Step;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

import static com.cloudogu.e2e.Config.BASE_URL;

public class RestSteps {

    @Step("REST: Verify can read repositories with configured username and password")
    public void verifyReadRepositories() throws IOException {
        URL url = new URL(BASE_URL + "/scm/api/v2/repositories");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        String encoded = Base64.getEncoder().encodeToString((Config.ADMIN_USERNAME + ":" + Config.ADMIN_PASSWORD).getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + encoded);
        assertThat(connection.getResponseCode()).isEqualTo(200);
    }

    @Step("REST: Verify values from Me page equal configured values")
    public void verifyMeData() throws IOException {
        URL url = new URL(BASE_URL + "/scm/api/v2/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        String encoded = Base64.getEncoder().encodeToString((Config.ADMIN_USERNAME + ":" + Config.ADMIN_PASSWORD).getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + encoded);
        assertThat(connection.getResponseCode()).isEqualTo(200);
        JsonObject content = getContent(connection);
        assertThat(content.get("name").getAsString()).isEqualTo(Config.ADMIN_USERNAME);
        assertThat(content.get("displayName").getAsString()).isEqualTo(Config.DISPLAY_NAME);
        assertThat(content.get("mail").getAsString()).isEqualTo(Config.EMAIL);
        assertThat(content.get("groups").getAsJsonArray()).extracting(JsonElement::getAsString).contains(Config.ADMIN_GROUP);
    }

    private JsonObject getContent(HttpURLConnection connection) throws IOException {
        JsonParser jsonParser = new JsonParser();
        JsonElement root = jsonParser.parse(new InputStreamReader(connection.getInputStream()));
        return root.getAsJsonObject();
    }

    static {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }
}
