package net.runelite.discord.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface Command {

    void execute(MessageReceivedEvent event, List<String> args);

}
