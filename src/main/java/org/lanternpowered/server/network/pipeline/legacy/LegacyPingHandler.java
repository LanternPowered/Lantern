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
package org.lanternpowered.server.network.pipeline.legacy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.status.LanternStatusClient;
import org.lanternpowered.server.status.LanternStatusResponse;
import org.lanternpowered.server.status.LanternStatusResponsePlayers;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class LegacyPingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        LanternServer server = ctx.channel().attr(Session.SESSION).get().getServer();

        ByteBuf buf = (ByteBuf) object;
        buf.markReaderIndex();

        // Whether it was a valid legacy message
        boolean legacy = false;

        try {
            int messageId = buf.readUnsignedByte();
            // Old client's are not so smart, make sure that
            // they don't attempt to login
            if (messageId == 0x02) {
                buf.readByte(); // Protocol version
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
                ctx.channel().close();
                return;
            }

            // Check for the ping message id.
            if (messageId != 0xfe) {
                return;
            }

            int readable = buf.readableBytes();
            boolean full = false;

            // The version used to ping the server
            MinecraftVersion version = LanternMinecraftVersion.V1_3;

            // Versions 1.4 - 1.5.x + 1.6 - Can request full data.
            if (readable > 0) {
                // Is always 1
                if (buf.readUnsignedByte() != 1) {
                    return;
                }
                full = true;
                version = LanternMinecraftVersion.V1_5;
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

                // There is extra host and port data
                if (buf.readUnsignedByte() >= 73) {
                    bytes = new byte[buf.readShort() << 1];
                    buf.readBytes(bytes);

                    String host = new String(bytes, StandardCharsets.UTF_16BE);
                    int port = buf.readInt();

                    virtualAddress = InetSocketAddress.createUnresolved(host, port);
                }

                readable = buf.readableBytes();
                if (readable > 0) {
                    Lantern.getLogger().warn("Trailing bytes on a legacy ping message: {}b", readable);
                }

                version = LanternMinecraftVersion.V1_6;
            }

            // The message was successfully decoded as a legacy one
            legacy = true;

            MinecraftVersion version0 = Lantern.getGame().getPlatform().getMinecraftVersion();
            Text motd = server.getMotd();

            int online = server.getOnlinePlayers().size();
            int max = server.getMaxPlayers();

            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            LanternStatusClient client = new LanternStatusClient(address, version, virtualAddress);
            LanternStatusResponsePlayers players = new LanternStatusResponsePlayers(server.getOnlinePlayers()
                    .stream().map(User::getProfile).collect(Collectors.toList()), online, max);
            LanternStatusResponse response = new LanternStatusResponse(version0, server.getFavicon().orElse(null), motd, players);

            ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(Cause.source(client).build(), client, response);
            Sponge.getEventManager().post(event);

            // Cancelled, we are done here
            if (event.isCancelled()) {
                ctx.channel().close();
                return;
            }

            motd = response.getDescription();
            online = players.getOnline();
            max = players.getMax();

            // The players should be hidden, this will replace the player count
            // with ???
            if (!response.getPlayers().isPresent()) {
                online = -1;
            }

            StringBuilder builder = new StringBuilder();

            if (full) {
                String motd0 = TextSerializers.LEGACY_FORMATTING_CODE.serialize(motd);
                builder
                        .append('\u00A7')
                        // This value is always 1.
                        .append(1)
                        .append('\u0000')
                        // The protocol version, just use a value out of range
                        // of the available ones.
                        .append(127)
                        .append('\u0000')
                        // The version/name string of the server.
                        .append(Lantern.getGame().getPlatform().getImplementation().getName())
                        .append('\u0000')
                        // The motd of the server. In legacy format.
                        .append(motd0)
                        .append('\u0000')
                        .append(online)
                        .append('\u0000')
                        .append(max);
            } else {
                String motd0 = TextSerializers.PLAIN.serialize(motd);
                builder
                        .append(motd0)
                        .append('\u00A7')
                        .append(online)
                        .append('\u00A7')
                        .append(max);
            }

            byte[] data = builder.toString().getBytes(StandardCharsets.UTF_16BE);

            ByteBuf output = ctx.alloc().buffer();
            output.writeByte(0xff);
            output.writeShort(data.length >> 1);
            output.writeBytes(data);

            ctx.channel().pipeline().firstContext().writeAndFlush(output).addListener(ChannelFutureListener.CLOSE);
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

}
