package net.runelite.discord.commands.custom;

import net.runelite.discord.Bot;
import net.runelite.discord.commands.Command;
import net.runelite.discord.commands.CommandHandler;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class AddCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty() || Bot.roles.isEmpty()
            || !event.getAuthor().hasRole(Bot.roles.get("contributor"))
            && !event.getAuthor().hasRole(Bot.roles.get("admin"))) {
            return;
        }
        String command = args.remove(0);
        command = command.replace("!", "");
        String response = String.join(" ", args);
        CustomCommands.commands.put(command, response);
        CustomCommands.save();
        if (CustomCommands.commands.containsKey(command)) {
            EmbedObject embedObject = new EmbedObject();
            embedObject.title = CommandHandler.COMMAND_PREFIX + command;
            embedObject.description = response;
            embedObject.color = Integer.parseInt("00ff00", 16);
            event.getChannel().sendMessage("Command Added", embedObject);
        }
    }

}
