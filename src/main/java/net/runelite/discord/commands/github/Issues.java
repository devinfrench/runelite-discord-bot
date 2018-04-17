package net.runelite.discord.commands.github;

import net.runelite.discord.commands.Command;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Issues extends Thread implements Command {

    private static final String ISSUE_BASE_URL = "https://github.com/runelite/runelite/issues/";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Map<Integer, String> issues = new ConcurrentHashMap<>();

    public Issues() {
        scheduler.scheduleAtFixedRate(this, 0, 10, TimeUnit.MINUTES);
    }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (!issues.isEmpty() && !args.isEmpty()) {
            int mostMatches = 0;
            int issue = 0;
            for (int number : issues.keySet()) {
                String title = issues.get(number).toLowerCase();
                int matches = 0;
                for (String arg : args) {
                    if (title.contains(arg)) {
                        matches++;
                    }
                }
                if (matches > mostMatches) {
                    issue = number;
                    mostMatches = matches;
                }
            }
            if (issue > 0) {
                event.getChannel().sendMessage(ISSUE_BASE_URL + issue);
            } else {
                event.getChannel().sendMessage("No open issue found.");
            }
        }
    }

    @Override
    public void run() {
        populateIssues();
    }

    private void populateIssues() {
        issues.clear();
        boolean hasIssues = true;
        int page = 1;
        while (hasIssues) {
            try {
                String json = GitHubApi.get("/issues?status=open&per_page=100&page=" + page);
                if (json != null) {
                    JSONArray ja = (JSONArray) new JSONTokener(json).nextValue();
                    if (ja.length() > 0) {
                        for (Object obj : ja) {
                            JSONObject jsonObj = (JSONObject) obj;
                            if (jsonObj.getString("html_url").contains("issue")) {
                                issues.put(jsonObj.getInt("number"), jsonObj.getString("title"));
                            }
                        }
                        page++;
                    } else {
                        hasIssues = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
