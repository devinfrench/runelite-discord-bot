package net.runelite.discord.commands.wiki;

import net.runelite.discord.commands.Command;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Wiki implements Command {

    private static final String WIKI_URL = "<https://github.com/runelite/runelite/wiki>";
    private Map<List<String>, String> articles = new ConcurrentHashMap<>();

    public Wiki() {
        load();
    }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (articles.isEmpty()) {
            return;
        }
        if (args.isEmpty()) {
            event.getChannel().sendMessage(WIKI_URL);
        } else {
            String term = String.join(" ", args);
            String article = findArticle(term);
            if (article != null) {
                event.getChannel().sendMessage("<" + article + ">");
            }
        }
    }

    private void load() {
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "wiki.json");
            String articlesJson = FileUtils.readFileToString(file, "UTF-8");
            JSONArray ja = (JSONArray) new JSONTokener(articlesJson).nextValue();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String[] keywords = jo.getString("keywords").split(",");
                articles.put(Arrays.asList(keywords), jo.getString("article"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findArticle(String term) {
        for (List<String> keywords : articles.keySet()) {
            if (keywords.contains(term.toLowerCase())) {
                return articles.get(keywords);
            }
        }
        return null;
    }

}
