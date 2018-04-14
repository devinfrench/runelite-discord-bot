package net.runelite.discord.presence;

import net.runelite.discord.commands.github.GitHubApi;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SessionsUpdater extends Thread {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

    private IDiscordClient client;

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        client = event.getClient();
        changePresence();
        start();
    }

    @Override
    public void run() {
        Instant minute = Instant.now().plus(1, ChronoUnit.MINUTES);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            Instant now = Instant.now();
            if (client != null && now.isAfter(minute)) {
                changePresence();
                minute = now.plus(1, ChronoUnit.MINUTES);
            }
        }
    }

    private void changePresence() {
        int sessions = getRuneLiteSessionCount();
        if (sessions > -1) {
            client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, sessions + " players online");
        }
    }

    private int getRuneLiteSessionCount() {
        String version = getRuneLiteVersion();
        if (version != null) {
            try {
                URIBuilder builder = new URIBuilder();
                builder.setScheme("https").setHost("api.runelite.net").setPath("/" + version + "/session/count")
                    .setParameter("user-agent", USER_AGENT);
                URI uri = builder.build();
                HttpGet httpGet = new HttpGet(uri);
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(httpGet);
                String sessions = new BasicResponseHandler().handleResponse(response);
                response.close();
                return Integer.parseInt(sessions);
            } catch (URISyntaxException | IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private String getRuneLiteVersion() {
        String tag = getLatestTag();
        return tag != null ? tag.replace("parent-", "") : null;
    }

    private String getLatestTag() {
        try {
            String tagsJson = GitHubApi.get("/tags");
            if (tagsJson != null) {
                JSONArray ja = (JSONArray) new JSONTokener(tagsJson).nextValue();
                return ((JSONObject) ja.get(0)).getString("name");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
