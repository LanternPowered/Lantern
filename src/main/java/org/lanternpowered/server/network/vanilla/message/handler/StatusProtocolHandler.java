/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler;

import static com.google.common.base.Preconditions.checkState;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.WrappedRemoteConnection;
import org.lanternpowered.server.network.message.handler.Async;
import org.lanternpowered.server.network.message.handler.ContextInject;
import org.lanternpowered.server.network.message.handler.NetworkMessageHandler;
import org.lanternpowered.server.network.status.LanternFavicon;
import org.lanternpowered.server.network.status.LanternStatusClient;
import org.lanternpowered.server.network.status.LanternStatusHelper;
import org.lanternpowered.server.network.status.LanternStatusResponse;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;
import org.lanternpowered.server.text.gson.LanternJsonTextSerializer;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

public final class StatusProtocolHandler {

    @ContextInject private NetworkSession session;
    @ContextInject private Channel channel;

    @Async
    @NetworkMessageHandler
    private void handleRequest(MessageStatusInRequest message) {
        final LanternServer server = this.session.getServer();
        final Gson gson = new Gson();

        final Text description = server.getMotd();

        final InetSocketAddress address = this.session.getAddress();
        final InetSocketAddress virtualAddress = this.session.getVirtualHost();

        final int protocol = this.session.getProtocolVersion();
        final MinecraftVersion clientVersion = Lantern.getGame().getMinecraftVersionCache().getVersionOrUnknown(protocol, false);
        if (clientVersion == LanternMinecraftVersion.UNKNOWN) {
            Lantern.getLogger().debug("Client with unknown protocol version {} pinged the server.", protocol);
        }

        final LanternStatusClient client = new LanternStatusClient(address, clientVersion, virtualAddress);
        final ClientPingServerEvent.Response.Players players = LanternStatusHelper.createPlayers(server);
        final LanternStatusResponse response = new LanternStatusResponse(Lantern.getGame().getPlatform().getMinecraftVersion(),
                server.getFavicon(), description, players);

        final Cause cause = Cause.of(EventContext.empty(), new WrappedRemoteConnection(this.session));
        final ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(cause, client, response);
        Sponge.getEventManager().post(event);

        // Cancelled, we are done here
        if (event.isCancelled()) {
            this.channel.close();
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
        rootObject.add("description", ((LanternJsonTextSerializer) TextSerializers.JSON).getGson().toJsonTree(response.getDescription()));

        response.getFavicon().ifPresent(icon -> rootObject.addProperty("favicon", ((LanternFavicon) icon).getEncoded()));

        final JsonObject fmlObject = new JsonObject();
        // Trick the client that the server is fml, we support fml channels anyway
        fmlObject.addProperty("type", "FML");
        // The client shouldn't know the plugins (mods) list
        fmlObject.add("modList", new JsonArray());

        // Add the fml info
        rootObject.add("modinfo", fmlObject);

        this.session.send(new MessageStatusOutResponse(gson.toJson(rootObject)));
    }

    @Async
    @NetworkMessageHandler
    private void handlePing(MessageStatusInOutPing message) {
        this.session.send(message);
    }
}
