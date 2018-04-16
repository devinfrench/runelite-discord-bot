package net.runelite.discord.commands;

import net.runelite.discord.commands.custom.AddCommand;
import net.runelite.discord.commands.custom.CustomCommands;
import net.runelite.discord.commands.custom.DelCommand;
import net.runelite.discord.commands.github.GitHub;
import net.runelite.discord.commands.github.Issues;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.*;

public class CommandHandler {

    public static final String COMMAND_PREFIX = "!";
    private static Map<String, Command> commands = new HashMap<>();

    static {
        CustomCommands.load();
        commands.put("gh", new GitHub());
        commands.put("issue", new Issues());
        commands.put("add", new AddCommand());
        commands.put("del", new DelCommand());
    }

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
        } else if (CustomCommands.commands.containsKey(command)) {
            event.getChannel().sendMessage(CustomCommands.commands.get(command));
        }
    }

}
