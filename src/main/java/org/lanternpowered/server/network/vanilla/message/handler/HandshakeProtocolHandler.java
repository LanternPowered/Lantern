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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.ProxyType;
import org.lanternpowered.server.network.message.handler.Async;
import org.lanternpowered.server.network.message.handler.ContextInject;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public final class HandshakeProtocolHandler {

    private static final String FML_MARKER = "\0FML\0";
    private static final Gson GSON = new Gson();

    @ContextInject private NetworkSession session;

    @Async
    @Handler
    private void handle(MessageHandshakeIn message) {
        final Optional<ProtocolState> optNextState = ProtocolState.getFromId(message.getNextState());
        if (!optNextState.isPresent()) {
            this.session.disconnect(t("Unknown protocol state! (%s)", message.getNextState()));
            return;
        }

        final ProtocolState nextState = optNextState.get();
        this.session.setProtocolState(nextState);
        if (!nextState.equals(ProtocolState.LOGIN) && !nextState.equals(ProtocolState.STATUS)) {
            this.session.disconnect(t("Received a unexpected handshake message! (%s)", nextState));
            return;
        }

        final ProxyType proxyType = Lantern.getGame().getGlobalConfig().getProxyType();
        String hostname = message.getHostname();
        InetSocketAddress virtualAddress;

        switch (proxyType) {
            case WATERFALL:
            case BUNGEE_CORD:
                String[] split = hostname.split("\0\\|", 2);

                // Check for a fml marker
                this.session.getChannel().attr(NetworkSession.FML_MARKER).set(split.length == 2 == split[1].contains(FML_MARKER));

                split = split[0].split("\00");
                if (split.length == 3 || split.length == 4) {
                    virtualAddress = new InetSocketAddress(split[1], message.getPort());

                    final UUID uniqueId = UUIDHelper.fromFlatString(split[2]);
                    final Multimap<String, ProfileProperty> properties;
                    if (split.length == 4) {
                        try {
                            properties = LanternProfileProperty.createPropertiesMapFromJson(GSON.fromJson(split[3], JsonArray.class));
                        } catch (Exception e) {
                            this.session.disconnect(t("Invalid %s proxy data format.", proxyType.getName()));
                            throw new CodecException(e);
                        }
                    } else {
                        properties = LinkedHashMultimap.create();
                    }

                    this.session.getChannel().attr(LoginProtocolHandler.SPOOFED_GAME_PROFILE).set(new LanternGameProfile(uniqueId, null, properties));
                } else {
                    this.session.disconnect(t("Please enable client detail forwarding (also known as \"ip forwarding\") on "
                            + "your proxy if you wish to use it on this server, and also make sure that you joined through the proxy."));
                    return;
                }
                break;
            case LILY_PAD:
                try {
                    final JsonObject jsonObject = GSON.fromJson(hostname, JsonObject.class);

                    final String securityKey = Lantern.getGame().getGlobalConfig().getProxySecurityKey();
                    // Validate the security key
                    if (!securityKey.isEmpty() && !jsonObject.get("s").getAsString().equals(securityKey)) {
                        this.session.disconnect(t("Proxy security key mismatch"));
                        Lantern.getLogger().warn("Proxy security key mismatch for the player {}", jsonObject.get("n").getAsString());
                        return;
                    }

                    final String name = jsonObject.get("n").getAsString();
                    final UUID uniqueId = UUIDHelper.fromFlatString(jsonObject.get("u").getAsString());

                    final Multimap<String, ProfileProperty> properties = LinkedHashMultimap.create();
                    if (jsonObject.has("p")) {
                        final JsonArray jsonArray = jsonObject.getAsJsonArray("p");

                        for (int i = 0; i < jsonArray.size(); i++) {
                            final JsonObject property = jsonArray.get(i).getAsJsonObject();

                            final String propertyName = property.get("n").getAsString();
                            final String propertyValue = property.get("v").getAsString();
                            final String propertySignature = property.has("s") ? property.get("s").getAsString() : null;

                            properties.put(propertyName, new LanternProfileProperty(propertyName, propertyValue, propertySignature));
                        }
                    }

                    this.session.getChannel().attr(LoginProtocolHandler.SPOOFED_GAME_PROFILE).set(new LanternGameProfile(uniqueId, name, properties));
                    this.session.getChannel().attr(NetworkSession.FML_MARKER).set(false);

                    final int port = jsonObject.get("rP").getAsInt();
                    final String host = jsonObject.get("h").getAsString();

                    virtualAddress = new InetSocketAddress(host, port);
                } catch (Exception e) {
                    this.session.disconnect(t("Invalid %s proxy data format.", proxyType.getName()));
                    throw new CodecException(e);
                }
                break;
            case NONE:
                final int index = hostname.indexOf(FML_MARKER);
                this.session.getChannel().attr(NetworkSession.FML_MARKER).set(index != -1);
                if (index != -1) {
                    hostname = hostname.substring(0, index);
                }
                virtualAddress = new InetSocketAddress(hostname, message.getPort());
                break;
            default:
                throw new IllegalStateException("The proxy type " + proxyType + " isn't implemented");
        }

        this.session.setVirtualHost(virtualAddress);
        this.session.setProtocolVersion(message.getProtocolVersion());

        if (nextState == ProtocolState.LOGIN) {
            final LanternMinecraftVersion version = Lantern.getGame().getPlatform().getMinecraftVersion();

            final int protocol = version.getProtocol();
            if (message.getProtocolVersion() < protocol) {
                this.session.disconnect(t("multiplayer.disconnect.outdated_client", version.getName()));
            } else if (message.getProtocolVersion() > protocol) {
                this.session.disconnect(t("multiplayer.disconnect.outdated_server", version.getName()));
            }
        }
    }
}
