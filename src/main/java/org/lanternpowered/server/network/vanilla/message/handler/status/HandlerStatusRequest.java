/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.vanilla.message.handler.status;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.status.LanternStatusClient;
import org.lanternpowered.server.status.LanternStatusResponse;
import org.lanternpowered.server.status.LanternStatusResponsePlayers;
import org.lanternpowered.server.text.gson.LanternJsonTextSerializer;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HandlerStatusRequest implements Handler<MessageStatusInRequest> {

    @Override
    public void handle(NetworkContext context, MessageStatusInRequest message) {
        Session session = context.getSession();
        LanternServer server = session.getServer();
        Gson gson = new Gson();

        MinecraftVersion version0 = LanternGame.get().getPlatform().getMinecraftVersion();
        Text motd = server.getMotd();

        int online = server.getOnlinePlayers().size();
        int max = server.getMaxPlayers();

        InetSocketAddress address = session.getAddress();
        InetSocketAddress virtualAddress = session.getVirtualHost();

        int protocol = session.getProtocolVersion();

        LanternMinecraftVersion version = new LanternMinecraftVersion(String.valueOf(protocol), protocol, false);
        LanternStatusClient client = new LanternStatusClient(address, version, virtualAddress);
        LanternStatusResponsePlayers players = new LanternStatusResponsePlayers(server.getOnlinePlayers()
        		.stream().map(p -> p.getProfile()).collect(Collectors.toList()), online, max);
        LanternStatusResponse response = new LanternStatusResponse(version0, server.getFavicon().orElse(null), motd, players);

        ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(Cause.of(client), client, response);

        // Cancelled, we are done here
        if (event.isCancelled()) {
            return;
        }

        motd = response.getDescription();
        online = players.getOnline();
        max = players.getMax();

        JsonObject rootObject = new JsonObject();
        JsonObject versionObject = new JsonObject();

        versionObject.addProperty("name", LanternGame.get().getPlatform().getImplementation().getName());
        versionObject.addProperty("protocol", ((LanternMinecraftVersion) version0).getProtocol());

        if (response.getPlayers().isPresent()) {
            JsonObject playersObject = new JsonObject();
            playersObject.addProperty("max", max);
            playersObject.addProperty("online", online);

            List<GameProfile> profiles = players.getProfiles();
            if (!profiles.isEmpty()) {
                JsonArray array = new JsonArray();
                for (GameProfile profile : profiles) {
                    Optional<String> optName = profile.getName();
                    if (!optName.isPresent()) {
                        continue;
                    }
                    JsonObject object3 = new JsonObject();
                    object3.addProperty("name", optName.get());
                    object3.addProperty("uuid", profile.getUniqueId().toString());
                    array.add(object3);
                }
                playersObject.add("sample", array);
            }
            rootObject.add("players", playersObject);
        }

        rootObject.add("version", versionObject);
        rootObject.add("description", ((LanternJsonTextSerializer) TextSerializers.JSON).getGson().toJsonTree(motd));

        Optional<Favicon> icon = response.getFavicon();
        if (icon.isPresent()) {
            rootObject.addProperty("favicon", ((LanternFavicon) icon.get()).getEncoded());
        }

        JsonObject fmlObject = new JsonObject();
        // Trick the client that the server is fml, we support fml channels anyway
        fmlObject.addProperty("type", "FML");
        // The client shouldn't know the plugins (mods) list
        fmlObject.add("modList", new JsonArray());

        // Add the fml info
        rootObject.add("modinfo", fmlObject);

        session.send(new MessageStatusOutResponse(gson.toJson(rootObject)));
    }
}
