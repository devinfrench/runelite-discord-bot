package net.runelite.discord.commands.runelite;

import net.runelite.discord.commands.Command;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class Information implements Command {

    private static final int EMBED_COLOR = Integer.parseInt("3E75B2", 16);
    private static final String EMBED_TITLE = "Information";
    private static final String EMBED_DESCRIPTION = "**Website:** https://runelite.net/\n" +
        "\n" +
        "**Patreon:** https://www.patreon.com/user?u=10095059\n" +
        "\n" +
        "**GitHub:**  https://github.com/runelite\n" +
        "\n" +
        "**Wiki:**       https://github.com/runelite/runelite/wiki";
    private static final EmbedObject INFO_EMBED = new EmbedObject(
        EMBED_TITLE,
        null,
        EMBED_DESCRIPTION,
        null,
        null,
        EMBED_COLOR,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(INFO_EMBED);
    }

}
