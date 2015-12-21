/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.network.status;

import org.spongepowered.api.entity.living.player.Player;

import org.spongepowered.api.plugin.PluginContainer;
import com.google.common.collect.Lists;
import org.spongepowered.api.Platform;
import org.spongepowered.api.world.World;
import org.spongepowered.api.text.Texts;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.LanternGame;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class for handling UDP packets according to the minecraft server query protocol.
 * @see QueryServer
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
public class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final byte ACTION_HANDSHAKE = 9;
    private static final byte ACTION_STATS = 0;

    // The {@link QueryServer} this handler belongs to
    private QueryServer queryServer;

    // Whether the a plugin list should be included in responses
    private boolean showPlugins;

    public QueryHandler(QueryServer queryServer, boolean showPlugins) {
        this.queryServer = queryServer;
        this.showPlugins = showPlugins;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.queryServer.getGame().getLogger().error("Error in query handling", cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf buf = msg.content();
        if (buf.readableBytes() < 7) {
            return;
        }

        int magic = buf.readUnsignedShort();
        byte type = buf.readByte();
        int sessionId = buf.readInt();

        if (magic != 0xFEFD) {
            return;
        }

        if (type == ACTION_HANDSHAKE) {
            handleHandshake(ctx, msg, sessionId);
        } else if (type == ACTION_STATS) {
            if (buf.readableBytes() < 4) {
                return;
            }
            int token = buf.readInt();
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

        ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(ACTION_STATS);
        buf.writeInt(sessionId);
        writeString(buf, Texts.toPlain(server.getMotd()));
        writeString(buf, "SMP");
        writeString(buf, this.getWorldName());
        writeString(buf, String.valueOf(server.getOnlinePlayers().size()));
        writeString(buf, String.valueOf(server.getMaxPlayers()));
        buf.order(ByteOrder.LITTLE_ENDIAN).writeShort(this.getPort());
        writeString(buf, this.getIp());
        ctx.write(new DatagramPacket(buf, packet.sender()));
    }

    private void handleFullStats(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        LanternGame game = this.queryServer.getGame();
        Platform platform = game.getPlatform();

        StringBuilder plugins = new StringBuilder()
                .append(platform.getImplementation().getName())
                .append(" ")
                .append(platform.getImplementation().getVersion())
                .append(" on ")
                .append(platform.getApi().getName())
                .append(" ")
                .append(platform.getApi().getVersion());
        List<PluginContainer> containers = Lists.newArrayList(game.getPluginManager().getPlugins());
        containers.remove(platform.getApi());
        containers.remove(platform.getImplementation());
        containers.remove(game.getPlugin());

        if (this.showPlugins) {
            char delim = ':';
            for (PluginContainer plugin : containers) {
                plugins.append(delim).append(' ').append(plugin.getName());
                delim = ';';
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("hostname", Texts.toPlain(game.getServer().getMotd()));
        data.put("gametype", "SMP");
        data.put("game_id", "MINECRAFT");
        data.put("version", game.getPlugin().getVersion());
        data.put("plugins", plugins);
        data.put("map", this.getWorldName());
        data.put("numplayers", game.getServer().getOnlinePlayers().size());
        data.put("maxplayers", game.getServer().getMaxPlayers());
        data.put("hostport", this.getPort());
        data.put("hostip", this.getIp());

        ByteBuf buf = ctx.alloc().buffer();
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

    private int getPort() {
        return this.queryServer.getGame().getGlobalConfig().getServerPort();
    }

    private String getIp() {
        final String ip = this.queryServer.getGame().getGlobalConfig().getServerIp();
        return ip.isEmpty() ? "127.0.0.1" : ip;
    }

    private static void writeString(ByteBuf out, String str) {
        out.writeBytes(str.getBytes(StandardCharsets.UTF_8)).writeByte(0);
    }
}
