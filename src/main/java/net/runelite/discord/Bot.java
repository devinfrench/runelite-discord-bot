package net.runelite.discord;

import net.runelite.discord.client.Client;
import net.runelite.discord.commands.CommandHandler;
import net.runelite.discord.commands.github.GitHubApi;
import net.runelite.discord.presence.SessionsUpdater;
import net.runelite.discord.presence.twitch.Twitch;
import net.runelite.discord.presence.twitch.TwitchApi;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;

public class Bot {

    public static IGuild runelite;

    public static void main(String[] args) {
        GitHubApi.OAUTH_TOKEN = args[1];
        TwitchApi.CLIENT_ID = args[2];
        IDiscordClient client = Client.createClient(args[0], true);
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new Bot());
        dispatcher.registerListener(new CommandHandler());
        dispatcher.registerListener(new SessionsUpdater());
        dispatcher.registerListener(new Twitch());
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        for (IGuild guild : event.getClient().getGuilds()) {
            if (guild.getName().equals("RuneLite")) {
                runelite = guild;
            }
        }
    }

}
