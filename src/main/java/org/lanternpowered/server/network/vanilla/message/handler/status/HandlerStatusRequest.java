package org.lanternpowered.server.network.vanilla.message.handler.status;

import java.net.InetSocketAddress;
import java.util.List;

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.status.LanternStatusClient;
import org.lanternpowered.server.status.LanternStatusResponse;
import org.lanternpowered.server.status.LanternStatusResponsePlayers;
import org.lanternpowered.server.text.gson.JsonTextRepresentation;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class HandlerStatusRequest implements Handler<MessageStatusInRequest> {

    @Override
    public void handle(Session session, MessageStatusInRequest message) {
        LanternServer server = session.getServer();
        Gson gson = new Gson();

        MinecraftVersion version0 = LanternGame.get().getPlatform().getMinecraftVersion();
        Text motd = server.getMotd();

        int online = server.getOnlinePlayers().size();
        int max = server.getMaxPlayers();

        InetSocketAddress address = (InetSocketAddress) session.getAddress();
        InetSocketAddress virtualAddress = (InetSocketAddress) session.getVirtualHost();

        int protocol = session.getProtocolVersion();

        LanternMinecraftVersion version = new LanternMinecraftVersion(String.valueOf(protocol), protocol, false);
        LanternStatusClient client = new LanternStatusClient(address, version, virtualAddress);
        // TODO: Replace the list with the actual profiles
        LanternStatusResponsePlayers players = new LanternStatusResponsePlayers(Lists.<GameProfile>newArrayList(), online, max);
        LanternStatusResponse response = new LanternStatusResponse(version0, server.getFavicon().orNull(), motd, players);

        ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(client, response);

        // Cancelled, we are done here
        if (event.isCancelled()) {
            return;
        }

        motd = response.getDescription();
        online = players.getOnline();
        max = players.getMax();

        // The players should be hidden, this will replace the player count with
        // ???
        if (!response.getPlayers().isPresent()) {
            online = -1;
        }

        JsonObject object0 = new JsonObject();
        JsonObject object1 = new JsonObject();
        JsonObject object2 = new JsonObject();

        object1.addProperty("name", LanternGame.get().getPlatform().getName());
        object1.addProperty("protocol", ((LanternMinecraftVersion) version0).getProtocol());

        object2.addProperty("max", max);
        object2.addProperty("online", online);

        if (online != -1) {
            List<GameProfile> profiles = players.getProfiles();

            if (!profiles.isEmpty()) {
                JsonArray array = new JsonArray();
                for (GameProfile profile : profiles) {
                    JsonObject object3 = new JsonObject();
                    object3.addProperty("name", profile.getName());
                    object3.addProperty("uuid", profile.getUniqueId().toString());
                    array.add(object3);
                }
                object2.add("sample", array);
            }
        }

        object0.add("version", object1);
        object0.add("players", object2);
        object0.add("description", ((JsonTextRepresentation) Texts.json()).getGson().toJsonTree(motd));

        Optional<Favicon> icon = response.getFavicon();
        if (icon.isPresent()) {
            object0.addProperty("favicon", ((LanternFavicon) icon.get()).getEncoded());
        }

        session.send(new MessageStatusOutResponse(gson.toJson(object0)));
        // TODO: Is this good?
        session.send(new MessageStatusInOutPing(System.currentTimeMillis()));
    }
}
