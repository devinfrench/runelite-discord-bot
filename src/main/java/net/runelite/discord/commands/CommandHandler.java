package net.runelite.discord.commands;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.*;

public class CommandHandler {

    private static final String COMMAND_PREFIX = "!";
    private static Map<String, Command> commands = new HashMap<>();

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        String[] argArray = event.getMessage().getContent().split(" ");
        if (argArray.length == 0 || !argArray[0].startsWith(COMMAND_PREFIX)) {
            return;
        }
        String command = argArray[0].substring(1);
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);
        if (commands.containsKey(command)) {
            commands.get(command).execute(event, args);
        }
    }

}
