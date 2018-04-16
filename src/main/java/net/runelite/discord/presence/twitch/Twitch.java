package net.runelite.discord.presence.twitch;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.*;

public class Twitch {

    private static final String EMBED_COLOR_HEX = "634299";

    private IGuild guild;
    private IChannel channel;
    private IRole role;

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        for (IGuild guild : event.getClient().getGuilds()) {
            if (guild.getName().equals("RuneLite")) {
                this.guild = guild;
            }
        }
        channel = guild.getChannelsByName("twitch").get(0);
        role = guild.getRolesByName("streamer").get(0);
    }

    @EventSubscriber
    public void onPresenceUpdateEvent(PresenceUpdateEvent event) {
        if (role == null || channel == null) {
            return;
        }
        IPresence presence = event.getNewPresence();
        if (event.getUser().hasRole(role)
            && presence.getActivity().isPresent()
            && presence.getActivity().get() == ActivityType.STREAMING
            && presence.getStreamingUrl().isPresent()
            && !event.getOldPresence().getStreamingUrl().isPresent()) {
            String id = presence.getStreamingUrl().get().replace("https://www.twitch.tv/", "");
            TwitchApi.Stream stream = TwitchApi.getStream(id);
            if (stream == null || !stream.getGame().contains("RuneScape")) {
                return;
            }
            EmbedObject embedObject = createEmbedObject(presence, stream);
            channel.sendMessage(presence.getStreamingUrl().get(), embedObject);
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
