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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.query;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.SimpleRemoteConnection;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.server.query.QueryServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Class for handling UDP packets according to the minecraft server query protocol.
 * @see QueryServer
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final byte ACTION_HANDSHAKE = 9;
    private static final byte ACTION_STATS = 0;

    // The {@link QueryServer} this handler belongs to
    private QueryServer queryServer;

    // Whether the a plugin list should be included in responses
    private boolean showPlugins;

    QueryHandler(QueryServer queryServer, boolean showPlugins) {
        this.queryServer = queryServer;
        this.showPlugins = showPlugins;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.queryServer.getGame().getLogger().error("Error in query handling", cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        final ByteBuf buf = msg.content();
        if (buf.readableBytes() < 7) {
            return;
        }

        final int magic = buf.readUnsignedShort();
        final byte type = buf.readByte();
        final int sessionId = buf.readInt();

        if (magic != 0xFEFD) {
            return;
        }

        if (type == ACTION_HANDSHAKE) {
            handleHandshake(ctx, msg, sessionId);
        } else if (type == ACTION_STATS) {
            if (buf.readableBytes() < 4) {
                return;
            }
            final int token = buf.readInt();
            if (this.queryServer.verifyChallengeToken(msg.sender(), token)) {
                if (buf.readableBytes() == 4) {
                    this.handleFullStats(ctx, msg, sessionId);
                } else {
                    this.handleBasicStats(ctx, msg, sessionId);
                }
            }
        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        int challengeToken = queryServer.generateChallengeToken(packet.sender());
        ByteBuf out = ctx.alloc().buffer();
        out.writeByte(ACTION_HANDSHAKE);
        out.writeInt(sessionId);
        writeString(out, String.valueOf(challengeToken));
        ctx.write(new DatagramPacket(out, packet.sender()));
    }

    private void handleBasicStats(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        LanternServer server = this.queryServer.getGame().getServer();

        // TODO: Find out how to support the size and max size properties
        final Cause cause = Cause.of(EventContext.empty(),
                new SimpleRemoteConnection((InetSocketAddress) ctx.channel().remoteAddress(), null));
        final QueryServerEvent.Basic event = SpongeEventFactory.createQueryServerEventBasic(cause,
                (InetSocketAddress) ctx.channel().localAddress(), "SMP", this.getWorldName(), server.getMotd().toPlain(),
                server.getMaxPlayers(), Integer.MAX_VALUE, server.getOnlinePlayers().size(), 0);
        Sponge.getEventManager().post(event);

        final InetSocketAddress address = event.getAddress();

        final ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(ACTION_STATS);
        buf.writeInt(sessionId);
        writeString(buf, event.getMotd());
        writeString(buf, event.getGameType());
        writeString(buf, event.getMap());
        writeString(buf, String.valueOf(event.getPlayerCount()));
        writeString(buf, String.valueOf(event.getMaxPlayerCount()));
        buf.writeShortLE(address.getPort());
        writeString(buf, address.getHostString());
        ctx.write(new DatagramPacket(buf, packet.sender()));
    }

    private void handleFullStats(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        final LanternGame game = this.queryServer.getGame();
        final LanternServer server = game.getServer();
        final Platform platform = game.getPlatform();

        final PluginContainer api = platform.getContainer(Platform.Component.API);
        final PluginContainer impl = platform.getContainer(Platform.Component.IMPLEMENTATION);
        final PluginContainer mc = platform.getContainer(Platform.Component.GAME);

        final StringBuilder plugins = new StringBuilder()
                .append(impl.getName())
                .append(" ")
                .append(impl.getVersion())
                .append(" on ")
                .append(api.getName())
                .append(" ")
                .append(api.getVersion());

        if (this.showPlugins) {
            final List<PluginContainer> containers = new ArrayList<>(game.getPluginManager().getPlugins());
            containers.remove(api);
            containers.remove(impl);
            containers.remove(mc);

            char delim = ':';
            for (PluginContainer plugin : containers) {
                plugins.append(delim).append(' ').append(plugin.getName());
                delim = ';';
            }
        }

        final List<String> playerNames = server.getOnlinePlayers()
                .stream().map(Player::getName).collect(Collectors.toList());
        final Cause cause = Cause.of(EventContext.empty(),
                new SimpleRemoteConnection((InetSocketAddress) ctx.channel().remoteAddress(), null));

        final QueryServerEvent.Full event = SpongeEventFactory.createQueryServerEventFull(cause,
                (InetSocketAddress) ctx.channel().localAddress(), new HashMap<>(),
                "MINECRAFT", "SMP", getWorldName(), server.getMotd().toPlain(), playerNames, plugins.toString(),
                mc.getVersion().orElse("unknown"), server.getMaxPlayers(), Integer.MAX_VALUE, playerNames.size(), 0);
        final InetSocketAddress address = event.getAddress();

        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("hostname", event.getMotd());
        data.put("gametype", event.getGameType());
        data.put("game_id", event.getGameId());
        data.put("version", event.getVersion());
        data.put("plugins", event.getPlugins());
        data.put("map", event.getMap());
        data.put("numplayers", event.getPlayerCount());
        data.put("maxplayers", event.getMaxPlayerCount());
        data.put("hostport", address.getPort());
        data.put("hostip", address.getHostString());
        event.getCustomValuesMap().entrySet().stream().filter(entry -> !data.containsKey(entry.getKey()))
                .forEach(entry -> data.put(entry.getKey(), entry.getValue()));

        final ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(ACTION_STATS);
        buf.writeInt(sessionId);
        // constant: splitnum\x00\x80\x00
        buf.writeBytes(new byte[] { 0x73, 0x70, 0x6C, 0x69, 0x74, 0x6E, 0x75, 0x6D, 0x00, (byte) 0x80, 0x00 });
        for (Entry<String, Object> e : data.entrySet()) {
            writeString(buf, e.getKey());
            writeString(buf, String.valueOf(e.getValue()));
        }
        buf.writeByte(0);
        // constant: \x01player_\x00\x00
        buf.writeBytes(new byte[] { 0x01, 0x70, 0x6C, 0x61, 0x79, 0x65, 0x72, 0x5F, 0x00, 0x00 });
        for (Player player : game.getServer().getOnlinePlayers()) {
            writeString(buf, player.getName());
        }
        buf.writeByte(0);
        ctx.write(new DatagramPacket(buf, packet.sender()));
    }

    private String getWorldName() {
        final Collection<World> worlds = this.queryServer.getGame().getServer().getWorlds();
        if (!worlds.isEmpty()) {
            return worlds.iterator().next().getName();
        }
        return "none";
    }

    private static void writeString(ByteBuf out, String str) {
        out.writeBytes(str.getBytes(StandardCharsets.UTF_8)).writeByte(0);
    }
}
