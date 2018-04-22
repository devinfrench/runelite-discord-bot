package net.runelite.discord.presence.twitch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.runelite.discord.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

public class Twitch implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Twitch.class);
    private static final String EMBED_COLOR_HEX = "634299";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void run() {
        try {
            final MessageHistory messages = getTwitchChannel().getFullMessageHistory();
            final List<String> existing = new ArrayList<>();
            LOG.debug("Running twitch task");

            // Delete all non-streaming users
            for (IMessage message : messages) {
                if (message.getContent().contains("twitch.tv")) {
                    LOG.debug("Found message {}", message.getContent());
                    final String url = message.getContent().replaceAll("<[^>]+> ", "");
                    LOG.debug("Found stream url {}", url);

                    // Add link to the list as this message is already present in channel
                    existing.add(url);

                    // Find existing stream from stream URL
                    final TwitchApi.Stream stream = findExistingStream(url);

                    // If stream ended, delete message
                    if (stream == null) {
                        LOG.debug("Deleting stream message for {}", url);
                        message.delete();
                        delay();
                    }
                }
            }

            // Find all users that are probably streaming and properly update discord
            for (IUser user : Bot.runelite.getUsersByRole(getStreamerRole())) {

                // If stream url is not already in channel, check stream
                if (user.getPresence().getStreamingUrl().isPresent()
                    && !existing.contains(user.getPresence().getStreamingUrl().get())) {
                    LOG.debug("Found streaming user {}", user);

                    // Find stream for all streamers
                    final TwitchApi.Stream stream = findStream(user.getPresence());

                    if (stream != null) {
                        LOG.debug("Sending stream message for {}", user.getPresence().getStreamingUrl().get());
                        sendStreamMessage(user, stream);
                        delay();
                    }
                }
            }
        } catch (Exception e) {
            // Ignore all exceptions so next loop will start
            e.printStackTrace();
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        scheduler.scheduleWithFixedDelay(this, 1, 3, TimeUnit.MINUTES);
    }

    private static void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) { }
    }

    private static TwitchApi.Stream findStream(final IPresence presence) {
        final boolean isStreamingSomething = presence.getActivity().isPresent()
            && presence.getActivity().get() == ActivityType.STREAMING
            && presence.getStreamingUrl().isPresent();

        return isStreamingSomething ? findExistingStream(presence.getStreamingUrl().get()) : null;
    }

    private static TwitchApi.Stream findExistingStream(final String streamUrl) {
        final String id = streamUrl.replace("https://www.twitch.tv/", "");
        final TwitchApi.Stream stream = TwitchApi.getStream(id);

        if (stream == null) {
            return null;
        }

        if (!stream.getGame().contains("RuneScape")) {
            return null;
        }

        if (!stream.getType().equals("live")) {
            return null;
        }

        return stream;
    }

    private static IRole getStreamerRole() {
        return Bot.roles.get("streamer");
    }

    private static IChannel getTwitchChannel() {
        return Bot.channels.get("twitch");
    }

    private static void sendStreamMessage(final IUser user, final TwitchApi.Stream stream) {
        final EmbedObject embedObject = createEmbedObject(user.getPresence(), stream);
        getTwitchChannel().sendMessage(user.mention() +  " " + user.getPresence().getStreamingUrl().get(), embedObject);
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
