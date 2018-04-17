package net.runelite.discord.commands;

import net.runelite.discord.commands.custom.CustomCommands;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class Help implements Command {

    private static final String EMBED_COLOR_HEX = "60D1F6";

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        EmbedObject embedObject = new EmbedObject();
        embedObject.title = "Custom Commands";
        embedObject.color = Integer.parseInt(EMBED_COLOR_HEX, 16);
        String[] commands = CustomCommands.commands.keySet().toArray(new String[CustomCommands.commands.size()]);
        EmbedObject.EmbedFieldObject[] fields = new EmbedObject.EmbedFieldObject[commands.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new EmbedObject.EmbedFieldObject(
                CommandHandler.COMMAND_PREFIX + commands[i], "--", false
            );
        }
        embedObject.fields = fields;
        event.getChannel().sendMessage(embedObject);
    }

}
