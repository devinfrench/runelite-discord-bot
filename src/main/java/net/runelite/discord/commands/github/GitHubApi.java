package net.runelite.discord.commands.github;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public class GitHubApi {

    private static final String BASE_URL = "https://api.github.com/repos/runelite/runelite";

    public static String get(String endpoint) throws IOException {
        return IOUtils.toString(new URL(BASE_URL + endpoint).openStream(), "UTF-8");
    }

}
