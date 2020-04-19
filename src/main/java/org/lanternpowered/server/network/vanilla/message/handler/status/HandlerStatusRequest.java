/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.message.handler.status;

import static com.google.common.base.Preconditions.checkState;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.lanternpowered.api.text.serializer.JsonTextSerializer;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.WrappedRemoteConnection;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.status.LanternFavicon;
import org.lanternpowered.server.network.status.LanternStatusClient;
import org.lanternpowered.server.network.status.LanternStatusHelper;
import org.lanternpowered.server.network.status.LanternStatusResponse;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

public final class HandlerStatusRequest implements Handler<MessageStatusInRequest> {

    @Override
    public void handle(NetworkContext context, MessageStatusInRequest message) {
        final NetworkSession session = context.getSession();
        final LanternServer server = session.getServer();
        final Gson gson = new Gson();

        final Text description = server.getMotd();

        final InetSocketAddress address = session.getAddress();
        final InetSocketAddress virtualAddress = session.getVirtualHost();

        final int protocol = session.getProtocolVersion();
        final MinecraftVersion clientVersion = Lantern.getGame().getMinecraftVersionCache().getVersionOrUnknown(protocol, false);
        if (clientVersion == LanternMinecraftVersion.UNKNOWN) {
            Lantern.getLogger().debug("Client with unknown protocol version {} pinged the server.", protocol);
        }

        final LanternStatusClient client = new LanternStatusClient(address, clientVersion, virtualAddress);
        final ClientPingServerEvent.Response.Players players = LanternStatusHelper.createPlayers(server);
        final LanternStatusResponse response = new LanternStatusResponse(Lantern.getGame().getPlatform().getMinecraftVersion(),
                description, players, server.getFavicon().orElse(null));

        final Cause cause = Cause.of(EventContext.empty(), new WrappedRemoteConnection(session));
        final ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(cause, client, response);
        Sponge.getEventManager().post(event);

        // Cancelled, we are done here
        if (event.isCancelled()) {
            context.getChannel().close();
            return;
        }

        final JsonObject rootObject = new JsonObject();
        final JsonObject versionObject = new JsonObject();

        checkState(response.getVersion() instanceof LanternMinecraftVersion);
        final LanternMinecraftVersion serverVersion = (LanternMinecraftVersion) response.getVersion();
        versionObject.addProperty("name", serverVersion.getName());
        versionObject.addProperty("protocol", serverVersion.getProtocol());

        if (response.getPlayers().isPresent()) {
            final JsonObject playersObject = new JsonObject();
            playersObject.addProperty("max", players.getMax());
            playersObject.addProperty("online", players.getOnline());

            List<GameProfile> profiles = players.getProfiles();
            if (!profiles.isEmpty()) {
                final JsonArray array = new JsonArray();
                for (GameProfile profile : profiles) {
                    Optional<String> optName = profile.getName();
                    if (!optName.isPresent()) {
                        continue;
                    }
                    final JsonObject profileObject = new JsonObject();
                    profileObject.addProperty("name", optName.get());
                    profileObject.addProperty("id", profile.getUniqueId().toString());
                    array.add(profileObject);
                }
                playersObject.add("sample", array);
            }
            rootObject.add("players", playersObject);
        }

        rootObject.add("version", versionObject);
        rootObject.add("description", JsonTextSerializer.Companion.serializeToTree(response.getDescription()));

        response.getFavicon().ifPresent(icon -> rootObject.addProperty("favicon", ((LanternFavicon) icon).getEncoded()));

        final JsonObject fmlObject = new JsonObject();
        // Trick the client that the server is fml, we support fml channels anyway
        fmlObject.addProperty("type", "FML");
        // The client shouldn't know the plugins (mods) list
        fmlObject.add("modList", new JsonArray());

        // Add the fml info
        rootObject.add("modinfo", fmlObject);

        session.send(new MessageStatusOutResponse(gson.toJson(rootObject)));
    }
}
