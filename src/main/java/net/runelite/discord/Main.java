package net.runelite.discord;

import net.runelite.discord.client.Client;
import net.runelite.discord.commands.CommandHandler;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public class Main {

    public static void main(String[] args) {
        IDiscordClient client = Client.createClient(args[0], true);
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new CommandHandler());
    }

}
