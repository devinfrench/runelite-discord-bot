package net.runelite.discord.presence.twitch;

import net.runelite.discord.Bot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IRole;

public class Twitch {

    private static final String EMBED_COLOR_HEX = "634299";

    @EventSubscriber
    public void onPresenceUpdateEvent(PresenceUpdateEvent event) {
        if (Bot.runelite == null) {
            return;
        }
        IPresence presence = event.getNewPresence();
        if (presence.getActivity().isPresent()
            && presence.getActivity().get() == ActivityType.STREAMING
            && presence.getStreamingUrl().isPresent()
            && !event.getOldPresence().getStreamingUrl().isPresent()) {
            IChannel channel = Bot.runelite.getChannelsByName("twitch").get(0);
            IRole role = Bot.runelite.getRolesByName("streamer").get(0);
            if (event.getUser().hasRole(role)) {
                String id = presence.getStreamingUrl().get().replace("https://www.twitch.tv/", "");
                TwitchApi.Stream stream = TwitchApi.getStream(id);
                if (stream == null || !stream.getGame().contains("RuneScape")) {
                    return;
                }
                EmbedObject embedObject = createEmbedObject(presence, stream);
                channel.sendMessage(presence.getStreamingUrl().get(), embedObject);
            }
        }
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

}
