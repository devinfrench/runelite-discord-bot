package net.runelite.discord.commands.github;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GitHubApi {

    private static final String BASE_URL = "https://api.github.com/repos/runelite/runelite";
    public static String OAUTH_TOKEN = "";

    public static String get(String endpoint) throws IOException {
        try {
            URI uri = new URI(BASE_URL + endpoint);
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Authorization", "token " + OAUTH_TOKEN);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String content = new BasicResponseHandler().handleResponse(response);
            response.close();
            return content;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

}
