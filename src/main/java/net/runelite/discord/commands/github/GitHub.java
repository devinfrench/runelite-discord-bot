package net.runelite.discord.commands.github;

import net.runelite.discord.commands.Command;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GitHub implements Command {

    private static final String BASE_URL = "https://api.github.com/repos/runelite/runelite/";

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            return;
        }
        if (args.get(0).startsWith("#")) {
            int number;
            try {
                number = Integer.valueOf(args.get(0).substring(1));
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
    }

    private String getHtmlUrl(int number) {
        String url = getPullUrl(number);
        return url == null ? getIssueUrl(number) : url;
    }

    private String getPullUrl(int number) {
        try {
            JSONObject jo = (JSONObject) new JSONTokener(IOUtils.toString(new URL(BASE_URL + "pulls/" + number).openStream(), "UTF-8")).nextValue();
            return jo.getString("html_url");
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getIssueUrl(int number) {
        try {
            JSONObject jo = (JSONObject) new JSONTokener(IOUtils.toString(new URL(BASE_URL + "issues/" + number).openStream(), "UTF-8")).nextValue();
            return jo.getString("html_url");
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
