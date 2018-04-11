package net.runelite.discord.commands.github;

import net.runelite.discord.commands.Command;
import org.json.JSONObject;
import org.json.JSONTokener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class GitHub implements Command {

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            return;
        }
        int number;
        try {
            number = Integer.valueOf(args.get(0));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        if (number > 0) {
            String htmlURL = getHtmlUrl(number);
            if (htmlURL != null) {
                event.getChannel().sendMessage(htmlURL);
            }
        }
    }

    private String getHtmlUrl(int number) {
        String json = getJson(number, "/pulls/");
        json = json == null ? getJson(number, "/issues/") : json;
        if (json != null) {
            JSONObject jo = (JSONObject) new JSONTokener(json).nextValue();
            return jo.getString("html_url");
        }
        return null;
    }

    private String getJson(int number, String endpoint) {
        try {
            String response = GitHubApi.get(endpoint + number);
            if (response != null) {
                return response;
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
