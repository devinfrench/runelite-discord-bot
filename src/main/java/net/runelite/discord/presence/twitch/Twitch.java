package net.runelite.discord.presence.twitch;

import net.runelite.discord.Bot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.util.MessageHistory;

import java.util.Timer;
import java.util.TimerTask;

public class Twitch {

    private static final String EMBED_COLOR_HEX = "634299";

    @EventSubscriber
    public void onPresenceUpdateEvent(PresenceUpdateEvent event) {
        if (Bot.runelite == null || !event.getUser().hasRole(Bot.roles.get("streamer"))) {
            return;
        }
        IPresence newPresence = event.getNewPresence();
        IPresence oldPresence = event.getOldPresence();
        if (newPresence.getActivity().isPresent()
            && newPresence.getActivity().get() == ActivityType.STREAMING
            && newPresence.getStreamingUrl().isPresent()
            && !event.getOldPresence().getStreamingUrl().isPresent()) {
            sendStreamMessage(newPresence);
        }
        if (newPresence.getActivity().isPresent()
            && newPresence.getActivity().get() != ActivityType.STREAMING
            && oldPresence.getStreamingUrl().isPresent()) {
            deleteStreamMessage(oldPresence.getStreamingUrl().get());
        }
    }

    private void sendStreamMessage(IPresence presence) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String id = presence.getStreamingUrl().get().replace("https://www.twitch.tv/", "");
                TwitchApi.Stream stream = TwitchApi.getStream(id);
                if (stream == null || !stream.getGame().contains("RuneScape")) {
                    return;
                }
                EmbedObject embedObject = createEmbedObject(presence, stream);
                Bot.channels.get("twitch").sendMessage(presence.getStreamingUrl().get(), embedObject);
                cancel();
            }
        }, 60000);
    }

    private EmbedObject createEmbedObject(IPresence presence, TwitchApi.Stream stream) {
        EmbedObject embedObject = new EmbedObject();
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

    private void deleteStreamMessage(String streamUrl) {
        IChannel channel = Bot.channels.get("twitch");
        MessageHistory messages = channel.getMessageHistory();
        for (IMessage message : messages) {
            if (message.getContent().contains(streamUrl)) {
                message.delete();
            }
        }
    }

}
