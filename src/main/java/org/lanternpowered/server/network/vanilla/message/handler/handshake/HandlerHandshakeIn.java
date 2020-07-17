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
package org.lanternpowered.server.network.vanilla.message.handler.handshake;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NettyThreadOnly;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.ProxyType;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.handler.login.LoginStartHandler;
import org.lanternpowered.server.network.vanilla.message.type.handshake.HandshakeMessage;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public final class HandlerHandshakeIn implements Handler<HandshakeMessage> {

    private static final String FML_MARKER = "\0FML\0";
    private static final Gson GSON = new Gson();

    @NettyThreadOnly
    @Override
    public void handle(NetworkContext context, HandshakeMessage message) {
        final Optional<ProtocolState> optNextState = ProtocolState.getFromId(message.getNextState());
        final NetworkSession session = context.getSession();
        if (!optNextState.isPresent()) {
            session.close(t("Unknown protocol state! (%s)", message.getNextState()));
            return;
        }

        final ProtocolState nextState = optNextState.get();
        session.setProtocolState(nextState);
        if (!nextState.equals(ProtocolState.LOGIN) && !nextState.equals(ProtocolState.STATUS)) {
            session.close(t("Received a unexpected handshake message! (%s)", nextState));
            return;
        }

        final ProxyType proxyType = Lantern.getGame().getGlobalConfig().getProxyType();
        String hostname = message.getHostname();
        InetSocketAddress virtualAddress;

        switch (proxyType) {
            case WATERFALL:
            case BUNGEE_CORD:
            case VELOCITY:
                String[] split = hostname.split("\0\\|", 2);

                // Check for a fml marker
                session.getChannel().attr(NetworkSession.FML_MARKER).set(split.length == 2 == split[1].contains(FML_MARKER));

                split = split[0].split("\00");
                if (split.length == 3 || split.length == 4) {
                    virtualAddress = new InetSocketAddress(split[1], message.getPort());

                    final UUID uniqueId = UUIDHelper.fromFlatString(split[2]);
                    final Multimap<String, ProfileProperty> properties;
                    if (split.length == 4) {
                        try {
                            properties = LanternProfileProperty.createPropertiesMapFromJson(GSON.fromJson(split[3], JsonArray.class));
                        } catch (Exception e) {
                            session.close(t("Invalid %s proxy data format.", proxyType.getDisplayName()));
                            throw new CodecException(e);
                        }
                    } else {
                        properties = LinkedHashMultimap.create();
                    }

                    session.getChannel().attr(LoginStartHandler.SPOOFED_GAME_PROFILE).set(new LanternGameProfile(uniqueId, null, properties));
                } else {
                    session.close(t("Please enable client detail forwarding (also known as \"ip forwarding\") on "
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
                        session.close(t("Proxy security key mismatch"));
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

                    session.getChannel().attr(LoginStartHandler.SPOOFED_GAME_PROFILE).set(new LanternGameProfile(uniqueId, name, properties));
                    session.getChannel().attr(NetworkSession.FML_MARKER).set(false);

                    final int port = jsonObject.get("rP").getAsInt();
                    final String host = jsonObject.get("h").getAsString();

                    virtualAddress = new InetSocketAddress(host, port);
                } catch (Exception e) {
                    session.close(t("Invalid %s proxy data format.", proxyType.getDisplayName()));
                    throw new CodecException(e);
                }
                break;
            case NONE:
                int index = hostname.indexOf(FML_MARKER);
                session.getChannel().attr(NetworkSession.FML_MARKER).set(index != -1);
                if (index != -1) {
                    hostname = hostname.substring(0, index);
                }
                virtualAddress = new InetSocketAddress(hostname, message.getPort());
                break;
            default:
                throw new IllegalStateException("The proxy type " + proxyType + " isn't implemented");
        }

        session.setVirtualHost(virtualAddress);
        session.setProtocolVersion(message.getProtocolVersion());

        if (nextState == ProtocolState.LOGIN) {
            final int protocol = Lantern.getGame().getPlatform().getMinecraftVersion().getProtocol();

            if (message.getProtocolVersion() < protocol) {
                session.close(t("multiplayer.disconnect.outdated_client", Lantern.getGame().getPlatform().getMinecraftVersion().getName()));
            } else if (message.getProtocolVersion() > protocol) {
                session.close(t("multiplayer.disconnect.outdated_server", Lantern.getGame().getPlatform().getMinecraftVersion().getName()));
            }
        }
    }

}
