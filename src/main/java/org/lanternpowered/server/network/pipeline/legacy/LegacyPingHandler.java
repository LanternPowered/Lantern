/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import java.net.InetSocketAddress;

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.status.LanternStatusClient;
import org.lanternpowered.server.status.LanternStatusResponse;
import org.lanternpowered.server.status.LanternStatusResponsePlayers;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@SuppressWarnings("deprecation")
public final class LegacyPingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        LanternServer server = ctx.channel().attr(Session.SESSION).get().getServer();

        ByteBuf buf0 = (ByteBuf) object;
        buf0.markReaderIndex();

        // Whether it was a valid legacy message
        boolean legacy = false;

        try {
            // Check for the right message id.
            if (buf0.readUnsignedByte() != 0xfe) {
                return;
            }

            int readable = buf0.readableBytes();

            boolean valid = true;
            boolean full = false;

            // The version used to ping the server
            MinecraftVersion version = LanternMinecraftVersion.V1_3;

            // Versions 1.4 - 1.5.x + 1.6 - Can request full data.
            if (readable > 0) {
                full = buf0.readUnsignedByte() == 1;
                version = LanternMinecraftVersion.V1_5;
            }

            // The virtual address that was used to join the server
            InetSocketAddress virtualAddress = null;

            // Version 1.6 - Used extra data.
            if (readable > 1) {
                valid &= buf0.readUnsignedByte() == 0xfa;
                valid &= new String(buf0.readBytes(buf0.readShort() * 2).array(), Charsets.UTF_16BE).equals("MC|PingHost");

                int restLength = buf0.readShort();

                // Validate the protocol version
                valid &= buf0.readUnsignedByte() >= 73;

                int hostLength = buf0.readShort();
                byte[] hostBytes = new byte[hostLength];
                buf0.readBytes(hostBytes);

                String host = new String(hostBytes, Charsets.UTF_16BE);

                // Two times the amount of bytes of the host length
                // no idea why this would be useful
                buf0.readBytes(hostBytes);

                valid &= (hostLength * 2 + 7) == restLength;
                int port = buf0.readInt();
                valid &= port < 65535;
                valid &= buf0.readableBytes() == 0;

                virtualAddress = InetSocketAddress.createUnresolved(host, port);
                version = LanternMinecraftVersion.V1_6;
            }

            if (!valid) {
                return;
            }

            // The message was successfully decoded as a legacy one
            legacy = true;

            MinecraftVersion version0 = LanternGame.get().getPlatform().getMinecraftVersion();
            Text motd = server.getMotd();

            int online = server.getOnlinePlayers().size();
            int max = server.getMaxPlayers();

            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            LanternStatusClient client = new LanternStatusClient(address, version, virtualAddress);
            // TODO: Replace the list with the actual profiles, but not used anyway
            LanternStatusResponsePlayers players = new LanternStatusResponsePlayers(Lists.<GameProfile>newArrayList(), online, max);
            LanternStatusResponse response = new LanternStatusResponse(version0, server.getFavicon().orElse(null), motd, players);

            ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(Cause.of(client), client, response);
            LanternGame.get().getEventManager().post(event);

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

            String motd0 = Texts.legacy().to(motd);
            StringBuilder builder = new StringBuilder();

            if (full) {
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
                    .append(LanternGame.get().getPlatform().getImplementation().getName())
                    .append('\u0000')
                    // The motd of the server. In legacy format.
                    .append(motd0)
                    .append('\u0000')
                    .append(online)
                    .append('\u0000')
                    .append(max);
            } else {
                builder
                    .append(motd0)
                    .append('\u00A7')
                    .append(online)
                    .append('\u00A7')
                    .append(max);
            }

            byte[] data = builder.toString().getBytes(Charsets.UTF_16BE);

            ByteBuf buf1 = ctx.alloc().buffer();
            buf1.writeByte(0xff);
            buf1.writeShort(data.length);
            buf1.writeBytes(data);

            ctx.channel().pipeline().firstContext().writeAndFlush(buf1).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception ignore) {
        } finally {
            if (!legacy) {
                buf0.resetReaderIndex();
                ctx.channel().pipeline().remove(this);
                ctx.fireChannelRead(object);
            }
        }
    }

}
