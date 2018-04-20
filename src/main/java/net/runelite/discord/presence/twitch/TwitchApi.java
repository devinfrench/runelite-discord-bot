package net.runelite.discord.presence.twitch;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TwitchApi {

    private static final String BASE_URL = "https://api.twitch.tv/kraken/";
    public static String CLIENT_ID = "";

    public static Stream getStream(String id) {
        String json = getStreamJson(id);
        if (json == null) {
            return null;
        }
        try {
            JSONObject jo = (JSONObject) new JSONTokener(json).nextValue();
            jo = jo.getJSONObject("stream");
            JSONObject joChannel = jo.getJSONObject("channel");
            JSONObject joPreview = jo.getJSONObject("preview");
            return new Stream(
                joChannel.getString("display_name"),
                joChannel.getString("status"),
                jo.getString("game"),
                jo.getInt("viewers"),
                jo.getString("stream_type"),
                joPreview.getString("large"),
                joChannel.getString("logo")
            );
        } catch (JSONException e) {
        }

        return null;
    }

    private static String getStreamJson(String id) {
        return get("streams/" + id);
    }

    private static String get(String params) {
        try {
            URI uri = new URI(BASE_URL + params);
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Client-ID", CLIENT_ID);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String content = new BasicResponseHandler().handleResponse(response);
            response.close();
            return content;
        } catch (IOException | URISyntaxException e) {
        }

        return null;
    }

    public static class Stream {

        private String displayName;
        private String title;
        private String game;
        private int viewers;
        private final String type;
        private String preview;
        private String logo;

        public Stream(String displayName, String title, String game, int viewers, String type, String preview, String logo) {
            this.displayName = displayName;
            this.title = title;
            this.game = game;
            this.viewers = viewers;
            this.type = type;
            this.preview = preview;
            this.logo = logo;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTitle() {
            return title;
        }

        public String getGame() {
            return game;
        }

        public int getViewers() {
            return viewers;
        }

        public String getPreview() {
            return preview;
        }

        public String getLogo() {
            return logo;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Stream{" +
                "displayName='" + displayName + '\'' +
                ", title='" + title + '\'' +
                ", game='" + game + '\'' +
                ", viewers=" + viewers +
                ", type=" + type +
                ", preview='" + preview + '\'' +
                ", logo='" + logo + '\'' +
                '}';
        }
    }

}
