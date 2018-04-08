package net.runelite.discord.commands.runelite;

import net.runelite.discord.commands.Command;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class Help implements Command {

    private static final int EMBED_COLOR = Integer.parseInt("BB0A0A", 16);
    private static final String EMBED_TITLE = "Help";
    private static final String EMBED_DESCRIPTION = "If you need help, feel free to ask in #runelite.\n" +
        "\n" +
        "If you have found a bug or want to request a feature or enhancement, open an issue over at https://github.com/runelite/runelite/issues/new.\n" +
        "Make sure you look at the existing issues so you don't make a duplicate.\n" +
        "\n" +
        "If you don't have a GitHub account, feel free to report it in #runelite, and someone should add an issue on GitHub.\n" +
        "\n" +
        "**Why is my bug report, feature request or pull request not being processed?**\n" +
        "Be patient. It takes time to look them through, but rest assured knowing that it will be processed sooner or later.\n" +
        "\n" +
        "**How do I enable XP drops and zoom unlimiter?**\n" +
        "We use the game's xp drops and zoom system, so enable those in the game. The zoom unlimiter works by unlimiting the game's zoom feature.";
    private static final EmbedObject HELP_EMBED = new EmbedObject(
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
        event.getChannel().sendMessage(HELP_EMBED);
    }

}
