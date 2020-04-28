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
package org.lanternpowered.server.network.query

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.network.SimpleRemoteConnection
import org.lanternpowered.server.util.future.thenAsync
import org.spongepowered.api.Platform
import org.spongepowered.api.event.server.query.QueryServerEvent
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.LinkedHashMap

/**
 * Class for handling UDP packets according to the minecraft server query protocol.
 *
 * @see QueryServer
 * @see [Protocol Specifications](http://wiki.vg/Query)
 */
internal class QueryHandler(
        private val queryServer: QueryServer,
        private val showPlugins: Boolean
) : SimpleChannelInboundHandler<DatagramPacket>() {

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        this.queryServer.game.logger.error("Error in query handling", cause)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket) {
        val buf = msg.content()
        if (buf.readableBytes() < 7) {
            return
        }
        val magic = buf.readUnsignedShort()
        val type = buf.readByte()
        val sessionId = buf.readInt()
        if (magic != 0xFEFD) {
            return
        }
        if (type == ACTION_HANDSHAKE) {
            handleHandshake(ctx, msg, sessionId)
        } else if (type == ACTION_STATS) {
            if (buf.readableBytes() < 4) {
                return
            }
            val token = buf.readInt()
            if (this.queryServer.verifyChallengeToken(msg.sender(), token)) {
                if (buf.readableBytes() == 4) {
                    handleFullStats(ctx, msg, sessionId)
                } else {
                    handleBasicStats(ctx, msg, sessionId)
                }
            }
        }
    }

    private fun handleHandshake(ctx: ChannelHandlerContext, packet: DatagramPacket, sessionId: Int) {
        val challengeToken = this.queryServer.generateChallengeToken(packet.sender())
        val out = ctx.alloc().buffer()
        out.writeByte(ACTION_HANDSHAKE.toInt())
        out.writeInt(sessionId)
        out.writeString(challengeToken.toString())
        ctx.write(DatagramPacket(out, packet.sender()))
    }

    private fun handleBasicStats(ctx: ChannelHandlerContext, packet: DatagramPacket, sessionId: Int) {
        val sender = packet.sender()
        LanternGame.syncScheduler.submit {
            val event = createBasicEvent(ctx.channel())
            EventManager.post(event)
            event
        }.thenAsync(ctx.channel().eventLoop()) { event ->
            val buf = ctx.alloc().buffer()
            buf.write(event, sessionId)
            ctx.write(DatagramPacket(buf, sender))
        }
    }

    private fun createBasicEvent(channel: Channel): QueryServerEvent.Basic {
        val game = this.queryServer.game
        val server = game.server
        val connection = SimpleRemoteConnection(channel.remoteAddress() as InetSocketAddress, null)
        val cause = causeOf(connection)
        val address = channel.localAddress() as InetSocketAddress

        return LanternEventFactory.createQueryServerEventBasic(cause,
                address, "SMP", worldName, server.motd.toPlain(),
                server.maxPlayers, Int.MAX_VALUE, server.onlinePlayers.size, 0)
    }

    private fun ByteBuf.write(event: QueryServerEvent.Basic, sessionId: Int) {
        writeByte(ACTION_STATS.toInt())
        writeInt(sessionId)
        writeString(event.motd)
        writeString(event.gameType)
        writeString(event.map)
        writeString(event.playerCount.toString())
        writeString(event.maxPlayerCount.toString())
        writeShortLE(event.address.port)
        writeString(event.address.hostString)
    }

    private fun handleFullStats(ctx: ChannelHandlerContext, packet: DatagramPacket, sessionId: Int) {
        val sender = packet.sender()
        LanternGame.syncScheduler.submit {
            val event = createFullEvent(ctx.channel())
            EventManager.post(event)
            event
        }.thenAsync(ctx.channel().eventLoop()) { event ->
            val buf = ctx.alloc().buffer()
            buf.write(event, sessionId)
            ctx.write(DatagramPacket(buf, sender))
        }
    }

    private fun createFullEvent(channel: Channel): QueryServerEvent.Full {
        val game = this.queryServer.game
        val server = game.server
        val platform: Platform = game.platform
        val api = platform.getContainer(Platform.Component.API)
        val impl = platform.getContainer(Platform.Component.IMPLEMENTATION)
        val mc = platform.getContainer(Platform.Component.GAME)
        val plugins = StringBuilder()
                .append(impl.name)
                .append(" ")
                .append(impl.version)
                .append(" on ")
                .append(api.name)
                .append(" ")
                .append(api.version)
        if (this.showPlugins) {
            val containers = game.pluginManager.plugins.toMutableList()
            containers.remove(api)
            containers.remove(impl)
            containers.remove(mc)
            var delim = ':'
            for (plugin in containers) {
                plugins.append(delim).append(' ').append(plugin.name)
                delim = ';'
            }
        }
        val playerNames = server.onlinePlayers.map { it.name }
        val connection = SimpleRemoteConnection(channel.remoteAddress() as InetSocketAddress, null)
        val cause = causeOf(connection)
        val address = channel.localAddress() as InetSocketAddress

        return LanternEventFactory.createQueryServerEventFull(cause, address, mutableMapOf<String, String>(),
                "MINECRAFT", "SMP", worldName, server.motd.toPlain(), playerNames, plugins.toString(),
                mc.version.orElse("unknown"), server.maxPlayers, Int.MAX_VALUE, playerNames.size, 0)
    }

    private fun ByteBuf.write(event: QueryServerEvent.Full, sessionId: Int) {
        val data: MutableMap<String, String> = LinkedHashMap()
        data["hostname"] = event.motd
        data["gametype"] = event.gameType
        data["game_id"] = event.gameId
        data["version"] = event.version
        data["plugins"] = event.plugins
        data["map"] = event.map
        data["numplayers"] = event.playerCount.toString()
        data["maxplayers"] = event.maxPlayerCount.toString()
        data["hostport"] = event.address.port.toString()
        data["hostip"] = event.address.hostString
        event.customValuesMap.entries.stream()
                .filter { entry -> !data.containsKey(entry.key) }
                .forEach { entry -> data[entry.key] = entry.value }
        writeByte(ACTION_STATS.toInt())
        writeInt(sessionId)
        // constant: splitnum\x00\x80\x00
        writeBytes(byteArrayOf(0x73, 0x70, 0x6C, 0x69, 0x74, 0x6E, 0x75, 0x6D, 0x00, 0x80.toByte(), 0x00))
        for ((key, value) in data) {
            writeString(key)
            writeString(value)
        }
        writeByte(0)
        // constant: \x01player_\x00\x00
        writeBytes(byteArrayOf(0x01, 0x70, 0x6C, 0x61, 0x79, 0x65, 0x72, 0x5F, 0x00, 0x00))
        for (player in event.players) {
            writeString(player)
        }
        writeByte(0)
    }

    private val worldName: String
        get() {
            val worlds = this.queryServer.game.server.worlds
            return if (worlds.isNotEmpty()) worlds.iterator().next().getProperties().directoryName else "none"
        }

    companion object {

        private const val ACTION_HANDSHAKE: Byte = 9
        private const val ACTION_STATS: Byte = 0

        private fun ByteBuf.writeString(str: String) {
            writeBytes(str.toByteArray(StandardCharsets.UTF_8)).writeByte(0)
        }
    }
}
