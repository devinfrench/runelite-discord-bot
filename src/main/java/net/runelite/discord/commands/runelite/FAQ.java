package net.runelite.discord.commands.runelite;

import net.runelite.discord.commands.Command;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class FAQ implements Command {

    private static final int EMBED_COLOR = Integer.parseInt("00AEDF", 16);
    private static final String EMBED_TITLE = "Frequently Asked Questions";
    private static final String EMBED_DESCRIPTION = "**What is open source?**\n" +
        "RuneLite being open source means that the code can be inspected by anybody.\n" +
        "\n" +
        "Changes to our software, that anyone can submit, are passed through an audit and acceptance process, where we make sure that the changes don't contain any malicious or rule-breaking code. This makes sure that you won't be running such code.\n" +
        "\n" +
        "**Will using RuneLite get me banned?**\n" +
        "No.\n" +
        "\n" +
        "**How often is RuneLite updated?**\n" +
        "If you're using the launcher, count on seeing updates every week on Thursday. These Thursday releases are done after the game has updated, and can take a few hours if there has been an engine change in the game.\n" +
        "\n" +
        "**Since when is RuneLite available?**\n" +
        "The code was made public in 2016, but the development and popularity really started accelerating in 2017 and 2018. Check out https://github.com/runelite/runelite/graphs/contributors for more detailed information.\n" +
        "\n" +
        "**Can I discuss other clients?**\n" +
        "Yes. As long as they are not bot clients.\n" +
        "\n" +
        "**How do I get the contributor or cool person rank?**\n" +
        "These ranks are given to people who have made notably great contributions to the project.";
    private static final EmbedObject FAQ_EMBED = new EmbedObject(
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
        event.getChannel().sendMessage(FAQ_EMBED);
    }

}
