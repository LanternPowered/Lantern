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
package org.lanternpowered.server.network.pipeline;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.SimpleRemoteConnection;
import org.lanternpowered.server.network.status.LanternStatusClient;
import org.lanternpowered.server.network.status.LanternStatusHelper;
import org.lanternpowered.server.network.status.LanternStatusResponse;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("deprecation")
public final class LegacyProtocolHandler extends ChannelInboundHandlerAdapter {

    private static final int V1_3_2_PROTOCOL = 39;
    private static final int V1_5_2_PROTOCOL = 61;

    private final NetworkSession session;

    public LegacyProtocolHandler(NetworkSession session) {
        this.session = session;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        final LanternServer server = this.session.getServer();

        final ByteBuf buf = (ByteBuf) object;
        buf.markReaderIndex();

        // Whether it was a valid legacy message
        boolean legacy = false;

        try {
            final int messageId = buf.readUnsignedByte();
            // Old client's are not so smart, make sure that
            // they don't attempt to login
            if (messageId == 0x02) {
                int protocol = buf.readByte(); // Protocol version
                int value = buf.readShort();
                // Check the length
                if (value < 0 || value > 16) {
                    return;
                }
                buf.readBytes(value << 1); // Username
                value = buf.readShort();
                // Check the length
                if (value < 0 || value > 255) {
                    return;
                }
                buf.readBytes(value << 1); // Host address
                buf.readInt(); // Port
                if (buf.readableBytes() > 0) {
                    return;
                }
                legacy = true;
                sendDisconnectMessage(ctx, LanternTexts.toPlain(t("multiplayer.disconnect.outdated_client",
                        Lantern.getGame().getPlatform().getMinecraftVersion().getName())));
                final MinecraftVersion clientVersion = Lantern.getGame().getMinecraftVersionCache().getVersionOrUnknown(protocol, true);
                if (clientVersion == LanternMinecraftVersion.UNKNOWN_LEGACY) {
                    Lantern.getLogger().debug("Client with unknown legacy protocol version {} attempted to join the server.", protocol);
                } else {
                    Lantern.getLogger().debug("Client with legacy protocol version {} (mc-version {}) attempted to join the server.", protocol,
                            clientVersion.getName());
                }
                return;
            }

            // Check for the ping message id.
            if (messageId != 0xfe) {
                return;
            }

            int readable = buf.readableBytes();
            boolean full = false;

            // The version used to ping the server
            int protocol = V1_3_2_PROTOCOL;

            // Versions 1.4 - 1.5.x + 1.6 - Can request full data.
            if (readable > 0) {
                // Is always 1
                if (buf.readUnsignedByte() != 1) {
                    return;
                }
                full = true;
                protocol = V1_5_2_PROTOCOL;
            }

            // The virtual address that was used to join the server
            InetSocketAddress virtualAddress = null;

            // Version 1.6 - Used extra data.
            if (readable > 1) {
                if (buf.readUnsignedByte() != 0xfa) {
                    return;
                }
                byte[] bytes = new byte[buf.readShort() << 1];
                buf.readBytes(bytes);
                if (!new String(bytes, StandardCharsets.UTF_16BE).equals("MC|PingHost")){
                    return;
                }

                // Not used
                buf.readShort();

                // The protocol version is present
                protocol = buf.readUnsignedByte();

                // There is extra host and port data
                if (protocol >= 73) {
                    bytes = new byte[buf.readShort() << 1];
                    buf.readBytes(bytes);

                    final String host = new String(bytes, StandardCharsets.UTF_16BE);
                    final int port = buf.readInt();

                    virtualAddress = InetSocketAddress.createUnresolved(host, port);
                }

                readable = buf.readableBytes();
                if (readable > 0) {
                    Lantern.getLogger().warn("Trailing bytes on a legacy ping message: {}b", readable);
                }
            }

            // The message was successfully decoded as a legacy one
            legacy = true;

            final boolean full1 = full;
            final int protocol1 = protocol;
            final InetSocketAddress virtualAddress1 = virtualAddress;

            // Call the event in the main thread
            Lantern.getScheduler().callSync(() -> {
                final MinecraftVersion clientVersion = Lantern.getGame().getMinecraftVersionCache().getVersionOrUnknown(protocol1, true);
                if (clientVersion == LanternMinecraftVersion.UNKNOWN) {
                    Lantern.getLogger().debug("Client with unknown legacy protocol version {} pinged the server.", protocol1);
                }

                final MinecraftVersion serverVersion = Lantern.getGame().getPlatform().getMinecraftVersion();
                Text description = server.getMotd();

                final InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                final LanternStatusClient client = new LanternStatusClient(address, clientVersion, virtualAddress1);
                final ClientPingServerEvent.Response.Players players = LanternStatusHelper.createPlayers(server);
                final LanternStatusResponse response = new LanternStatusResponse(serverVersion, server.getFavicon(), description, players);

                final SimpleRemoteConnection connection = new SimpleRemoteConnection(address, virtualAddress1);
                final Cause cause = Cause.of(EventContext.empty(), connection);
                final ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(cause, client, response);
                Sponge.getEventManager().post(event);

                // Cancelled, we are done here
                if (event.isCancelled()) {
                    ctx.channel().close();
                    return;
                }

                description = response.getDescription();
                int online = players.getOnline();
                final int max = players.getMax();

                // The players should be hidden, this will replace the player count
                // with ???
                if (!response.getPlayers().isPresent()) {
                    online = -1;
                }

                final String data;

                if (full1) {
                    final String description0 = getFirstLine(TextSerializers.LEGACY_FORMATTING_CODE.serialize(description));
                    // 1. This value is always 1.
                    // 2. The protocol version, just use a value out of range
                    //    of the available ones.
                    // 3. The version/name string of the server.
                    // 4. The motd of the server. In legacy format.
                    // 5. The online players
                    // 6. The maximum amount of players
                    data = String.format("\u00A7%s\u0000%s\u0000%s\u0000%s\u0000%s\u0000%s",
                            1, 127, response.getVersion().getName(), description0, online, max);
                } else {
                    final String description0 = getFirstLine(TextSerializers.PLAIN.serialize(description));
                    // 1. The motd of the server. In legacy format.
                    // 2. The online players
                    // 3. The maximum amount of players
                    data = String.format("%s\u00A7%s\u00A7%s",
                            description0, online, max);
                }

                sendDisconnectMessage(ctx, data);
            });
        } catch (Exception ignore) {
        } finally {
            if (legacy) {
                buf.release();
            } else {
                buf.resetReaderIndex();
                ctx.channel().pipeline().remove(this);
                ctx.fireChannelRead(buf);
            }
        }
    }

    /**
     * Sends a disconnect message to a legacy client and closes the connection.
     *
     * @param ctx The channel handler context
     * @param message The message
     */
    private static void sendDisconnectMessage(ChannelHandlerContext ctx, String message) {
        final byte[] data = message.getBytes(StandardCharsets.UTF_16BE);

        final ByteBuf output = ctx.alloc().buffer();
        output.writeByte(0xff);
        output.writeShort(data.length >> 1);
        output.writeBytes(data);

        ctx.channel().pipeline().firstContext().writeAndFlush(output).addListener(ChannelFutureListener.CLOSE);
    }

    private static String getFirstLine(String value) {
        final int i = value.indexOf('\n');
        return i == -1 ? value : value.substring(0, i);
    }

}
