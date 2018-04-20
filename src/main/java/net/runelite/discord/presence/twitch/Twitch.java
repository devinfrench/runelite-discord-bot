package net.runelite.discord.presence.twitch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.runelite.discord.Bot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

import java.util.Timer;
import java.util.TimerTask;

public class Twitch implements Runnable {

    private static final String EMBED_COLOR_HEX = "634299";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void run() {
        final MessageHistory messages = getTwitchChannel().getMessageHistory();
        final List<IUser> users = Bot.runelite.getUsersByRole(getStreamerRole());

        // Delete all non-streaming users
        for (IMessage message : messages) {
            if (message.getContent().contains("https://twitch.tv")) {
                // Remove user from the list as his message is already present in channel
                users.remove(message.getAuthor());

                final TwitchApi.Stream stream = findStream(message.getAuthor().getPresence());

                if (stream == null) {
                    message.delete();
                }
            }
        }

        // Find all users that are probably streaming and properly update discord
        for (IUser user : users) {
            final TwitchApi.Stream stream = findStream(user.getPresence());

            if (stream != null) {
                sendStreamMessage(user.getPresence(), stream);
            }
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        scheduler.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
    }

    @EventSubscriber
    public void onPresenceUpdateEvent(PresenceUpdateEvent event) {
        if (Bot.runelite == null || !event.getUser().hasRole(getStreamerRole())) {
            return;
        }

        final IPresence newPresence = event.getNewPresence();
        final IPresence oldPresence = event.getOldPresence();
        final TwitchApi.Stream stream = findStream(newPresence);

        if (stream != null && !event.getOldPresence().getStreamingUrl().isPresent()) {
            sendStreamMessage(newPresence, stream);
        } else if (stream == null && oldPresence.getStreamingUrl().isPresent()) {
            deleteStreamMessage(oldPresence.getStreamingUrl().get());
        }
    }

    private static TwitchApi.Stream findStream(final IPresence presence) {
        final boolean isStreamingSomething = presence.getActivity().isPresent()
            && presence.getActivity().get() == ActivityType.STREAMING
            && presence.getStreamingUrl().isPresent();

        if (isStreamingSomething) {
            final String id = presence.getStreamingUrl().get().replace("https://www.twitch.tv/", "");
            final TwitchApi.Stream stream = TwitchApi.getStream(id);

            if (stream == null) {
                return null;
            }

            if (!stream.getGame().contains("RuneScape")) {
                return null;
            }

            return stream;
        }

        return null;
    }

    private static IRole getStreamerRole() {
        return Bot.roles.get("streamer");
    }

    private static IChannel getTwitchChannel() {
        return Bot.channels.get("twitch");
    }

    private static void sendStreamMessage(final IPresence presence, final TwitchApi.Stream stream) {

        final EmbedObject embedObject = createEmbedObject(presence, stream);
        getTwitchChannel().sendMessage(presence.getStreamingUrl().get(), embedObject);
    }

    private static void deleteStreamMessage(String streamUrl) {
        final MessageHistory messages = getTwitchChannel().getMessageHistory();

        for (IMessage message : messages) {
            if (message.getContent().contains(streamUrl)) {
                message.delete();
            }
        }
    }

    private static EmbedObject createEmbedObject(IPresence presence, TwitchApi.Stream stream) {
        final EmbedObject embedObject = new EmbedObject();
        embedObject.title = stream.getTitle();
        embedObject.url = presence.getStreamingUrl().get();
        embedObject.color = Integer.parseInt(EMBED_COLOR_HEX, 16);
        embedObject.author = new EmbedObject.AuthorObject(
            stream.getDisplayName(), null, stream.getLogo(), null
        );
        embedObject.thumbnail = new EmbedObject.ThumbnailObject(
            stream.getLogo(), null, 100, 100
        );
        embedObject.fields = new EmbedObject.EmbedFieldObject[]{
            new EmbedObject.EmbedFieldObject("Game", stream.getGame(), true),
            new EmbedObject.EmbedFieldObject("Viewers", "" + stream.getViewers(), true)
        };
        embedObject.image = new EmbedObject.ImageObject(
            stream.getPreview(), null, 360, 640
        );

        return embedObject;
    }
}
